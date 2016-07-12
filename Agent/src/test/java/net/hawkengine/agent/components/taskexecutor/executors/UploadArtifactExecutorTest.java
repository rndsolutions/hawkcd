package net.hawkengine.agent.components.taskexecutor.executors;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.base.TestBase;
import net.hawkengine.agent.enums.ExecutionState;
import net.hawkengine.agent.enums.ExecutionStatus;
import net.hawkengine.agent.models.UploadArtifactTask;
import net.hawkengine.agent.models.payload.JobExecutionInfo;
import net.hawkengine.agent.models.payload.TaskExecutionInfo;
import net.hawkengine.agent.services.interfaces.IFileManagementService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class UploadArtifactExecutorTest extends TestBase{

//    @InjectMocks
//    private UploadArtifactExecutor uploadArtifactExecutor;
//
//    @Mock
//    private Client mockedClient;
//    @Mock
//    private WebResource mockedResource;
//    @Mock
//    private ClientResponse mockedResponse;
//    @Mock
//    private IFileManagementService mockedFileManagementService;
//
//    private UploadArtifactTask task;
//    private JobExecutionInfo jobExecutionInfo;
//    private File mockedFile;
//    private File[] mockedFileList;
//
//    @Before
//    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//
//        AgentConfiguration.configure();
//
//        this.task = new UploadArtifactTask();
//        this.task.setSource("TestFolder");
//
//        this.jobExecutionInfo = new JobExecutionInfo();
//        this.jobExecutionInfo.setPipelineName("Hawk");
//
//        this.mockedFile = new File("");
//        this.mockedFileList = new File[1];
//    }
//
//    @After
//    public void tearDown() throws Exception {
//
//    }
//
//    @Test
//    public void uploadArtifactExecutor_passed() throws Exception {
//
//        ExecutionState expectedState = ExecutionState.COMPLETED;
//        ExecutionStatus expectedStatus = ExecutionStatus.PASSED;
//
//        AtomicBoolean testResult = new AtomicBoolean();
//        testResult.set(true);
//
//        StringBuilder errorMessages = new StringBuilder();
//
//        Mockito.when(mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("Path");
//        Mockito.when(mockedFileManagementService.generateUniqueFile(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedFile);
//        Mockito.when(mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedFileList);
//        //Mockito.when(mockedFileManagementService.zipFiles(Mockito.anyString(), Mockito.any(File[].class), Mockito.anyString())).thenReturn(null);
//
//        Mockito.when(mockedResponse.getStatus()).thenReturn(200);
//        Mockito.when(mockedClient.resource(Mockito.anyString())).thenReturn(mockedResource);
//        Mockito.when(mockedResource.post(ClientResponse.class, mockedFile)).thenReturn(mockedResponse);
//
//        TaskExecutionInfo result = this.uploadArtifactExecutor.ExecuteTask(this.task, this.jobExecutionInfo);
//
//        ExecutionStatus actualStatus = result.getStatus();
//        ExecutionState actualState = result.getState();
//
//        super.checkResult(testResult, errorMessages, result != null, "The expected task result is taskExecutionInfo object, but the actual is null.");
//        super.checkResult(testResult, errorMessages, actualStatus == expectedStatus, String.format("The expected task status is %s, but the actual is %s.", expectedStatus, actualStatus));
//        super.checkResult(testResult, errorMessages, actualState == expectedState, String.format("The expected task state is %s, but the actual is %s.", expectedState, actualState));
//
//        Assert.assertEquals(errorMessages.toString(), true, testResult.get());
//
//    }
//
//    @Test
//    public void uploadArtifactExecutor_corruptedArtifact_failed() throws Exception {
//
//        ExecutionState expectedState = ExecutionState.COMPLETED;
//        ExecutionStatus expectedStatus = ExecutionStatus.FAILED;
//
//        AtomicBoolean testResult = new AtomicBoolean();
//        testResult.set(true);
//
//        StringBuilder errorMessages = new StringBuilder();
//
//        Mockito.when(mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("Path");
//        Mockito.when(mockedFileManagementService.generateUniqueFile(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedFile);
//        Mockito.when(mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedFileList);
//        //Mockito.when(mockedFileManagementService.zipFiles(Mockito.anyString(), Mockito.any(File[].class), Mockito.anyString())).thenReturn(null);
//
//        Mockito.when(mockedResponse.getStatus()).thenReturn(500);
//        Mockito.when(mockedClient.resource(Mockito.anyString())).thenReturn(mockedResource);
//        Mockito.when(mockedResource.post(ClientResponse.class, mockedFile)).thenReturn(mockedResponse);
//
//        TaskExecutionInfo result = this.uploadArtifactExecutor.ExecuteTask(this.task, this.jobExecutionInfo);
//
//        ExecutionStatus actualStatus = result.getStatus();
//        ExecutionState actualState = result.getState();
//
//        super.checkResult(testResult, errorMessages, result != null, "The expected task result is taskExecutionInfo object, but the actual is null.");
//        super.checkResult(testResult, errorMessages, actualStatus == expectedStatus, String.format("The expected task status is %s, but the actual is %s.", expectedStatus, actualStatus));
//        super.checkResult(testResult, errorMessages, actualState == expectedState, String.format("The expected task state is %s, but the actual is %s.", expectedState, actualState));
//
//        Assert.assertEquals(errorMessages.toString(), true, testResult.get());
//    }
//
//    @Test
//    public void uploadArtifactExecutor_invalidArtifactSource_failed() throws Exception {
//
//        ExecutionState expectedState = ExecutionState.COMPLETED;
//        ExecutionStatus expectedStatus = ExecutionStatus.FAILED;
//
//        AtomicBoolean testResult = new AtomicBoolean();
//        testResult.set(true);
//
//        StringBuilder errorMessages = new StringBuilder();
//
//        Mockito.when(mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("Path");
//        Mockito.when(mockedFileManagementService.generateUniqueFile(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedFile);
//        Mockito.when(mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
//        //Mockito.when(mockedFileManagementService.zipFiles(Mockito.anyString(), Mockito.any(File[].class), Mockito.anyString())).thenReturn(null);
//
//        TaskExecutionInfo result = this.uploadArtifactExecutor.ExecuteTask(this.task, this.jobExecutionInfo);
//
//        ExecutionStatus actualStatus = result.getStatus();
//        ExecutionState actualState = result.getState();
//
//        super.checkResult(testResult, errorMessages, result != null, "The expected task result is taskExecutionInfo object, but the actual is null.");
//        super.checkResult(testResult, errorMessages, actualStatus == expectedStatus, String.format("The expected task status is %s, but the actual is %s.", expectedStatus, actualStatus));
//        super.checkResult(testResult, errorMessages, actualState == expectedState, String.format("The expected task state is %s, but the actual is %s.", expectedState, actualState));
//
//        Assert.assertEquals(errorMessages.toString(), true, testResult.get());
//    }
//
//    @Test
//    public void uploadArtifactExecutor_zippingFailed_failed() throws Exception {
//
//        ExecutionState expectedState = ExecutionState.COMPLETED;
//        ExecutionStatus expectedStatus = ExecutionStatus.FAILED;
//
//        AtomicBoolean testResult = new AtomicBoolean();
//        testResult.set(true);
//
//        StringBuilder errorMessages = new StringBuilder();
//
//        Mockito.when(mockedFileManagementService.getRootPath(Mockito.anyString())).thenReturn("Path");
//        Mockito.when(mockedFileManagementService.generateUniqueFile(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedFile);
//        Mockito.when(mockedFileManagementService.getFiles(Mockito.anyString(), Mockito.anyString())).thenReturn(mockedFileList);
//
//        Mockito.when(mockedClient.resource(Mockito.anyString())).thenReturn(mockedResource);
//        Mockito.when(mockedFileManagementService.zipFiles(Mockito.anyString(),Mockito.any(File[].class), Mockito.anyString(), Mockito.anyBoolean())).thenReturn("Error Message");
//
//        TaskExecutionInfo result = this.uploadArtifactExecutor.ExecuteTask(this.task, this.jobExecutionInfo);
//
//        ExecutionStatus actualStatus = result.getStatus();
//        ExecutionState actualState = result.getState();
//
//        super.checkResult(testResult, errorMessages, result != null, "The expected task result is taskExecutionInfo object, but the actual is null.");
//        super.checkResult(testResult, errorMessages, actualStatus == expectedStatus, String.format("The expected task status is %s, but the actual is %s.", expectedStatus, actualStatus));
//        super.checkResult(testResult, errorMessages, actualState == expectedState, String.format("The expected task state is %s, but the actual is %s.", expectedState, actualState));
//
//        Assert.assertEquals(errorMessages.toString(), true, testResult.get());
//    }
}