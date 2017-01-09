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

import io.hawkcd.agent.AgentConfiguration;
import io.hawkcd.agent.base.TestBase;
import io.hawkcd.agent.components.taskexecutor.TaskExecutor;
import io.hawkcd.agent.enums.TaskStatus;
import io.hawkcd.agent.models.ExecTask;
import io.hawkcd.agent.models.Job;
import io.hawkcd.agent.models.Task;
import io.hawkcd.agent.models.payload.WorkInfo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RunWith(RunOnlyOnWindows.class)
public class ExecTaskExecutorTestOnWindows extends TestBase {

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
        String expectedArguments = "/c echo test";

        this.expectedExecTask.setCommand("cmd");
        this.expectedExecTask.setArguments(expectedArguments);
        this.expectedExecTask.setIgnoringErrors(false);

        this.expectedTask.setTaskDefinition(this.expectedExecTask);

        List<Task> jobTasks = new ArrayList<>();

        jobTasks.add(this.expectedTask);
        this.job.setTasks(jobTasks);
        this.expectedWorkInfo.setJob(this.job);

        this.expectedWorkingDir = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDir(), this.expectedWorkInfo.getPipelineDefinitionName()).toString();
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
        String expectedArguments = "/c echo test";

        this.expectedExecTask.setCommand("cmhhd");
        this.expectedExecTask.setArguments(expectedArguments);
        this.expectedExecTask.setIgnoringErrors(false);

        this.expectedTask.setTaskDefinition(this.expectedExecTask);

        List<Task> jobTasks = new ArrayList<>();

        jobTasks.add(this.expectedTask);
        this.job.setTasks(jobTasks);
        this.expectedWorkInfo.setJob(this.job);

        this.expectedWorkingDir = Paths.get(AgentConfiguration.getInstallInfo().getAgentArtifactsDirectoryPath(), this.expectedWorkInfo.getPipelineDefinitionName()).toString();
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
        String expectedArguments = "/c echo test";

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
        String expectedArguments = "/c echo test";

        this.expectedExecTask.setCommand("cmd");
        this.expectedExecTask.setArguments(expectedArguments);
        this.expectedExecTask.setIgnoringErrors(true);

        this.expectedTask.setTaskDefinition(this.expectedExecTask);

        List<Task> jobTasks = new ArrayList<>();

        jobTasks.add(this.expectedTask);
        this.job.setTasks(jobTasks);
        this.expectedWorkInfo.setJob(this.job);

        this.expectedWorkingDir = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDir(), this.expectedWorkInfo.getPipelineDefinitionName()).toString();
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
        String expectedArguments = "/c echo test";

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