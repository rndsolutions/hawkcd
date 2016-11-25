/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hawkcd.agent.components.taskexecutor.executors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import io.hawkcd.agent.AgentConfiguration;
import io.hawkcd.agent.components.taskexecutor.TaskExecutor;
import io.hawkcd.agent.constants.ConfigConstants;
import io.hawkcd.agent.enums.TaskStatus;
import io.hawkcd.agent.models.Task;
import io.hawkcd.agent.models.TaskDefinition;
import io.hawkcd.agent.models.UploadArtifactTask;
import io.hawkcd.agent.models.payload.UploadArtifactInfo;
import io.hawkcd.agent.models.payload.WorkInfo;
import io.hawkcd.agent.services.FileManagementService;
import io.hawkcd.agent.services.interfaces.IFileManagementService;
import io.hawkcd.agent.utilities.ReportAppender;
import io.hawkcd.agent.utilities.deserializers.TaskDefinitionAdapter;
import org.apache.commons.io.FileUtils;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

        String pathToFile = AgentConfiguration.getInstallInfo().getAgentSandbox() + File.separator + "Pipelines" + File.separator + workInfo.getPipelineDefinitionName() + File.separator + taskDefinition.getSource();
//        if (!taskDefinition.getSource().isEmpty()) {
//            pathToFile = pathToFile + File.separator + taskDefinition.getSource();
//        }

        String fullPath = this.fileManagementService.pathCombine(pathToFile);
        String rootPath = this.fileManagementService.getRootPath(fullPath);
        String wildCardPattern = this.fileManagementService.getPattern(rootPath, fullPath);

        if (rootPath.isEmpty()) {
            return this.nullProcessing(report, task, String.format("%s is Non-existent source.", taskDefinition.getSource()));
        }

        List<File> files = this.fileManagementService.getFiles(fullPath, wildCardPattern);

        if (files.size() == 0) {
            return this.nullProcessing(report, task, String.format("Error in getting files in %s", fullPath));
        }

        File zipFile = this.fileManagementService.generateUniqueFile(AgentConfiguration.getInstallInfo().getAgentTempDirectoryPath(), "zip");

        String errorMessage = this.fileManagementService.zipFiles(zipFile.getPath(), files, rootPath, true);

        if (errorMessage != null) {
            zipFile.delete();
            return this.nullProcessing(report, task, "Error occurred in zipping files!");
        }

        String executionFolder = String.valueOf(workInfo.getPipelineExecutionID());
        UploadArtifactInfo uploadArtifactInfo = new UploadArtifactInfo(zipFile, taskDefinition.getDestination());
        String folderPath = String.format(ConfigConstants.SERVER_CREATE_ARTIFACT_API_ADDRESS, workInfo.getPipelineDefinitionName(), executionFolder);
        AgentConfiguration.getInstallInfo().setCreateArtifactApiAddress(String.format("%s/%s", AgentConfiguration.getInstallInfo().getServerAddress(), folderPath));

        String destination = null;
        try {
            destination = URLEncoder.encode(taskDefinition.getDestination(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }

        String requestSource = this.fileManagementService.urlCombine(AgentConfiguration.getInstallInfo().getCreateArtifactApiAddress()) + "/upload-artifact" + "?destination=" + destination;
        WebResource webResource = this.restClient.resource(requestSource);
        InputStream targetStream = null;
        try {
            targetStream = FileUtils.openInputStream(zipFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientResponse response = webResource
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)
                .post(ClientResponse.class, targetStream);

        if (response.getStatus() != 200) {
            zipFile.delete();
            return this.nullProcessing(report, task, String.format("Error occurred in server response! Returned status code: %s", response.getStatus()));
        }

        zipFile.delete();

        this.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());

        return task;
    }
}
