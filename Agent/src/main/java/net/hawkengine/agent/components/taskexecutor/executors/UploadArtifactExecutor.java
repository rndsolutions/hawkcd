package net.hawkengine.agent.components.taskexecutor.executors;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.components.taskexecutor.TaskExecutor;
import net.hawkengine.agent.constants.ConfigConstants;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.UploadArtifactTask;
import net.hawkengine.agent.models.payload.WorkInfo;
import net.hawkengine.agent.services.FileManagementService;
import net.hawkengine.agent.services.interfaces.IFileManagementService;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class UploadArtifactExecutor extends TaskExecutor {
    private Client restClient;
    private IFileManagementService fileManagementService;

    public UploadArtifactExecutor() {
        this.restClient = Client.create();
        this.fileManagementService = new FileManagementService();
    }

    public UploadArtifactExecutor(Client client, IFileManagementService fileManagementService) {
        this.restClient = client.create();
        this.fileManagementService = fileManagementService;
    }

    @Override
    public Task executeTask(Task task, StringBuilder report, WorkInfo workInfo) {
        UploadArtifactTask taskDefinition = (UploadArtifactTask) task.getTaskDefinition();

        report.append(String.format("Start uploading artifact source: %s destination: %s", taskDefinition.getSource(), taskDefinition.getDestination()));
        this.updateTask(task, TaskStatus.PASSED, LocalDateTime.now(), null);

        String fullPath = this.fileManagementService.pathCombine(taskDefinition.getSource());
        String rootPath = this.fileManagementService.getRootPath(fullPath);
        String wildCardPattern = this.fileManagementService.getPattern(rootPath, fullPath);

        if (rootPath.isEmpty()) {
            return this.nullProcessing(report, task, String.format("%s is Nonexistent source.", taskDefinition.getSource()));
        }

        List<File> files = this.fileManagementService.getFiles(rootPath, wildCardPattern);

        if (files.size() == 0) {
            return this.nullProcessing(report, task, String.format("Error in getting files in %s", fullPath));
        }

        File zipFile = this.fileManagementService.generateUniqueFile(AgentConfiguration.getInstallInfo().getAgentTempDirectoryPath(), "zip");

        String errorMessage = this.fileManagementService.zipFiles(zipFile.getPath(), files, rootPath, false);

        if (errorMessage != null) {
            zipFile.delete();
            return this.nullProcessing(report, task, "Error occurred in zipping files!");
        }

        String folderPath = String.format(ConfigConstants.SERVER_CREATE_ARTIFACT_API_ADDRESS, workInfo.getPipelineDefinitionName(), workInfo.getStageDefinitionName(), workInfo.getJobDefinitionName());
        AgentConfiguration.getInstallInfo().setCreateArtifactApiAddress(String.format("%s/%s", AgentConfiguration.getInstallInfo().getServerAddress(), folderPath));
        String requestSource = this.fileManagementService.urlCombine(AgentConfiguration.getInstallInfo().getCreateArtifactApiAddress()) + "/upload-artifact";

        WebResource webResource = this.restClient.resource(requestSource);
        ClientResponse response = webResource.type("multipart/form-data").post(ClientResponse.class, zipFile);

        if (response.getStatus() != 200) {
            zipFile.delete();
            return this.nullProcessing(report, task, String.format("Error occurred in server response! Returned status code: %s", response.getStatus()));
        }

        zipFile.delete();

        this.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());

        return task;
    }
}
