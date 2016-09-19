package net.hawkengine.core.pipelinescheduler;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.StageStatus;
import net.hawkengine.model.enums.Status;
import net.hawkengine.model.enums.TaskType;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PipelinePreparer implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(PipelinePreparer.class);

    private EnvironmentVariableService environmentVariableService;
    private IPipelineDefinitionService pipelineDefinitionService;
    private IPipelineService pipelineService;
    private Pipeline currentPipeline;

    public PipelinePreparer() {
        this.environmentVariableService = new EnvironmentVariableService();
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.pipelineService = new PipelineService();
    }

    public PipelinePreparer(IPipelineService pipelineService, IPipelineDefinitionService pipelineDefinitionService) {
        this.pipelineDefinitionService = pipelineDefinitionService;
        this.pipelineService = pipelineService;
    }

    @Override
    public void run() {
        LOGGER.info(String.format(LoggerMessages.WORKER_STARTED, PipelinePreparer.class.getSimpleName()));
        try {
            while (true) {
                List<Pipeline> filteredPipelines = (List<Pipeline>) this.pipelineService.getAllUpdatedUnpreparedPipelinesInProgress().getObject();

                for (Pipeline pipeline : filteredPipelines) {
                    this.preparePipeline(pipeline);
                    this.preparePipelineEnvironmentVariables(pipeline);
                    this.pipelineService.update(pipeline);
                    LOGGER.info(pipeline.getPipelineDefinitionName() + " prepared.");
                }

                Thread.sleep(ServerConfiguration.getConfiguration().getPipelineSchedulerPollInterval() * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Pipeline preparePipeline(Pipeline pipelineToPrepare) {
        this.currentPipeline = pipelineToPrepare;
        String pipelineDefinitionId = pipelineToPrepare.getPipelineDefinitionId();
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();

        if (pipelineToPrepare.getStatus() == Status.IN_PROGRESS) {
            pipelineToPrepare.setStages(this.preparePipelineStages(pipelineDefinition.getStageDefinitions(), pipelineToPrepare));
        } else {
            pipelineToPrepare.setStages(this.preparePipelineStagesToRerun(pipelineDefinition.getStageDefinitions(), pipelineToPrepare));
        }

        pipelineToPrepare.setPipelineDefinitionId(pipelineDefinitionId);
        pipelineToPrepare.setEnvironmentVariables(pipelineDefinition.getEnvironmentVariables());
        pipelineToPrepare.setEnvironments(pipelineDefinition.getEnvironments());
        pipelineToPrepare.setPrepared(true);

        return pipelineToPrepare;
    }

    public Pipeline preparePipelineEnvironmentVariables(Pipeline pipeline) {
        List<EnvironmentVariable> pipelineVariables = pipeline.getEnvironmentVariables();

        for (Stage stage : pipeline.getStages()) {
            List<EnvironmentVariable> stageVariables = stage.getEnvironmentVariables();

            for (Job job : stage.getJobs()) {
                List<EnvironmentVariable> jobVariables = job.getEnvironmentVariables();
                List<EnvironmentVariable> overridenVariables = this.environmentVariableService.getOverriddenVariables(jobVariables, stageVariables, pipelineVariables);

                for (Task task : job.getTasks()) {
                    if (task.getTaskDefinition().getType() == TaskType.EXEC) {
                        ExecTask execTask = (ExecTask) task.getTaskDefinition();
                        String arguments = this.environmentVariableService.replaceVariablesInArguments(overridenVariables, execTask.getArguments());
                        execTask.setArguments(arguments);
                        task.setTaskDefinition(execTask);
                    }
                }
            }
        }

        return pipeline;
    }

    public List<Stage> preparePipelineStages(List<StageDefinition> stageDefinitions, Pipeline pipeline) {
        List<Stage> stages = pipeline.getStages();

        int stageDefinitionCollectionSize = stageDefinitions.size();

        for (int i = 0; i < stageDefinitionCollectionSize; i++) {
            Stage currentStage = stages.get(i);
            currentStage.setStageDefinitionId(stageDefinitions.get(i).getId());
            currentStage.setEnvironmentVariables(stageDefinitions.get(i).getEnvironmentVariables());
            currentStage.setPipelineId(pipeline.getId());
            currentStage.setJobs(this.preparePipelineJobs(stageDefinitions.get(i).getJobDefinitions(), currentStage));
            currentStage.setTriggeredManually(stageDefinitions.get(i).isTriggeredManually());

            stages.set(i, currentStage);
        }

        return stages;
    }

    public List<Stage> preparePipelineStagesToRerun(List<StageDefinition> stageDefinitions, Pipeline pipeline) {
        int numberOfStageDefinitions = stageDefinitions.size();

        List<Stage> stages = pipeline.getStages();
        Stage stageToRerun = stages.remove(stages.size() - 1);
        int stageExecutionId = stageToRerun.getExecutionId();
        int stageToRerunIndex = this.getStageToRerunIndex(stageToRerun, stageDefinitions, numberOfStageDefinitions);

        for (int i = 0; i < numberOfStageDefinitions; i++) {
            StageDefinition currentStageDefinition = stageDefinitions.get(i);

            Stage currentStage = new Stage();
            if (i == stageToRerunIndex) {
                currentStage = stageToRerun;
                currentStage.setTriggeredManually(false);
            } else {
                if (i < stageToRerunIndex) {
                    currentStage.setStatus(StageStatus.UNSCHEDULED);
                }

                currentStage.setPipelineId(pipeline.getId());
                currentStage.setTriggeredManually(currentStageDefinition.isTriggeredManually());
                for (int j = 0; j < currentStageDefinition.getJobDefinitions().size(); j++) {
                    currentStage.getJobs().add(new Job());
                }

                currentStage.setJobs(this.preparePipelineJobs(currentStageDefinition.getJobDefinitions(), currentStage));
            }

            currentStage.setStageDefinitionId(currentStageDefinition.getId());
            currentStage.setStageDefinitionName(currentStageDefinition.getName());
            currentStage.setExecutionId(stageExecutionId);
            currentStage.setEnvironmentVariables(currentStageDefinition.getEnvironmentVariables());

            stages.add(currentStage);
        }

        return stages;
    }

    public List<Job> preparePipelineJobs(List<JobDefinition> jobDefinitions, Stage stage) {
        List<Job> jobs = stage.getJobs();

        int jobDefinitionCollectionSize = jobDefinitions.size();

        for (int i = 0; i < jobDefinitionCollectionSize; i++) {
            Job currentJob = jobs.get(i);
            currentJob.setJobDefinitionId(jobDefinitions.get(i).getId());
            currentJob.setJobDefinitionName(jobDefinitions.get(i).getName());
            currentJob.setStageId(stage.getId());
            currentJob.setPipelineId(stage.getPipelineId());
            currentJob.setEnvironmentVariables(jobDefinitions.get(i).getEnvironmentVariables());
            currentJob.setResources(jobDefinitions.get(i).getResources());
            currentJob.setPipelineId(stage.getPipelineId());
            currentJob.setTasks(this.prepareTasks(jobDefinitions.get(i).getTaskDefinitions(), currentJob));

            jobs.set(i, currentJob);
        }

        return jobs;
    }

    public List<Task> prepareTasks(List<TaskDefinition> taskDefinitions, Job job) {
        List<Task> tasks = new ArrayList<>();

        int taskDefinitionCollectionSize = taskDefinitions.size();

        for (int i = 0; i < taskDefinitionCollectionSize; i++) {
            tasks.add(new Task());
            Task currentTask = tasks.get(i);
            currentTask.setTaskDefinition(taskDefinitions.get(i));
            currentTask.setJobId(job.getId());
            currentTask.setStageId(job.getStageId());
            currentTask.setPipelineId(job.getPipelineId());
            currentTask.setType(taskDefinitions.get(i).getType());
            if (currentTask.getType() == TaskType.FETCH_MATERIAL) {
                FetchMaterialTask fetchMaterialTask = (FetchMaterialTask) taskDefinitions.get(i);
                for (Material material : this.currentPipeline.getMaterials()) {
                    if (material.getMaterialDefinition().getId().equals(fetchMaterialTask.getMaterialDefinitionId())) {
                        fetchMaterialTask.setMaterialDefinition(material.getMaterialDefinition());
                        currentTask.setTaskDefinition(fetchMaterialTask);
                        break;
                    }
                }
            }
            currentTask.setRunIfCondition(taskDefinitions.get(i).getRunIfCondition());
            tasks.set(i, currentTask);
        }

        return tasks;
    }

    private int getStageToRerunIndex(Stage stageToRerun, List<StageDefinition> stageDefinitions, int numberOfStageDefinitions) {
        int stageToRerunIndex = -1;
        for (int i = 0; i < numberOfStageDefinitions; i++) {
            if (stageToRerun.getStageDefinitionId().equals(stageDefinitions.get(i).getId())) {
                stageToRerunIndex = i;
                break;
            }
        }

        return stageToRerunIndex;
    }
}
