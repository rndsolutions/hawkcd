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

package io.hawkcd.scheduler;

import io.hawkcd.Config;
import io.hawkcd.utilities.constants.LoggerMessages;
import io.hawkcd.model.Environment;
import io.hawkcd.model.enums.PipelineStatus;
import io.hawkcd.model.Stage;
import io.hawkcd.model.enums.TaskType;
import io.hawkcd.services.PipelineDefinitionService;
import io.hawkcd.services.PipelineService;
import io.hawkcd.services.interfaces.IPipelineDefinitionService;
import io.hawkcd.services.interfaces.IPipelineService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.hawkcd.model.EnvironmentVariable;
import io.hawkcd.model.ExecTask;
import io.hawkcd.model.FetchArtifactTask;
import io.hawkcd.model.FetchMaterialTask;
import io.hawkcd.model.Job;
import io.hawkcd.model.JobDefinition;
import io.hawkcd.model.Material;
import io.hawkcd.model.Pipeline;
import io.hawkcd.model.PipelineDefinition;
import io.hawkcd.model.StageDefinition;
import io.hawkcd.model.Task;
import io.hawkcd.model.TaskDefinition;

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

                Thread.sleep(Config.getConfiguration().getPipelineSchedulerPollInterval() * 1000);
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
                .filter(p -> p.areMaterialsUpdated() && (p.getStatus() == PipelineStatus.IN_PROGRESS) && !(p.isPrepared()))
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        return filteredPipelines;
    }

    public Pipeline preparePipeline(Pipeline pipelineToPrepare) {
        this.currentPipeline = pipelineToPrepare;
        String pipelineDefinitionId = pipelineToPrepare.getPipelineDefinitionId();
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();

        List<StageDefinition> stages = pipelineDefinition.getStageDefinitions();
        List<EnvironmentVariable> pipelineDefinitionEnvironmentVariables = pipelineDefinition.getEnvironmentVariables();

        pipelineToPrepare.setPipelineDefinitionId(pipelineDefinitionId);
        pipelineToPrepare.setEnvironmentVariables(pipelineDefinitionEnvironmentVariables);
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
            if (currentTask.getType() == TaskType.FETCH_MATERIAL) {
                FetchMaterialTask fetchMaterialTask = (FetchMaterialTask) taskDefinitions.get(i);
                for (Material material : this.currentPipeline.getMaterials()) {
                    if (material.getMaterialDefinition().getId().equals(fetchMaterialTask.getMaterialDefinitionId())) {
                        fetchMaterialTask.setMaterialDefinition(material.getMaterialDefinition());
                        currentTask.setTaskDefinition(fetchMaterialTask);
                        break;
                    }
                }
            } else if (currentTask.getType() == TaskType.FETCH_ARTIFACT) {
                FetchArtifactTask fetchArtifactTask = (FetchArtifactTask) taskDefinitions.get(i);
                if (fetchArtifactTask.shouldUseLatestRun()) {
                    Pipeline currentPipeline = (Pipeline) this.pipelineService.getLastRun(fetchArtifactTask.getDesignatedPipelineDefinitionId()).getObject();
                    fetchArtifactTask.setDesignatedPipelineExecutionId(Integer.toString(currentPipeline.getExecutionId()));
                    currentTask.setTaskDefinition(fetchArtifactTask);
                }
            }

            currentTask.setRunIfCondition(taskDefinitions.get(i).getRunIfCondition());
            tasks.set(i, currentTask);
        }

        return tasks;
    }
}
