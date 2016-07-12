package net.hawkengine.agent.components.taskexecutor.executors;

import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.base.TestBase;
import net.hawkengine.agent.enums.ExecutionState;
import net.hawkengine.agent.enums.ExecutionStatus;
import net.hawkengine.agent.models.FetchMaterialTask;
import net.hawkengine.agent.models.payload.JobExecutionInfo;
import net.hawkengine.agent.models.payload.TaskExecutionInfo;
import net.hawkengine.agent.services.interfaces.IMaterialService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.atomic.AtomicBoolean;

public class FetchMaterialExecutorTest extends TestBase{

//    private FetchMaterialTask task;
//    private JobExecutionInfo jobExecutionInfo;
//
//    @InjectMocks
//    private FetchMaterialExecutor fetchMaterialExecutor;
//
//    @Mock
//    private IMaterialService mockedService;
//
//    @Before
//    public void setUp() throws Exception {
//        AgentConfiguration.configure();
//
//        MockitoAnnotations.initMocks(this);
//
//        this.task = new FetchMaterialTask();
//        this.task.setPipelineName("Hawk");
//        this.task.setDestination("MaterialFolder");
//
//        this.jobExecutionInfo = new JobExecutionInfo();
//    }
//
//    @After
//    public void tearDown() throws Exception {
//
//    }
//
//    @Test
//    public void FetchMaterialExecutor_passed() throws Exception {
//
//        ExecutionState expectedState = ExecutionState.COMPLETED;
//        ExecutionStatus expectedStatus = ExecutionStatus.PASSED;
//
//        AtomicBoolean testResult = new AtomicBoolean();
//        testResult.set(true);
//
//        StringBuilder errorMessages = new StringBuilder();
//
//        task.setDestination("");
//        Mockito.when(mockedService.fetchMaterial(Mockito.any(FetchMaterialTask.class))).thenReturn(null);
//
//        TaskExecutionInfo result = this.fetchMaterialExecutor.ExecuteTask(this.task, this.jobExecutionInfo);
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
//    public void FetchMaterialExecutor_failed() throws Exception {
//
//        ExecutionState expectedState = ExecutionState.COMPLETED;
//        ExecutionStatus expectedStatus = ExecutionStatus.FAILED;
//
//        AtomicBoolean testResult = new AtomicBoolean();
//        testResult.set(true);
//
//        StringBuilder errorMessages = new StringBuilder();
//
//        task.setDestination("");
//        Mockito.when(mockedService.fetchMaterial(Mockito.any(FetchMaterialTask.class))).thenReturn("ERROR!");
//
//        TaskExecutionInfo result = this.fetchMaterialExecutor.ExecuteTask(this.task, this.jobExecutionInfo);
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