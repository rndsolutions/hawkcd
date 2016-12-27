package io.hawkcd.services;

import io.hawkcd.model.*;

import java.util.ArrayList;
import java.util.List;

public class PipelineBuilder {
    public static Pipeline buildPipeline(PipelineDefinition pipelineDefinition) {
        Pipeline pipeline = new Pipeline(pipelineDefinition);

        StageRun stageRun = new StageRun();
        for (StageDefinition stageDefinition : pipelineDefinition.getStageDefinitions()) {
            Stage stage = buildStage(stageDefinition, pipeline.getId());
            stageRun.addStage(stage);
        }

        pipeline.addStageRun(stageRun);

        return pipeline;
    }

    public static Stage buildStage(StageDefinition stageDefinition, String pipelineId) {
        Stage stage = new Stage(stageDefinition, pipelineId);

        for (JobDefinition jobDefinition : stageDefinition.getJobDefinitions()) {
            Job job = buildJob(jobDefinition, stage.getId(), pipelineId);
            stage.getJobs().add(job);
        }

        return stage;
    }

    public static Stage buildStage(StageDefinition stageDefinition, String pipelineId, ArrayList<String> jobDefinitionIdsForStageRerun) {
        Stage stage = new Stage(stageDefinition, pipelineId);

        List<JobDefinition> jobDefinitions = getJobDefinitionsForStageRerun(stageDefinition.getJobDefinitions(), jobDefinitionIdsForStageRerun);

        for (JobDefinition jobDefinition : jobDefinitions) {
            Job job = buildJob(jobDefinition, stage.getId(), pipelineId);
            stage.getJobs().add(job);
        }

        return stage;
    }

    public static Job buildJob(JobDefinition jobDefinition, String stageId, String pipelineId) {
        Job job = new Job(jobDefinition, stageId, pipelineId);

        for (TaskDefinition taskDefinition : jobDefinition.getTaskDefinitions()) {
            Task task = new Task(taskDefinition, job.getId(), stageId, pipelineId);
            job.getTasks().add(task);
        }

        return job;
    }

    private static List<JobDefinition> getJobDefinitionsForStageRerun(List<JobDefinition> jobDefinitions, ArrayList<String> jobDefinitionIdsForStageRerun) {
        List<JobDefinition> jobDefinitionsForStageRerun = new ArrayList<>();
        for (JobDefinition jobDefinition : jobDefinitions) {
            if (jobDefinitionIdsForStageRerun.contains(jobDefinition.getId())) {
                jobDefinitionsForStageRerun.add(jobDefinition);
            }
        }

        return jobDefinitionsForStageRerun;
    }
}
