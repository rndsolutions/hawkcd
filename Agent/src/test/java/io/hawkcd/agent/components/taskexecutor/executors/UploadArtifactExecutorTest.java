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
import junit.framework.Assert;
import io.hawkcd.agent.AgentConfiguration;
import io.hawkcd.agent.base.TestBase;
import io.hawkcd.agent.enums.TaskStatus;
import io.hawkcd.agent.enums.TaskType;
import io.hawkcd.agent.models.Job;
import io.hawkcd.agent.models.Task;
import io.hawkcd.agent.models.TaskDefinition;
import io.hawkcd.agent.models.UploadArtifactTask;
import io.hawkcd.agent.models.payload.UploadArtifactInfo;
import io.hawkcd.agent.models.payload.WorkInfo;
import io.hawkcd.agent.services.FileManagementService;
import io.hawkcd.agent.services.interfaces.IFileManagementService;
import io.hawkcd.agent.utilities.deserializers.TaskDefinitionAdapter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Client.class, WebResource.class})
public class UploadArtifactExecutorTest extends TestBase {

    private Task correctUploadArtifactTask;
    private Client mockedClient;
    private WebResource mockedResource;
    private WebResource.Builder mockedBuilder;
    private ClientResponse mockedResponse;
    private IFileManagementService mockedFileManagementService;
    private UploadArtifactTask uploadArtifactTaskDefinition;
    private File mockedFile;
    private List<File> mockedFileList;
    private WorkInfo workInfo;
    private UploadArtifactExecutor uploadArtifactExecutor;
    private StringBuilder report;
    private Gson jsonConverter;

