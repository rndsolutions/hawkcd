package net.hawkengine.agent.components.taskexecutor.executors;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.components.taskexecutor.TaskExecutor;
import net.hawkengine.agent.constants.LoggerMessages;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.UploadArtifactTask;
import net.hawkengine.agent.services.FileManagementService;
import net.hawkengine.agent.services.interfaces.IFileManagementService;
import net.hawkengine.agent.models.payload.WorkInfo;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.time.LocalDateTime;

public class UploadArtifactExecutor extends TaskExecutor {

    private Client restClient;
    private IFileManagementService fileManagementService;

    public UploadArtifactExecutor() {
        this.restClient = Client.create();
        this.fileManagementService = new FileManagementService();
    }

    public IFileManagementService getFileManagementService() {
        return fileManagementService;
    }

    public void setFileManagementService(IFileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
    }

    public Client getRestClient() {
        return restClient;
    }

    public void setRestClient(Client restClient) {
        this.restClient = restClient;
    }

    @Override
    public Task executeTask(Task task, StringBuilder report, WorkInfo workInfo) {

        UploadArtifactTask taskDefinition = (UploadArtifactTask) task.getTaskDefinition();

        report.append(String.format("Start uploading artifact source: %s destination: %s", taskDefinition.getSource(), taskDefinition.getDestination()));
        this.updateTask(task, TaskStatus.PASSED, LocalDateTime.now(), null);

        String fullPath = this.fileManagementService.pathCombine(AgentConfiguration.getInstallInfo().getAgentPipelinesDirectoryPath(), "pipelineName", taskDefinition.getSource());
        String rootPath = this.fileManagementService.getRootPath(fullPath);
        String wildCardPattern = this.fileManagementService.getPattern(rootPath, fullPath);

        if (rootPath.isEmpty()) {
            this.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());

            report.append(String.format("%s is Nonexistent source.", taskDefinition.getSource()));
            LOGGER.error("Nonexistent source.");

            return task;
        }

        File[] files = this.fileManagementService.getFiles(rootPath, wildCardPattern);

        if (files == null) {
            this.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());

            report.append(String.format("%s is Nonexistent source.", taskDefinition.getSource()));
            LOGGER.error("Nonexistent source.");

            return task;
        }

        File zipFile = this.fileManagementService.generateUniqueFile(AgentConfiguration.getInstallInfo().getAgentTempDirectoryPath(), "zip");

        String errorMessage = this.fileManagementService.zipFiles(zipFile.getPath(), files, rootPath, false);

        if (errorMessage != null) {
            zipFile.delete();
            this.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());

            report.append("Error occurred");
            LOGGER.error(String.format(LoggerMessages.TASK_THROWS_EXCEPTION, task.getTaskDefinition().getId(), errorMessage));

            return task;
        }

//        String requestSource = this.fileManagementService.urlCombine(AgentConfiguration.getInstallInfo().getCreateArtifactApiAddress(),
//                report.getPipelineDefinitionName(),
//                Integer.toString(report.getPipelineExecutionId()),
//                report.getStageDefinitionName(),
//                Integer.toString(report.getStageExecutionId()),
//                report.getJobName(),
//                taskDefinition.getDestination(), zipFile.getName());

        WebResource webResource = this.restClient.resource("requestSource");
        webResource.type(MediaType.MULTIPART_FORM_DATA);
        ClientResponse response = webResource.post(ClientResponse.class, zipFile);

        if (response.getStatus() != 200) {
            zipFile.delete();
            this.updateTask(task, TaskStatus.FAILED, null, LocalDateTime.now());

            report.append("Error occurred");
            LOGGER.debug(String.format("Could not get resource. TaskStatus code %d", response.getStatus()));

            return task;
        }

        zipFile.delete();

        this.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());

        return task;
    }
}
