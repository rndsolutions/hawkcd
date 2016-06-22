package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.*;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PipelineService extends CrudService<Pipeline> implements IPipelineService {
    private IPipelineDefinitionService pipelineDefinitionService;

    public PipelineService() {
        super.setRepository(new RedisRepository(Pipeline.class));
        this.pipelineDefinitionService = new PipelineDefinitionService();
        super.setObjectType("Pipeline");
    }

    public PipelineService(IDbRepository repository, IPipelineDefinitionService pipelineDefinitionService) {
        super.setRepository(repository);
        this.pipelineDefinitionService = pipelineDefinitionService;
        super.setObjectType("Pipeline");
    }

    @Override
    public ServiceResult getById(String pipelineId) {
        return super.getById(pipelineId);
    }

    @Override
    public ServiceResult getAll() {
        return super.getAll();
    }


    @Override
    public ServiceResult add(Pipeline pipeline) {
        this.addStagesToPipeline(pipeline);
        return super.add(pipeline);
    }

    @Override
    public ServiceResult update(Pipeline pipeline) {
        return super.update(pipeline);
    }

    @Override
    public ServiceResult delete(String pipelineId) {
        return super.delete(pipelineId);
    }

    private void addStagesToPipeline(Pipeline pipeline) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        List<StageDefinition> stageDefinitions = pipelineDefinition.getStageDefinitions();
        int stagesCollectionSize = stageDefinitions.size();

        List<Stage> stages = new ArrayList<>();
        for (int i = 0; i < stagesCollectionSize; i++){
            Stage stage = new Stage();
            stage.setPipelineId(pipeline.getId());
            stages.add(stage);
            this.addJobsToPipeline(pipeline, stageDefinitions, stage);
        }
        pipeline.setStages(stages);
    }

    private void addJobsToPipeline(Pipeline pipeline, List<StageDefinition> stageDefinitions, Stage stage) {
        for (StageDefinition stageDefinition : stageDefinitions) {
            List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();
            int jobsCollectionSize = jobDefinitions.size();
            List<Job> jobs = new ArrayList<>();
            for (int i = 0; i < jobsCollectionSize; i++){
                Job job = new Job();
                job.setPipelineId(pipeline.getId());
                job.setStageId(stage.getId());
                jobs.add(job);
                this.addTasksToPipeline(pipeline.getId(), stage.getId(), job, jobDefinitions);
            }
            stage.setJobs(jobs);
        }
    }

    private void addTasksToPipeline(String pipelineId, String stageId, Job job, List<JobDefinition> jobDefinitions){
        for (JobDefinition jobDefinition : jobDefinitions) {
            List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();
            int taskCollectionSize = taskDefinitions.size();
            List<Task> tasks = new ArrayList<>();
            for (int i = 0; i < taskCollectionSize; i++){
                Task task = new Task();
                task.setJobId(job.getId());
                task.setStageId(stageId);
                task.setPipelineId(pipelineId);
                tasks.add(task);
            }
            job.setTasks(tasks);
        }
    }
}
