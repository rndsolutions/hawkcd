package net.hawkengine.core.pipelinescheduler;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.Status;
import net.hawkengine.model.enums.TaskType;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelinePreparer implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(PipelinePreparer.class);

    private EnvironmentVariableService environmentVariableService;
    private IPipelineDefinitionService pipelineDefinitionService;
    private IPipelineService pipelineService;

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
        LOGGER.info(String.format(LoggerMessages.WORKER_STARTED, "Pipeline Preparer"));
        try {
            while (true) {
                List<Pipeline> filteredPipelines = (List<Pipeline>) this.pipelineService.getAllUpdatedUnpreparedPipelinesInProgress().getObject();

                for (Pipeline pipeline : filteredPipelines) {
                    this.preparePipeline(pipeline);
                    this.preparePipelineEnvironmentVariables(pipeline);
                    this.pipelineService.update(pipeline);
                    LOGGER.info(pipeline.getPipelineDefinitionName() + " prepared.");
                }

                Thread.sleep(ServerConfiguration.getConfiguration().getMaterialTrackerPollInterval() * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // TODO: Replace with method form PipelineService
    public List<Pipeline> getAllUpdatedPipelines() {
        List<Pipeline> pipelines = (List<Pipeline>) this.pipelineService.getAll().getObject();

        List<Pipeline> filteredPipelines = pipelines
                .stream()
                .filter(p -> p.areMaterialsUpdated() && (p.getStatus() == Status.IN_PROGRESS) && !(p.isPrepared()))
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        return filteredPipelines;
    }

    public Pipeline preparePipeline(Pipeline pipelineToPrepare) {
        String pipelineDefinitionId = pipelineToPrepare.getPipelineDefinitionId();
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();

        List<StageDefinition> stages = pipelineDefinition.getStageDefinitions();
        List<Environment> pipelineDefinitionEnvironments = pipelineDefinition.getEnvironments();
        List<EnvironmentVariable> pipelineDefinitionEnvironmentVariables = pipelineDefinition.getEnvironmentVariables();

        pipelineToPrepare.setPipelineDefinitionId(pipelineDefinitionId);
        pipelineToPrepare.setEnvironmentVariables(pipelineDefinitionEnvironmentVariables);
        pipelineToPrepare.setEnvironments(pipelineDefinitionEnvironments);
        pipelineToPrepare.setStages(this.preparePipelineStages(stages, pipelineToPrepare));
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
                        String arguments = this.environmentVariableService.replaceVariablesInArguments(execTask.getArguments(), overridenVariables);
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

            stages.set(i, currentStage);
        }

        return stages;
    }

    public List<Job> preparePipelineJobs(List<JobDefinition> jobDefinitions, Stage stage) {
        List<Job> jobs = stage.getJobs();

        int jobDefinitionCollectionSize = jobDefinitions.size();

        for (int i = 0; i < jobDefinitionCollectionSize; i++) {
            Job currentJob = jobs.get(i);
            currentJob.setJobDefinitionId(jobDefinitions.get(i).getId());
            currentJob.setEnvironmentVariables(jobDefinitions.get(i).getEnvironmentVariables());
            currentJob.setResources(jobDefinitions.get(i).getResources());
            currentJob.setStageId(stage.getId());
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
            currentTask.setRunIfCondition(taskDefinitions.get(i).getRunIfCondition());
            tasks.set(i, currentTask);
        }

        return tasks;
    }
}
