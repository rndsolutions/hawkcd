package net.hawkengine.agent.components.taskexecutor.executors;

import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.base.TestBase;
import net.hawkengine.agent.components.taskexecutor.TaskExecutor;
import net.hawkengine.agent.enums.ExecutionStatus;
import net.hawkengine.agent.models.ExecTask;
import net.hawkengine.agent.models.payload.JobExecutionInfo;
import net.hawkengine.agent.models.payload.TaskExecutionInfo;
import org.junit.*;

import java.io.File;

public class ExecTaskExecutorTest extends TestBase{

    private static TaskExecutor execTaskExecutor = new ExecTaskExecutor();

    private ExecTask task;
    private JobExecutionInfo jobExecutionInfo;
    private String testFailMessage;

    @Before
    public void setUp() throws Exception {
        AgentConfiguration.configure();
        File pipelinesDir = new File(AgentConfiguration.getInstallInfo().getAgentPipelinesDir());
        pipelinesDir.mkdir();

        this.task = new ExecTask();
        this.jobExecutionInfo = new JobExecutionInfo();
        this.testFailMessage = "";
    }

    @After
    public void tearDown() throws Exception {
        this.testFailMessage = "";
    }

    @Test
    public void validTask_returnTaskExecutionInfo() throws Exception {

        this.task.setCommand("cmd");
        this.task.setArguments(new String[]{"/c", "echo", "test argument"});
        this.task.setIgnoreErrors(false);
        this.jobExecutionInfo.setPipelineName("Hawk");

        TaskExecutionInfo result = execTaskExecutor.ExecuteTask(this.task, this.jobExecutionInfo);

        Assert.assertNotNull(result);
    }

    @Test
    public void successfullyExecutingTask_statusPassed() throws Exception {

        Assume.assumeTrue(System.getProperty("os.name").toLowerCase().startsWith("win"));
        //arrange
        this.task.setCommand("cmd");
        this.task.setArguments(new String[]{"/c", "echo", "test argument"});
        this.task.setIgnoreErrors(false);
        this.jobExecutionInfo.setPipelineName("");

        ExecutionStatus expectedResult = ExecutionStatus.PASSED;
        //act
        ExecutionStatus actualResult = execTaskExecutor.ExecuteTask(this.task, this.jobExecutionInfo).getStatus();
        //assert
        this.testFailMessage = String.format("Expected result is %s, but the actual is %s", expectedResult, actualResult);

        Assert.assertEquals(testFailMessage, expectedResult, actualResult);
    }

    @Test
    public void failingTaskAndNotIgnoringErrors_statusFailed() throws Exception {

        this.task.setCommand("cmd");
        this.task.setArguments(new String[]{"/c", "wrong", "command"});
        this.task.setIgnoreErrors(false);
        this.jobExecutionInfo.setPipelineName("");

        ExecutionStatus expectedResult = ExecutionStatus.FAILED;
        ExecutionStatus actualResult = execTaskExecutor.ExecuteTask(this.task, this.jobExecutionInfo).getStatus();

        this.testFailMessage = String.format("Expected result is %s, but the actual is %s", expectedResult, actualResult);

        Assert.assertEquals(testFailMessage, expectedResult, actualResult);
    }

//    @Test
//    public void failingTaskAndIgnoringErrors_statusPassed() throws Exception {
//
//        this.task.setCommand("cmd");
//        this.task.setArguments(new String[]{"/c", "wrong", "command"});
//        this.task.setIgnoreErrors(true);
//        this.jobExecutionInfo.setPipelineDefinitionName("");
//
//        ExecutionStatus expectedResult = ExecutionStatus.PASSED;
//        ExecutionStatus actualResult = execTaskExecutor.executeTask(this.task, this.jobExecutionInfo).getStatus();
//
//        this.testFailMessage = String.format("Expected result is %s, but the actual is %s", expectedResult, actualResult);
//
//        Assert.assertEquals(testFailMessage, expectedResult, actualResult);
//    }
//
//    @Test
//    public void customExistingWorkingDirectory_statusPassed() throws Exception {
//
//        this.task.setCommand("cmd");
//        this.task.setArguments(new String[]{"/c", "echo", "test argument"});
//        this.task.setIgnoreErrors(false);
//        this.task.setWorkingDirectory("src");
//
//        ExecutionStatus expectedResult = ExecutionStatus.PASSED;
//        ExecutionStatus actualResult = execTaskExecutor.executeTask(this.task, this.jobExecutionInfo).getStatus();
//
//        this.testFailMessage = String.format("Expected result is %s, but the actual is %s", expectedResult, actualResult);
//
//        Assert.assertEquals(testFailMessage, expectedResult, actualResult);
//    }
//
//    @Test
//    public void nonexistentWorkingDirectory_statusFailed() throws Exception {
//
//        this.task.setCommand("cmd");
//        this.task.setArguments(new String[]{"/c", "echo", "test argument"});
//        this.task.setIgnoreErrors(false);
//        this.task.setWorkingDirectory("NonExistentDirectory");
//
//        ExecutionStatus expectedResult = ExecutionStatus.FAILED;
//        ExecutionStatus actualResult = execTaskExecutor.executeTask(this.task, this.jobExecutionInfo).getStatus();
//
//        this.testFailMessage = String.format("Expected result is %s, but the actual is %s", expectedResult, actualResult);
//
//        Assert.assertEquals(testFailMessage, expectedResult, actualResult);
//    }
}