    @Before
    public void setUp() {
        AgentConfiguration.configure();

        try {
            PowerMockito.whenNew(WebResource.Builder.class).withNoArguments().thenReturn(Mockito.mock(WebResource.Builder.class));
            this.mockedBuilder = Mockito.mock(WebResource.Builder.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PowerMockito.mockStatic(Client.class);
        this.mockedClient = Mockito.mock(Client.class);
        Mockito.when(Client.create()).thenReturn(this.mockedClient);

        this.mockedResource = Mockito.mock(WebResource.class);
        this.mockedResponse = Mockito.mock(ClientResponse.class);
        this.mockedFileManagementService = Mockito.mock(FileManagementService.class);
        this.mockedFile = new File("pathToFile");
        this.mockedFileList = new ArrayList<>();
        this.uploadArtifactExecutor = new UploadArtifactExecutor(this.mockedClient, this.mockedFileManagementService);
        this.report = new StringBuilder();
        this.setupData();

        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .create();
    }

    private void setupData() {
        this.correctUploadArtifactTask = new Task();
        this.correctUploadArtifactTask.setType(TaskType.UPLOAD_ARTIFACT);
        this.uploadArtifactTaskDefinition = new UploadArtifactTask();
        this.uploadArtifactTaskDefinition.setSource("correctSource");
        this.uploadArtifactTaskDefinition.setDestination("correctDestination");
        this.correctUploadArtifactTask.setTaskDefinition(this.uploadArtifactTaskDefinition);
        this.mockedFileList.add(new File(""));

        this.workInfo = new WorkInfo();
        this.workInfo.setPipelineDefinitionName("correct");
        this.workInfo.setStageDefinitionName("correct");
        this.workInfo.setJob(new Job());
        this.workInfo.getJob().setJobDefinitionName("correct");
    }

    @Test
    public void uploadArtifactExecutor_instantiated_notNull() {
        UploadArtifactExecutor executor = new UploadArtifactExecutor();
        Assert.assertNotNull(executor);
    }

    @Ignore
    @Test
    public void executeTask_artifactUploadedSuccessfully_taskPassed() {
        //Arrange
        Mockito.when(this.mockedFileManagementService.pathCombine(Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("rootPath");
        Mockito.when(this.mockedFileManagementService.getPattern(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFileList);
        Mockito.when(this.mockedFileManagementService.generateUniqueFile(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFile);
        Mockito.when(this.mockedFileManagementService.zipFiles("pathToFile", this.mockedFileList, "rootPath", false)).thenReturn(null);
        Mockito.when(this.mockedFileManagementService.urlCombine(Mockito.anyString())).thenReturn("my/path");

        UploadArtifactInfo uploadArtifactInfo = new UploadArtifactInfo(this.mockedFile, this.uploadArtifactTaskDefinition.getDestination());
        String uploadArtifactInfoAsString = this.jsonConverter.toJson(uploadArtifactInfo);

        Mockito.when(this.mockedClient.resource("my/path/upload-artifact")).thenReturn(this.mockedResource);
        Mockito.when(this.mockedResource.type(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.accept(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.type("multipart/form-data").post(ClientResponse.class, uploadArtifactInfoAsString)).thenReturn(this.mockedResponse);
        Mockito.when(this.mockedResponse.getStatus()).thenReturn(200);

        //Act
        Task resultTask = this.uploadArtifactExecutor.executeTask(this.correctUploadArtifactTask, this.report, this.workInfo);

        //Assert
        Mockito.verify(this.mockedFileManagementService, Mockito.times(1)).zipFiles(Mockito.anyObject(), Mockito.any(this.mockedFileList.getClass()), Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(this.mockedFileManagementService, Mockito.times(1)).getFiles(Mockito.anyString(), Mockito.anyString());
        Assert.assertEquals(TaskStatus.PASSED, resultTask.getStatus());
    }

    @Test
    public void executeTask_failedToZipFiles_taskFailed() {
        //Arrange
        Mockito.when(this.mockedFileManagementService.pathCombine(Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("rootPath");
        Mockito.when(this.mockedFileManagementService.getPattern(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFileList);
        Mockito.when(this.mockedFileManagementService.generateUniqueFile(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFile);
        Mockito.when(this.mockedFileManagementService.zipFiles("pathToFile", this.mockedFileList, "rootPath", true)).thenReturn("Error in zipFiles method!");
        Mockito.when(this.mockedFileManagementService.urlCombine(Mockito.anyString())).thenReturn("my/path");

        Mockito.when(this.mockedClient.resource("my/path/upload-artifact")).thenReturn(this.mockedResource);
        Mockito.when(this.mockedResource.type(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.accept(Mockito.anyString())).thenReturn(this.mockedBuilder);

        UploadArtifactInfo uploadArtifactInfo = new UploadArtifactInfo(this.mockedFile, this.uploadArtifactTaskDefinition.getDestination());
        String uploadArtifactInfoAsString = this.jsonConverter.toJson(uploadArtifactInfo);

        Mockito.when(this.mockedResource.type("multipart/form-data").post(ClientResponse.class, uploadArtifactInfoAsString)).thenReturn(this.mockedResponse);
        Mockito.when(this.mockedResponse.getStatus()).thenReturn(200);

        //Act
        Task resultTask = this.uploadArtifactExecutor.executeTask(this.correctUploadArtifactTask, this.report, this.workInfo);

        //Assert
        Mockito.verify(this.mockedFileManagementService, Mockito.times(1)).zipFiles(Mockito.anyObject(), Mockito.any(this.mockedFileList.getClass()), Mockito.anyString(), Mockito.anyBoolean());
        Assert.assertEquals(TaskStatus.FAILED, resultTask.getStatus());
        Assert.assertEquals(this.correctUploadArtifactTask.getTaskDefinition(), resultTask.getTaskDefinition());
        Assert.assertEquals(this.correctUploadArtifactTask.getTaskDefinition().getType(), resultTask.getTaskDefinition().getType());
    }

    @Ignore
    @Test
    public void executeTask_responseNotOk_taskFailed() {
        //Arrange
        Mockito.when(this.mockedFileManagementService.pathCombine(Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("rootPath");
        Mockito.when(this.mockedFileManagementService.getPattern(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFileList);
        Mockito.when(this.mockedFileManagementService.generateUniqueFile(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFile);
        Mockito.when(this.mockedFileManagementService.zipFiles("pathToFile", this.mockedFileList, "rootPath", false)).thenReturn(null);
        Mockito.when(this.mockedFileManagementService.urlCombine(Mockito.anyString())).thenReturn("my/path");

        UploadArtifactInfo uploadArtifactInfo = new UploadArtifactInfo(this.mockedFile, this.uploadArtifactTaskDefinition.getDestination());
        String uploadArtifactInfoAsString = this.jsonConverter.toJson(uploadArtifactInfo);

        Mockito.when(this.mockedClient.resource("my/path/upload-artifact")).thenReturn(this.mockedResource);
        Mockito.when(this.mockedResource.type(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.accept(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.type("application/json").post(ClientResponse.class, uploadArtifactInfoAsString)).thenReturn(this.mockedResponse);
        Mockito.when(this.mockedResponse.getStatus()).thenReturn(400);

        //Act
        Task resultTask = this.uploadArtifactExecutor.executeTask(this.correctUploadArtifactTask, this.report, this.workInfo);

        //Assert
        Mockito.verify(this.mockedFileManagementService, Mockito.times(1)).zipFiles(Mockito.anyObject(), Mockito.any(this.mockedFileList.getClass()), Mockito.anyString(), Mockito.anyBoolean());
        Assert.assertEquals(TaskStatus.FAILED, resultTask.getStatus());
        Assert.assertEquals(this.correctUploadArtifactTask.getTaskDefinition(), resultTask.getTaskDefinition());
        Assert.assertEquals(this.correctUploadArtifactTask.getTaskDefinition().getType(), resultTask.getTaskDefinition().getType());
    }

    @Test
    public void executeTask_noFilesToUpload_taskFailed() {
        //Arrange
        Mockito.when(this.mockedFileManagementService.pathCombine(Mockito.anyString())).thenReturn("full\\path");
        Mockito.when(this.mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("rootPath");
        Mockito.when(this.mockedFileManagementService.getPattern(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(new ArrayList<>());
        Mockito.when(this.mockedFileManagementService.generateUniqueFile(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFile);
        Mockito.when(this.mockedFileManagementService.zipFiles("pathToFile", this.mockedFileList, "rootPath", false)).thenReturn(null);
        Mockito.when(this.mockedFileManagementService.urlCombine(Mockito.anyString())).thenReturn("my/path");

        Mockito.when(this.mockedClient.resource("my/path")).thenReturn(this.mockedResource);
        Mockito.when(this.mockedResource.type(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.accept(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.type("multipart/form-data").post(ClientResponse.class, this.mockedFile)).thenReturn(this.mockedResponse);
        Mockito.when(this.mockedResponse.getStatus()).thenReturn(200);

        //Act
        Task resultTask = this.uploadArtifactExecutor.executeTask(this.correctUploadArtifactTask, this.report, this.workInfo);

        //Assert
        Assert.assertEquals(TaskStatus.FAILED, resultTask.getStatus());
        Assert.assertEquals(this.correctUploadArtifactTask.getTaskDefinition(), resultTask.getTaskDefinition());
        Assert.assertEquals(this.correctUploadArtifactTask.getTaskDefinition().getType(), resultTask.getTaskDefinition().getType());
    }

    @Test
    public void executeTask_emptyRootPath_taskFailed() {
        //Arrange
        Mockito.when(this.mockedFileManagementService.pathCombine(Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getPattern(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        Mockito.when(this.mockedFileManagementService.generateUniqueFile(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFile);
        Mockito.when(this.mockedFileManagementService.zipFiles("pathToFile", this.mockedFileList, "rootPath", false)).thenReturn(null);
        Mockito.when(this.mockedFileManagementService.urlCombine(Mockito.anyString())).thenReturn("my/path");

        Mockito.when(this.mockedClient.resource("my/path")).thenReturn(this.mockedResource);
        Mockito.when(this.mockedResource.type(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.accept(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.type("multipart/form-data").post(ClientResponse.class, this.mockedFile)).thenReturn(this.mockedResponse);
        Mockito.when(this.mockedResponse.getStatus()).thenReturn(200);

        //Act
        Task resultTask = this.uploadArtifactExecutor.executeTask(this.correctUploadArtifactTask, this.report, this.workInfo);

        //Assert
        Assert.assertEquals(TaskStatus.FAILED, resultTask.getStatus());
        Assert.assertEquals(this.correctUploadArtifactTask.getTaskDefinition(), resultTask.getTaskDefinition());
        Assert.assertEquals(this.correctUploadArtifactTask.getTaskDefinition().getType(), resultTask.getTaskDefinition().getType());
    }
}