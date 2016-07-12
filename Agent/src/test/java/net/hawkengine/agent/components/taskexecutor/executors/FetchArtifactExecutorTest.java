package net.hawkengine.agent.components.taskexecutor.executors;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.base.TestBase;
import net.hawkengine.agent.enums.ExecutionState;
import net.hawkengine.agent.enums.ExecutionStatus;
import net.hawkengine.agent.models.FetchArtifactTask;
import net.hawkengine.agent.models.payload.JobExecutionInfo;
import net.hawkengine.agent.models.payload.TaskExecutionInfo;
import net.hawkengine.agent.services.FileManagementService;
import net.hawkengine.agent.services.interfaces.IFileManagementService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.omg.CORBA.portable.InputStream;

import java.util.concurrent.atomic.AtomicBoolean;

public class FetchArtifactExecutorTest extends TestBase{

//    private static FetchArtifactExecutor fetchArtifactExecutor = new FetchArtifactExecutor();
//
//    private Client mockedClient = Mockito.mock(Client.class);
//    private WebResource mockedResource = Mockito.mock(WebResource.class);
//    private ClientResponse mockedResponse = Mockito.mock(ClientResponse.class);
//    private static IFileManagementService mockedFileManagementService = Mockito.mock(FileManagementService.class);
//
//    private FetchArtifactTask task;
//    private JobExecutionInfo jobExecutionInfo;
//    private String testFailMessage;
//
//    @Before
//    public void setUp() throws Exception {
//
//        AgentConfiguration.configure();
//
//        this.fetchArtifactExecutor.setRestClient(mockedClient);
//        this.fetchArtifactExecutor.setFileManagementService(mockedFileManagementService);
//
//        this.task = new FetchArtifactTask();
//        this.task.setPipeline("Hawk");
//        this.task.setStage("Build");
//        this.task.setJob("Compile");
//        this.task.setSource("TestPackage");
//        this.task.setDestination("TestFolder");
//
//        this.jobExecutionInfo = new JobExecutionInfo();
//        this.jobExecutionInfo.setPipelineExecutionId(1);
//        this.jobExecutionInfo.setStageExecutionId(1);
//        this.testFailMessage = "";
//    }
//
//    @After
//    public void tearDown() throws Exception {
//
//    }
//
//    @Test
//    public void fetchArtifactExecutor_passed() throws Exception {
//
//        ExecutionState expectedState = ExecutionState.COMPLETED;
//        ExecutionStatus expectedStatus = ExecutionStatus.PASSED;
//
//        AtomicBoolean testResult = new AtomicBoolean();
//        testResult.set(true);
//
//        StringBuilder errorMessages = new StringBuilder();
//
//        Mockito.when(mockedResponse.getStatus()).thenReturn(200);
//        Mockito.when(mockedResponse.getEntityInputStream()).thenReturn(Mockito.mock(InputStream.class));
//        Mockito.when(mockedClient.resource(Mockito.any(String.class))).thenReturn(mockedResource);
//        Mockito.when(mockedResource.get(Mockito.any(Class.class))).thenReturn(mockedResponse);
//
//        Mockito.when(mockedFileManagementService.streamToFile(Mockito.any(InputStream.class), Mockito.any(String.class))).thenReturn(null);
//        Mockito.when(mockedFileManagementService.unzipFile(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(null);
//
//        TaskExecutionInfo result = this.fetchArtifactExecutor.executeTask(this.task, this.jobExecutionInfo);
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
//    public void fetchArtifactExecutor__invalidArtifactSource_failed() throws Exception {
//
//        ExecutionState expectedState = ExecutionState.COMPLETED;
//        ExecutionStatus expectedStatus = ExecutionStatus.FAILED;
//
//        AtomicBoolean testResult = new AtomicBoolean();
//        testResult.set(true);
//
//        StringBuilder errorMessages = new StringBuilder();
//
//        Mockito.when(mockedResponse.getStatus()).thenReturn(404);
//        Mockito.when(mockedClient.resource(Mockito.any(String.class))).thenReturn(mockedResource);
//        Mockito.when(mockedResource.get(Mockito.any(Class.class))).thenReturn(mockedResponse);
//
//        TaskExecutionInfo result = fetchArtifactExecutor.executeTask(this.task, this.jobExecutionInfo);
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
//    public void fetchArtifactExecutor__failToGetArtifactContent_failed() throws Exception {
//
//        ExecutionState expectedState = ExecutionState.COMPLETED;
//        ExecutionStatus expectedStatus = ExecutionStatus.FAILED;
//
//        AtomicBoolean testResult = new AtomicBoolean();
//        testResult.set(true);
//
//        StringBuilder errorMessages = new StringBuilder();
//
//        Mockito.when(mockedResponse.getStatus()).thenReturn(200);
//        Mockito.when(mockedClient.resource(Mockito.any(String.class))).thenReturn(mockedResource);
//        Mockito.when(mockedResource.get(Mockito.any(Class.class))).thenReturn(mockedResponse);
//
//        Mockito.when(mockedFileManagementService.streamToFile(Mockito.any(InputStream.class), Mockito.any(String.class))).thenReturn("Error message");
//
//        TaskExecutionInfo result = fetchArtifactExecutor.executeTask(this.task, this.jobExecutionInfo);
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
//    public void fetchArtifactExecutor__failToUnzipFile_failed() throws Exception {
//
//        ExecutionState expectedState = ExecutionState.COMPLETED;
//        ExecutionStatus expectedStatus = ExecutionStatus.FAILED;
//
//        AtomicBoolean testResult = new AtomicBoolean();
//        testResult.set(true);
//
//        StringBuilder errorMessages = new StringBuilder();
//
//        Mockito.when(mockedResponse.getStatus()).thenReturn(200);
//        Mockito.when(mockedClient.resource(Mockito.any(String.class))).thenReturn(mockedResource);
//        Mockito.when(mockedResource.get(Mockito.any(Class.class))).thenReturn(mockedResponse);
//
//        Mockito.when(mockedFileManagementService.streamToFile(Mockito.any(InputStream.class), Mockito.any(String.class))).thenReturn(null);
//        Mockito.when(mockedFileManagementService.unzipFile(Mockito.any(String.class), Mockito.any(String.class))).thenReturn("Error Message");
//
//        TaskExecutionInfo result = fetchArtifactExecutor.executeTask(this.task, this.jobExecutionInfo);
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