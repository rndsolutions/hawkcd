package net.hawkengine.agent.components.taskexecutor.executors;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.components.taskexecutor.TaskExecutor;
import net.hawkengine.agent.constants.LoggerMessages;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.FetchArtifactTask;
import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.payload.TaskExecutionInfo;
import net.hawkengine.agent.services.FileManagementService;
import net.hawkengine.agent.services.interfaces.IFileManagementService;
import net.hawkengine.model.payload.WorkInfo;

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

        TaskExecutionInfo taskExecutionInfo = new TaskExecutionInfo();
        taskExecutionInfo.setTaskId(taskDefinition.getId());

        super.updateTask(task, TaskStatus.PASSED, LocalDateTime.now(), null);

        report.append(String.format("%s pipeline=%s stage=%s job=%s source=%s destination=%s",
                taskDefinition.getType(),
                taskDefinition.getPipeline(),
                taskDefinition.getStage(),
                taskDefinition.getJob(),
                taskDefinition.getSource(),
                taskDefinition.getDestination()));

        String requestSource = this.fileManagementService.urlCombine(AgentConfiguration.getInstallInfo().getFetchArtifactApiAddress(),
                taskDefinition.getPipeline(),
//                Integer.toString(report.getPipelineExecutionId()),
                taskDefinition.getStage(),
//                Integer.toString(report.getStageExecutionId()),
                taskDefinition.getJob(),
                taskDefinition.getSource());

        WebResource webResource = this.restClient.resource(requestSource);
        ClientResponse response = webResource.get(ClientResponse.class);

        if ((response.getStatus() != 200) || (response.getEntityInputStream() == null)) {
            super.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());

            report.append(String.format("Could not get resource. TaskStatus code %d", response.getStatus()));
            LOGGER.debug(String.format("Could not get resource. TaskStatus code %d", response.getStatus()));
            return task;
        }

        String errorMessage;

        String filePath = Paths.get(AgentConfiguration.getInstallInfo().getAgentTempDirectoryPath(), UUID.randomUUID() + ".zip").toString();

        errorMessage = this.fileManagementService.streamToFile(response.getEntityInputStream(), filePath);

        if (errorMessage != null) {
            super.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());

            report.append("Error occurred");
            LOGGER.error(String.format(LoggerMessages.TASK_THROWS_EXCEPTION, task.getTaskDefinition().getId(), errorMessage));
            return task;
        }

        String destination = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDirectoryPath(), taskDefinition.getPipeline(), taskDefinition.getDestination()).toString();
        errorMessage = this.fileManagementService.unzipFile(filePath, destination);
        this.fileManagementService.deleteFile(filePath);

        if (errorMessage != null) {
            super.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());

            report.append("Error occurred");
            LOGGER.error(String.format(LoggerMessages.TASK_THROWS_EXCEPTION, task.getTaskDefinition().getId(), errorMessage));
            return task;
        }

        report.append(String.format("Saved artifact to %s after verifying the integrity of its contents.", destination));
        super.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());

        return task;
    }
}
