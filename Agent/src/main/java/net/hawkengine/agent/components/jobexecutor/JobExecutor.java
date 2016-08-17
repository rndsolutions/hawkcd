package net.hawkengine.agent.components.jobexecutor;

import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.components.taskexecutor.ITaskExecutor;
import net.hawkengine.agent.components.taskexecutor.factories.TaskExecutorFactory;
import net.hawkengine.agent.constants.ConfigConstants;
import net.hawkengine.agent.constants.MessageConstants;
import net.hawkengine.agent.enums.JobStatus;
import net.hawkengine.agent.enums.RunIf;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.EnvironmentVariable;
import net.hawkengine.agent.models.Job;
import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.payload.WorkInfo;

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
        currentJob = workInfo.getJob();
        String jobStarted = String.format(MessageConstants.JOB_STARTED_ON_AGENT, workInfo.getPipelineDefinitionName());
        LOGGER.info(jobStarted);
        jobStarted = ConfigConstants.CONSOLE_YELLOW + jobStarted;
        this.currentJob.getReport().append(jobStarted + "\n");


        //this.printEnvironmentVariables(workInfo.getEnvironmentVariables());



//        this.currentJob.appendToJobResult(String.format("Start to prepare %s/%s/%s/%s/%s on %s %s",
//                this.getCurrentJob().getPipelineDefinitionName(),
//                this.getCurrentJob().getPipelineExecutionId(),
//                this.getCurrentJob().getStageDefinitionName(),
//                this.getCurrentJob().getStageExecutionId(),
//                this.getCurrentJob().getJobName(),
//                AgentConfiguration.getAgentInfo().getName(),
//                AgentConfiguration.getInstallInfo().getAgentPipelinesDir()), true);
//        this.currentJob.appendToJobResult(String.format(MessageConstants.JOB_STARTED_ON_AGENT, workInfo.getJob().getName()), true);
//        this.currentJob.appendToJobResult(String.format("Current job status: %s", this.currentJob.getStatus()), true);

        this.createPipelineDirs(workInfo);

        List<Task> tasksForExecution = workInfo.getJob().getTasks();

        boolean hasAnyTaskFailed = false;

        for (int i = 0; i < tasksForExecution.size(); i++) {

            Task currentTask = tasksForExecution.get(i);
            boolean isFirstTask = i == 0;

            // TODO: Job should be RUNNING
            if (this.currentJob.getStatus() == JobStatus.RUNNING) {
                if ((currentTask.getRunIfCondition() == RunIf.PASSED) || (currentTask.getRunIfCondition() == RunIf.ANY) || isFirstTask) {

//                    this.currentJob.appendToJobResult(String.format(MessageConstants.TASK_STARTED, currentTask.getType(), currentTask.getId()), true);

                    LOGGER.info(String.format(MessageConstants.TASK_STARTED, currentTask.getType(), i));

                    this.taskExecutor = TaskExecutorFactory.create(currentTask.getTaskDefinition());
                    Task currentInfo = this.taskExecutor.executeTask(currentTask, this.currentJob.getReport(), workInfo);

//                    this.currentJob.getTaskExecutionInfo().add(currentInfo);
                    LOGGER.info(String.format(MessageConstants.TASK_COMPLETED, currentTask.getType(), i, currentInfo.getStatus()));
                }
            } else {
                if ((currentTask.getRunIfCondition() == RunIf.FAILED) || (currentTask.getRunIfCondition() == RunIf.ANY)) {

//                    this.currentJob.appendToJobResult(String.format(MessageConstants.TASK_STARTED, currentTask.getType(), currentTask.getId()), true);
                    LOGGER.info(String.format(MessageConstants.TASK_STARTED, currentTask.getType(), i));

                    this.taskExecutor = TaskExecutorFactory.create(currentTask.getTaskDefinition());
                    Task currentInfo = this.taskExecutor.executeTask(currentTask, this.currentJob.getReport(),workInfo);

//                    this.currentJob.getTaskExecutionInfo().add(currentInfo);
                    LOGGER.info(String.format(MessageConstants.TASK_COMPLETED, currentTask.getType(), i, currentInfo.getStatus()));
                }
            }

            if (!hasAnyTaskFailed) {
                hasAnyTaskFailed = this.hasAnyTaskFailed(this.currentJob.getTasks());
            }

            if (hasAnyTaskFailed) {
                this.currentJob.setStatus(JobStatus.FAILED);
            }
        }

        if (this.currentJob.getStatus() != JobStatus.FAILED){
            this.currentJob.setStatus(JobStatus.PASSED);
        }

//        this.currentJob.setState(ExecutionState.COMPLETED);
        //this.currentJob.setEndTime(LocalDateTime.now());

//        this.LOGGER.info(String.format(MessageConstants.JOB_COMPLETED, workInfo.getJob().getName(), this.currentJob.getStatus()));
//        this.currentJob.appendToJobResult(String.format("Current job status: %s", this.currentJob.getStatus()), true);
    }

    public void initJobExecutionInfo(WorkInfo workInfo) {
//        this.currentJob = new JobExecutionInfo();
//        this.currentJob.setPipelineDefinitionName(workInfo.getPipelineDefinitionName());
//        this.currentJob.setPipelineId(workInfo.getPipelineId());
//        this.currentJob.setPipelineLabel(workInfo.getLabelTemplate());
//        this.currentJob.setPipelineExecutionId(workInfo.getPipelineExecutionID());
//        this.currentJob.setStageDefinitionName(workInfo.getStageDefinitionName());
//        this.currentJob.setStageId(workInfo.getStageId());
//        this.currentJob.setStageExecutionId(workInfo.getStageExecutionID());
//        this.currentJob.setJobName(workInfo.getJob().getName());
//        this.currentJob.setJobId(workInfo.getJob().getId());
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
                this.LOGGER.info(message);
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
