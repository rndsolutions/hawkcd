package net.hawkengine.agent.components.taskexecutor.executors;

import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.base.TestBase;
import net.hawkengine.agent.components.taskexecutor.TaskExecutor;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.ExecTask;
import net.hawkengine.agent.models.Job;
import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.payload.WorkInfo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExecTaskExecutorTest extends TestBase {

    private TaskExecutor execTaskExecutor;
    private Task expectedTask;
    private ExecTask expectedExecTask;
    private Job job;
    private WorkInfo expectedWorkInfo;
    private StringBuilder expectedReport;
    private String expectedWorkingDir;

    @Before
    public void setUp() throws Exception {
        this.execTaskExecutor = new ExecTaskExecutor();

        AgentConfiguration.configure();
        File pipelinesDir = new File(AgentConfiguration.getInstallInfo().getAgentPipelinesDir());
        pipelinesDir.mkdir();

        this.job = new Job();

        this.expectedWorkInfo = new WorkInfo();
        this.expectedWorkInfo.setPipelineDefinitionName("tetttPipeline");
        this.expectedReport = new StringBuilder();

        this.expectedTask = new Task();
        this.expectedExecTask = new ExecTask();
    }

    @After
    public void after() {
        if (this.expectedWorkingDir != null) {
            new File(this.expectedWorkingDir).delete();
        }
    }

    @Test
    public void executeTask_validTask_passedTaskStatus() throws Exception {
        //Assert
        List<String> expectedArguments = new ArrayList<>();
        expectedArguments.add("/c");
        expectedArguments.add("echo");
        expectedArguments.add("test");

        this.expectedExecTask.setCommand("cmd");
        this.expectedExecTask.setArguments(expectedArguments);
        this.expectedExecTask.setIgnoringErrors(false);

        this.expectedTask.setTaskDefinition(this.expectedExecTask);

        List<Task> jobTasks = new ArrayList<>();

        jobTasks.add(this.expectedTask);
        this.job.setTasks(jobTasks);
        this.expectedWorkInfo.setJob(this.job);

        this.expectedWorkingDir = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDirectoryPath(), this.expectedWorkInfo.getPipelineDefinitionName()).toString();
        new File(this.expectedWorkingDir).mkdirs();

        //Act
        Task actualTask = execTaskExecutor.executeTask(this.expectedTask, this.expectedReport, this.expectedWorkInfo);

        //Assert
        Assert.assertNotNull(actualTask);
        Assert.assertEquals(TaskStatus.PASSED, actualTask.getStatus());
    }

    @Test
    public void executeTask_invalidTask_failedTaskStatus() throws Exception {
        //Assert
        List<String> expectedArguments = new ArrayList<>();
        expectedArguments.add("/c");
        expectedArguments.add("echo");
        expectedArguments.add("test");

        this.expectedExecTask.setCommand("cmhhd");
        this.expectedExecTask.setArguments(expectedArguments);
        this.expectedExecTask.setIgnoringErrors(false);

        this.expectedTask.setTaskDefinition(this.expectedExecTask);

        List<Task> jobTasks = new ArrayList<>();

        jobTasks.add(this.expectedTask);
        this.job.setTasks(jobTasks);
        this.expectedWorkInfo.setJob(this.job);

        this.expectedWorkingDir = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDirectoryPath(), this.expectedWorkInfo.getPipelineDefinitionName()).toString();
        new File(this.expectedWorkingDir).mkdirs();

        //Act
        Task actualTask = execTaskExecutor.executeTask(this.expectedTask, this.expectedReport, this.expectedWorkInfo);

        //Assert
        Assert.assertNotNull(actualTask);
        Assert.assertEquals(TaskStatus.FAILED, actualTask.getStatus());
    }

    @Test
    public void executeTask_failingTaskNotIgnoringErrors_failedTaskStatus() throws Exception {
        //Assert
        List<String> expectedArguments = new ArrayList<>();
        expectedArguments.add("/c");
        expectedArguments.add("echo");
        expectedArguments.add("test");

        this.expectedExecTask.setCommand("cmd");
        this.expectedExecTask.setArguments(expectedArguments);
        this.expectedExecTask.setIgnoringErrors(false);

        this.expectedTask.setTaskDefinition(this.expectedExecTask);

        List<Task> jobTasks = new ArrayList<>();

        jobTasks.add(this.expectedTask);
        this.job.setTasks(jobTasks);
        this.expectedWorkInfo.setJob(this.job);

        //Act
        Task actualTask = execTaskExecutor.executeTask(this.expectedTask, this.expectedReport, this.expectedWorkInfo);

        //Assert
        Assert.assertNotNull(actualTask);
        Assert.assertEquals(TaskStatus.FAILED, actualTask.getStatus());
    }

    @Test
    public void executeTask_failingTaskIgnoringErrors_passedTaskStatus() throws Exception {
        //Assert
        List<String> expectedArguments = new ArrayList<>();
        expectedArguments.add("/c");
        expectedArguments.add("echo");
        expectedArguments.add("test");

        this.expectedExecTask.setCommand("cmd");
        this.expectedExecTask.setArguments(expectedArguments);
        this.expectedExecTask.setIgnoringErrors(true);

        this.expectedTask.setTaskDefinition(this.expectedExecTask);

        List<Task> jobTasks = new ArrayList<>();

        jobTasks.add(this.expectedTask);
        this.job.setTasks(jobTasks);
        this.expectedWorkInfo.setJob(this.job);

        this.expectedWorkingDir = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDirectoryPath(), this.expectedWorkInfo.getPipelineDefinitionName()).toString();
        new File(this.expectedWorkingDir).mkdirs();

        //Act
        Task actualTask = execTaskExecutor.executeTask(this.expectedTask, this.expectedReport, this.expectedWorkInfo);

        //Assert
        Assert.assertNotNull(actualTask);
        Assert.assertEquals(TaskStatus.PASSED, actualTask.getStatus());
    }

    @Test
    public void executeTask_nonExistingDirectory_failedTaskStatus() throws Exception {
        //Assert
        List<String> expectedArguments = new ArrayList<>();
        expectedArguments.add("/c");
        expectedArguments.add("echo");
        expectedArguments.add("test");

        this.expectedExecTask.setCommand("cmd");
        this.expectedExecTask.setArguments(expectedArguments);
        this.expectedExecTask.setIgnoringErrors(false);

        this.expectedTask.setTaskDefinition(this.expectedExecTask);

        List<Task> jobTasks = new ArrayList<>();

        jobTasks.add(this.expectedTask);
        this.job.setTasks(jobTasks);
        this.expectedWorkInfo.setJob(this.job);

        //Act
        Task actualTask = execTaskExecutor.executeTask(this.expectedTask, this.expectedReport, this.expectedWorkInfo);

        //Assert
        Assert.assertNotNull(actualTask);
        Assert.assertEquals(TaskStatus.FAILED, actualTask.getStatus());
    }
}