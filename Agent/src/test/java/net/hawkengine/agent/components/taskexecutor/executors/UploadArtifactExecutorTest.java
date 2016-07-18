package net.hawkengine.agent.components.taskexecutor.executors;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import junit.framework.Assert;

import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.base.TestBase;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.enums.TaskType;
import net.hawkengine.agent.models.Job;
import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.UploadArtifactTask;
import net.hawkengine.agent.models.payload.WorkInfo;
import net.hawkengine.agent.services.FileManagementService;
import net.hawkengine.agent.services.interfaces.IFileManagementService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

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
    private File[] mockedFileList;
    private WorkInfo workInfo;
    private UploadArtifactExecutor uploadArtifactExecutor;
    private StringBuilder report;

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
        this.mockedFileList = new File[1];
        this.uploadArtifactExecutor = new UploadArtifactExecutor(this.mockedClient, this.mockedFileManagementService);
        this.report = new StringBuilder();
        setupData();
    }

    private void setupData() {
        this.correctUploadArtifactTask = new Task();
        this.correctUploadArtifactTask.setType(TaskType.UPLOAD_ARTIFACT);
        this.uploadArtifactTaskDefinition = new UploadArtifactTask();
        this.uploadArtifactTaskDefinition.setSource("correctSource");
        this.uploadArtifactTaskDefinition.setDestination("correctDestination");
        this.correctUploadArtifactTask.setTaskDefinition(this.uploadArtifactTaskDefinition);

        this.workInfo = new WorkInfo();
        this.workInfo.setPipelineDefinitionName("correct");
        this.workInfo.setStageDefinitionName("correct");
        this.workInfo.setJob(new Job());
        this.workInfo.getJob().setJobDefinitionName("correct");
    }

    @Test
    public void uploadArtifactExecutor_passing() {
        //Arrange
        Mockito.when(this.mockedFileManagementService.pathCombine(Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("rootPath");
        Mockito.when(this.mockedFileManagementService.getPattern(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFileList);
        Mockito.when(this.mockedFileManagementService.generateUniqueFile(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFile);
        Mockito.when(this.mockedFileManagementService.zipFiles("pathToFile", this.mockedFileList, "rootPath", false)).thenReturn(null);
        Mockito.when(this.mockedFileManagementService.urlCombine(Mockito.anyString())).thenReturn("my/path");

        Mockito.when(this.mockedClient.resource("my/path/upload-artifact")).thenReturn(this.mockedResource);
        Mockito.when(this.mockedResource.type(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.accept(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.type("multipart/form-data").post(ClientResponse.class, this.mockedFile)).thenReturn(this.mockedResponse);
        Mockito.when(this.mockedResponse.getStatus()).thenReturn(200);

        //Act
        Task resultTask = this.uploadArtifactExecutor.executeTask(this.correctUploadArtifactTask, this.report, this.workInfo);

        //Assert
        Mockito.verify(this.mockedFileManagementService, Mockito.times(1)).zipFiles(Mockito.anyObject(), Mockito.any(File[].class), Mockito.anyString(), Mockito.anyBoolean());
        Mockito.verify(this.mockedFileManagementService, Mockito.times(1)).getFiles(Mockito.anyString(), Mockito.anyString());
        Assert.assertEquals(TaskStatus.PASSED, resultTask.getStatus());
    }

    @Test
    public void uploadArtifactExecutor_zippingFiles_failing() {
        //Arrange
        Mockito.when(this.mockedFileManagementService.pathCombine(Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("rootPath");
        Mockito.when(this.mockedFileManagementService.getPattern(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFileList);
        Mockito.when(this.mockedFileManagementService.generateUniqueFile(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFile);
        Mockito.when(this.mockedFileManagementService.zipFiles("pathToFile", this.mockedFileList, "rootPath", false)).thenReturn("Error in zipFiles method!");
        Mockito.when(this.mockedFileManagementService.urlCombine(Mockito.anyString())).thenReturn("my/path");

        Mockito.when(this.mockedClient.resource("my/path/upload-artifact")).thenReturn(this.mockedResource);
        Mockito.when(this.mockedResource.type(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.accept(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.type("multipart/form-data").post(ClientResponse.class, this.mockedFile)).thenReturn(this.mockedResponse);
        Mockito.when(this.mockedResponse.getStatus()).thenReturn(200);

        //Act
        Task resultTask = this.uploadArtifactExecutor.executeTask(this.correctUploadArtifactTask, this.report, this.workInfo);

        //Assert
        Mockito.verify(this.mockedFileManagementService, Mockito.times(1)).zipFiles(Mockito.anyObject(), Mockito.any(File[].class), Mockito.anyString(), Mockito.anyBoolean());
        Assert.assertEquals(TaskStatus.FAILED, resultTask.getStatus());
        Assert.assertEquals(this.correctUploadArtifactTask.getTaskDefinition(), resultTask.getTaskDefinition());
        Assert.assertEquals(this.correctUploadArtifactTask.getTaskDefinition().getType(), resultTask.getTaskDefinition().getType());
    }

    @Test
    public void uploadArtifactExecutor_serverResponse_failing() {
        //Arrange
        Mockito.when(this.mockedFileManagementService.pathCombine(Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("rootPath");
        Mockito.when(this.mockedFileManagementService.getPattern(Mockito.anyString(), Mockito.anyString())).thenReturn("");
        Mockito.when(this.mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFileList);
        Mockito.when(this.mockedFileManagementService.generateUniqueFile(Mockito.anyString(), Mockito.anyString())).thenReturn(this.mockedFile);
        Mockito.when(this.mockedFileManagementService.zipFiles("pathToFile", this.mockedFileList, "rootPath", false)).thenReturn(null);
        Mockito.when(this.mockedFileManagementService.urlCombine(Mockito.anyString())).thenReturn("my/path");

        Mockito.when(this.mockedClient.resource("my/path/upload-artifact")).thenReturn(this.mockedResource);
        Mockito.when(this.mockedResource.type(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.accept(Mockito.anyString())).thenReturn(this.mockedBuilder);
        Mockito.when(this.mockedResource.type("multipart/form-data").post(ClientResponse.class, this.mockedFile)).thenReturn(this.mockedResponse);
        Mockito.when(this.mockedResponse.getStatus()).thenReturn(400);

        //Act
        Task resultTask = this.uploadArtifactExecutor.executeTask(this.correctUploadArtifactTask, this.report, this.workInfo);

        //Assert
        Mockito.verify(this.mockedFileManagementService, Mockito.times(1)).zipFiles(Mockito.anyObject(), Mockito.any(File[].class), Mockito.anyString(), Mockito.anyBoolean());
        Assert.assertEquals(TaskStatus.FAILED, resultTask.getStatus());
        Assert.assertEquals(this.correctUploadArtifactTask.getTaskDefinition(), resultTask.getTaskDefinition());
        Assert.assertEquals(this.correctUploadArtifactTask.getTaskDefinition().getType(), resultTask.getTaskDefinition().getType());
    }

    @Test
    public void uploadArtifactExecutor_getFiles_failing() {
        //Arrange
        Mockito.when(this.mockedFileManagementService.pathCombine(Mockito.anyString())).thenReturn("full\\path");
        Mockito.when(this.mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("rootPath");
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

    @Test
    public void uploadArtifactExecutor_emptyRootPath_failing() {
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

    @Test
    public void test_initialConstructor() {
        UploadArtifactExecutor executor = new UploadArtifactExecutor();
        Assert.assertNotNull(executor);
    }
}