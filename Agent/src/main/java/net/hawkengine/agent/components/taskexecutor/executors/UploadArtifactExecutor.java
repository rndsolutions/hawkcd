package net.hawkengine.agent.components.taskexecutor.executors;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.components.taskexecutor.TaskExecutor;
import net.hawkengine.agent.constants.ConfigConstants;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.TaskDefinition;
import net.hawkengine.agent.models.UploadArtifactTask;
import net.hawkengine.agent.models.payload.UploadArtifactInfo;
import net.hawkengine.agent.models.payload.WorkInfo;
import net.hawkengine.agent.services.FileManagementService;
import net.hawkengine.agent.services.interfaces.IFileManagementService;
import net.hawkengine.agent.utilities.ReportAppender;
import net.hawkengine.agent.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.model.MaterialDefinition;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class UploadArtifactExecutor extends TaskExecutor {
    private Client restClient;
    private IFileManagementService fileManagementService;
    private Gson jsonConverter;


    public UploadArtifactExecutor() {
        this.restClient = Client.create();
        this.fileManagementService = new FileManagementService();
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .create();
    }

    public UploadArtifactExecutor(Client client, IFileManagementService fileManagementService) {
        this.restClient = client.create();
        this.fileManagementService = fileManagementService;
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .create();
    }

    @Override
    public Task executeTask(Task task, StringBuilder report, WorkInfo workInfo) {
        UploadArtifactTask taskDefinition = (UploadArtifactTask) task.getTaskDefinition();

        this.updateTask(task, TaskStatus.PASSED, LocalDateTime.now(), null);

        String uploadingMessage = String.format("Start uploading artifact source: %s ", taskDefinition.getSource());
        LOGGER.debug(uploadingMessage);
        ReportAppender.appendInfoMessage(uploadingMessage, report);

        String fullPath = this.fileManagementService.pathCombine(taskDefinition.getSource());
        String rootPath = this.fileManagementService.getRootPath(fullPath);
        String wildCardPattern = this.fileManagementService.getPattern(rootPath, fullPath);

        if (rootPath.isEmpty()) {
            return this.nullProcessing(report, task, String.format("%s is Non-existent source.", taskDefinition.getSource()));
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

        String executionFolder = "Run" + workInfo.getPipelineExecutionID();
        UploadArtifactInfo uploadArtifactInfo = new UploadArtifactInfo(zipFile, taskDefinition.getDestination());
        String uploadArtifactInfoAsString = this.jsonConverter.toJson(uploadArtifactInfo);
        String folderPath = String.format(ConfigConstants.SERVER_CREATE_ARTIFACT_API_ADDRESS, workInfo.getPipelineDefinitionName(), executionFolder);
        AgentConfiguration.getInstallInfo().setCreateArtifactApiAddress(String.format("%s/%s", AgentConfiguration.getInstallInfo().getServerAddress(), folderPath));
        String requestSource = this.fileManagementService.urlCombine(AgentConfiguration.getInstallInfo().getCreateArtifactApiAddress()) + "/upload-artifact";
        WebResource webResource = this.restClient.resource(requestSource);
        ClientResponse response = webResource.type("application/json").post(ClientResponse.class, uploadArtifactInfoAsString);

        if (response.getStatus() != 200) {
            zipFile.delete();
            return this.nullProcessing(report, task, String.format("Error occurred in server response! Returned status code: %s", response.getStatus()));
        }

        zipFile.delete();

        this.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());

        return task;
    }
}
