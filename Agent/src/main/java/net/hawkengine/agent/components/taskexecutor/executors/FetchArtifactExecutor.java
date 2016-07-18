package net.hawkengine.agent.components.taskexecutor.executors;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.components.taskexecutor.TaskExecutor;
import net.hawkengine.agent.constants.Constants;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.FetchArtifactTask;
import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.payload.WorkInfo;
import net.hawkengine.agent.services.FileManagementService;
import net.hawkengine.agent.services.interfaces.IFileManagementService;

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
        this.setRestClient(client.create());
        this.setFileManagementService(fileManagementService);
    }

    public Client getRestClient() {
        return this.restClient;
    }

    public void setRestClient(Client restClient) {
        this.restClient = restClient;
    }

    public IFileManagementService getFileManagementService() {
        return this.fileManagementService;
    }

    public void setFileManagementService(IFileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
    }

    @Override
    public Task executeTask(Task task, StringBuilder report, WorkInfo workInfo) {

        FetchArtifactTask taskDefinition = (FetchArtifactTask) task.getTaskDefinition();
        super.updateTask(task, TaskStatus.PASSED, LocalDateTime.now(), null);

        report.append(String.format("%s pipeline=%s stage=%s job=%s source=%s destination=%s",
                taskDefinition.getType(),
                taskDefinition.getPipeline(),
                taskDefinition.getStage(),
                taskDefinition.getJob(),
                taskDefinition.getSource(),
                taskDefinition.getDestination()));

        String folderPath = String.format(Constants.SERVER_CREATE_ARTIFACT_API_ADDRESS, workInfo.getPipelineDefinitionName(), workInfo.getStageDefinitionName(), workInfo.getJobDefinitionName());
        AgentConfiguration.getInstallInfo().setCreateArtifactApiAddress(String.format("%s/%s", AgentConfiguration.getInstallInfo().getServerAddress(), folderPath));

        String requestSource = this.fileManagementService.urlCombine(AgentConfiguration.getInstallInfo().getCreateArtifactApiAddress()) + "/fetch-artifact";
        WebResource webResource = this.restClient.resource(requestSource);
        ClientResponse response = webResource.type("application/json").post(ClientResponse.class, taskDefinition.getSource());

        if ((response.getStatus() != 200)) {
           return this.nullProcessing(report,task,String.format("Could not get resource. TaskStatus code %s",response.getStatus()));
        }

        if(response.getEntityInputStream() == null){
            return this.nullProcessing(report,task,"Could not get resource. Input stream is null");
        }

        String errorMessage;

        String filePath = Paths.get(AgentConfiguration.getInstallInfo().getAgentTempDirectoryPath(), UUID.randomUUID() + ".zip").toString();
        File fetchArtifactDir = new File(filePath);
        this.fileManagementService.generateDirectory(fetchArtifactDir);
        errorMessage = this.fileManagementService.initiateFile(fetchArtifactDir, response.getEntityInputStream(), filePath);

        if (errorMessage != null) {
           return this.nullProcessing(report,task,"Error occurred in creating the artifact!");
        }

        String destination = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDirectoryPath(), taskDefinition.getPipeline(), taskDefinition.getStage(), taskDefinition.getJob()).toString();
        errorMessage = this.fileManagementService.unzipFile(filePath, destination);
        filePath = Paths.get(AgentConfiguration.getInstallInfo().getAgentTempDirectoryPath()).toString();
        String deleteMessage = this.fileManagementService.deleteFilesInDirectory(filePath);

        if (errorMessage != null) {
                return this.nullProcessing(report,task,"Error occurred in unzipping files!");
        }

        if (deleteMessage != null) {
            return this.nullProcessing(report, task, "Error occurred in deleting files!");
        }

        report.append(System.getProperty("line.separator"));
        report.append(String.format("Saved artifact to %s after verifying the integrity of its contents.", destination));
        super.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());

        return task;
    }
}