package net.hawkengine.agent.components.taskexecutor.executors;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.xml.internal.ws.api.server.SDDocument;
import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.components.taskexecutor.TaskExecutor;
import net.hawkengine.agent.constants.ConfigConstants;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.FetchArtifactTask;
import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.payload.WorkInfo;
import net.hawkengine.agent.services.FileManagementService;
import net.hawkengine.agent.services.interfaces.IFileManagementService;
import net.hawkengine.agent.utilities.ReportAppender;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

public class FetchArtifactExecutor extends TaskExecutor {

    private Client restClient;
    private IFileManagementService fileManagementService;

    public FetchArtifactExecutor() {
        this.restClient = Client.create();
        this.fileManagementService = new FileManagementService();
    }

    public FetchArtifactExecutor(Client client, IFileManagementService fileManagementService) {
        this.restClient = client.create();
        this.fileManagementService = fileManagementService;
    }

    @Override
    public Task executeTask(Task task, StringBuilder report, WorkInfo workInfo) {
        FetchArtifactTask taskDefinition = (FetchArtifactTask) task.getTaskDefinition();
        super.updateTask(task, TaskStatus.PASSED, LocalDateTime.now(), null);

        if ((taskDefinition.getPipelineExecutionId() == null) || (taskDefinition.getPipelineExecutionId().isEmpty())){
            return this.nullProcessing(report, task, "Error occurred in getting pipeline execution ID!");
        }
        if ((taskDefinition.getPipelineDefinitionName() == null) || (taskDefinition.getPipelineDefinitionName().isEmpty())){
            return this.nullProcessing(report, task, "Error occurred in getting pipeline name!");
        }

        String fetchingMessage = String.format("Start fetching artifact source:  %s\\%s\\%s",
                taskDefinition.getPipelineDefinitionName(),
                taskDefinition.getPipelineExecutionId(),
                taskDefinition.getSource());
        LOGGER.debug(fetchingMessage);
        ReportAppender.appendInfoMessage(fetchingMessage, report);

        String folderPath = String.format(ConfigConstants.SERVER_CREATE_ARTIFACT_API_ADDRESS, workInfo.getPipelineDefinitionName(), workInfo.getPipelineExecutionID());
        AgentConfiguration.getInstallInfo().setCreateArtifactApiAddress(String.format("%s/%s", AgentConfiguration.getInstallInfo().getServerAddress(), folderPath));

        String requestSource = this.fileManagementService.urlCombine(AgentConfiguration.getInstallInfo().getCreateArtifactApiAddress()) + "/fetch-artifact";
        WebResource webResource = this.restClient.resource(requestSource);
        String source = taskDefinition.getPipelineDefinitionName() + File.separator + taskDefinition.getPipelineExecutionId() + File.separator + taskDefinition.getSource();
        ClientResponse response = webResource.type("application/json").post(ClientResponse.class, source);

        if ((response.getStatus() != 200)) {
            return this.nullProcessing(report, task, String.format("Could not get resource. TaskStatus code %s", response.getStatus()));
        }

        if (response.getEntityInputStream() == null) {
            return this.nullProcessing(report, task, "Could not get resource. Input stream is null");
        }

        String filePath = Paths.get(AgentConfiguration.getInstallInfo().getAgentTempDirectoryPath(), UUID.randomUUID() + ".zip").toString();
        File fetchArtifactDir = new File(filePath);
        this.fileManagementService.generateDirectory(fetchArtifactDir);

        String errorMessage;
        errorMessage = this.fileManagementService.initiateFile(fetchArtifactDir, response.getEntityInputStream(), filePath);
        if (errorMessage != null) {
            return this.nullProcessing(report, task, "Error occurred in creating the artifact!");
        }
        String destination;
        if (taskDefinition.getDestination() != null){
            destination = String.valueOf(Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDir() + File.separator + taskDefinition.getPipelineDefinitionName(), taskDefinition.getDestination()));
        } else {
            destination = String.valueOf(Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDir() + File.separator + taskDefinition.getPipelineDefinitionName()));
        }
            errorMessage = this.fileManagementService.unzipFile(filePath, destination);
        filePath = Paths.get(AgentConfiguration.getInstallInfo().getAgentTempDirectoryPath()).toString();
        String deleteMessage = this.fileManagementService.deleteFilesInDirectory(filePath);

        if (errorMessage != null) {
            return this.nullProcessing(report, task, "Error occurred in unzipping files!");
        }

        if (deleteMessage != null) {
            return this.nullProcessing(report, task, "Error occurred in deleting files!");
        }

        super.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());

        String fetchedMessage = String.format("Saved artifact to %s after verifying the integrity of its contents.", destination);
        LOGGER.debug(fetchedMessage);
        ReportAppender.appendInfoMessage(fetchedMessage, report);

        return task;
    }
}