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

package io.hawkcd.agent.components.jobexecutor;

import io.hawkcd.agent.AgentConfiguration;
import io.hawkcd.agent.components.taskexecutor.ITaskExecutor;
import io.hawkcd.agent.components.taskexecutor.factories.TaskExecutorFactory;
import io.hawkcd.agent.constants.MessageConstants;
import io.hawkcd.agent.enums.JobStatus;
import io.hawkcd.agent.enums.RunIf;
import io.hawkcd.agent.enums.TaskStatus;
import io.hawkcd.agent.models.EnvironmentVariable;
import io.hawkcd.agent.models.Job;
import io.hawkcd.agent.models.Task;
import io.hawkcd.agent.models.payload.WorkInfo;
import io.hawkcd.agent.utilities.ReportAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class JobExecutor implements IJobExecutor {
    private static final Logger LOGGER = LogManager.getLogger(JobExecutor.class);

    private ITaskExecutor taskExecutor;
    private Job currentJob;

    @Override
    public Job getCurrentJob() {
        return this.currentJob;
    }

    @Override
    public void setCurrentJob(Job job) {
        this.currentJob = job;
    }

    @Override
    public void executeJob(WorkInfo workInfo) {
        this.currentJob = workInfo.getJob();

        String jobStarted = String.format(MessageConstants.JOB_STARTED_ON_AGENT, this.currentJob.getJobDefinitionName(), AgentConfiguration.getAgentInfo().getHostName());
        LOGGER.info(jobStarted);
        ReportAppender.appendStartedMessage(jobStarted, this.currentJob.getReport(), Job.class);

        //this.printEnvironmentVariables(workInfo.getEnvironmentVariables());

//        this.currentJob.appendToJobResult(String.format("Start to prepare %s/%s/%s/%s/%s on %s %s",
//                this.getCurrentJob().getDesignatedPipelineDefinitionName(),
//                this.getCurrentJob().getPipelineExecutionId(),
//                this.getCurrentJob().getPipelineExecutionId(),
//                this.getCurrentJob().getStageExecutionId(),
//                this.getCurrentJob().getJobName(),
//                AgentConfiguration.getAgentInfo().getName(),
//                AgentConfiguration.getInstallInfo().getAgentPipelinesDir()), true);
//        this.currentJob.appendToJobResult(String.format(MessageConstants.JOB_STARTED_ON_AGENT, workInfo.getJobDefinitionName().getName()), true);
//        this.currentJob.appendToJobResult(String.format("Current job status: %s", this.currentJob.getStatus()), true);

        this.createPipelineDirs(workInfo);

        List<Task> tasksForExecution = workInfo.getJob().getTasks();
        boolean hasAnyTaskFailed = false;
        for (int i = 0; i < tasksForExecution.size(); i++) {
            Task currentTask = tasksForExecution.get(i);
            boolean isFirstTask = i == 0;
            if (this.currentJob.getStatus() == JobStatus.RUNNING) {
                if ((currentTask.getRunIfCondition() == RunIf.PASSED) || (currentTask.getRunIfCondition() == RunIf.ANY) || isFirstTask) {
                    String taskStarted = String.format(MessageConstants.TASK_STARTED, i, currentTask.getType());
                    LOGGER.info(taskStarted);
                    ReportAppender.appendStartedMessage(taskStarted, this.currentJob.getReport(), Task.class);

                    this.taskExecutor = TaskExecutorFactory.create(currentTask.getTaskDefinition());
                    Task currentInfo = this.taskExecutor.executeTask(currentTask, this.currentJob.getReport(), workInfo);

                    String taskCompleted = String.format(MessageConstants.TASK_COMPLETED, i, currentTask.getStatus());
                    LOGGER.info(taskCompleted);
                    ReportAppender.appendCompletedMessage(taskCompleted, this.currentJob.getReport(), currentInfo.getStatus());
                }
            } else {
                if ((currentTask.getRunIfCondition() == RunIf.FAILED) || (currentTask.getRunIfCondition() == RunIf.ANY)) {
                    String taskStarted = String.format(MessageConstants.TASK_STARTED, i, currentTask.getType());
                    LOGGER.info(taskStarted);
                    ReportAppender.appendStartedMessage(taskStarted, this.currentJob.getReport(), Task.class);

                    this.taskExecutor = TaskExecutorFactory.create(currentTask.getTaskDefinition());
                    Task currentInfo = this.taskExecutor.executeTask(currentTask, this.currentJob.getReport(), workInfo);

                    String taskCompleted = String.format(MessageConstants.TASK_COMPLETED, i, currentTask.getStatus());
                    LOGGER.info(taskCompleted);
                    ReportAppender.appendCompletedMessage(taskCompleted, this.currentJob.getReport(), currentInfo.getStatus());
                }
            }

            if (!hasAnyTaskFailed) {
                hasAnyTaskFailed = this.hasAnyTaskFailed(this.currentJob.getTasks());
            }

            if (hasAnyTaskFailed) {
                this.currentJob.setStatus(JobStatus.FAILED);
            }
        }

        if (this.currentJob.getStatus() != JobStatus.FAILED) {
            this.currentJob.setStatus(JobStatus.PASSED);
        }


        String jobCompleted = String.format(MessageConstants.JOB_COMPLETED_WITH_STATUS, this.currentJob.getJobDefinitionName(), this.currentJob.getStatus());
        LOGGER.info(jobCompleted);
        ReportAppender.appendCompletedMessage(jobCompleted, this.currentJob.getReport(), this.currentJob.getStatus());
    }

    public void initJobExecutionInfo(WorkInfo workInfo) {
//        this.currentJob = new JobExecutionInfo();
//        this.currentJob.setDesignatedPipelineDefinitionName(workInfo.getDesignatedPipelineDefinitionName());
//        this.currentJob.setPipelineId(workInfo.getPipelineId());
//        this.currentJob.setPipelineLabel(workInfo.getLabelTemplate());
//        this.currentJob.setPipelineExecutionId(workInfo.getPipelineExecutionID());
//        this.currentJob.setPipelineExecutionId(workInfo.getPipelineExecutionId());
//        this.currentJob.setStageId(workInfo.getStageId());
//        this.currentJob.setStageExecutionId(workInfo.getStageExecutionID());
//        this.currentJob.setJobName(workInfo.getJobDefinitionName().getName());
//        this.currentJob.setJobId(workInfo.getJobDefinitionName().getId());
//
//        this.currentJob.setStatus(ExecutionStatus.PASSED);
//        this.currentJob.setState(ExecutionState.RUNNING);
//        this.currentJob.setStart(LocalDateTime.now());
    }

    public void resetJobExecutionInfo() {
        this.currentJob = null;
    }

    private void printEnvironmentVariables(List<EnvironmentVariable> environmentVariables) {
        if (environmentVariables != null) {
            for (EnvironmentVariable environmentVariable : environmentVariables) {
                String variableValue = environmentVariable.isSecured() ? "******" : environmentVariable.getValue();
                String message = environmentVariable.getName() + " - " + variableValue;
                LOGGER.info(message);
//                this.currentJob.appendToJobResult(message, true);
            }
        }
    }

    private void createPipelineDirs(WorkInfo workInfo) {
        String pipelinesDirPath = AgentConfiguration.getInstallInfo().getAgentPipelinesDir();
        File pipelinesDirFile = new File(pipelinesDirPath);
        pipelinesDirFile.mkdir();

        String currentPipelineDirPath = Paths.get(pipelinesDirPath, workInfo.getPipelineDefinitionName()).toString();
        File currentPipelineDirFile = new File(currentPipelineDirPath);
        currentPipelineDirFile.mkdir();
    }

    private boolean hasAnyTaskFailed(List<Task> tasks) {

        for (int i = 0; i < tasks.size(); i++) {
            Task currentTask = tasks.get(i);

            //TODO: implement logic for ignoring errors
            if ((currentTask.getStatus() == TaskStatus.FAILED)) {
                return true;
            }
        }
        return false;
    }
}
