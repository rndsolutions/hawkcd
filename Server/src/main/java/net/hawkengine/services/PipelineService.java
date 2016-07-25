package net.hawkengine.services;

import net.hawkengine.core.utilities.EndpointConnector;
import net.hawkengine.db.DbRepositoryFactory;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.Status;
import net.hawkengine.model.enums.TaskType;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineService extends CrudService<Pipeline> implements IPipelineService {
    private static final Class CLASS_TYPE = Pipeline.class;

    private IPipelineDefinitionService pipelineDefinitionService;

    public PipelineService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        this.pipelineDefinitionService = new PipelineDefinitionService();
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    public PipelineService(IDbRepository repository, IPipelineDefinitionService pipelineDefinitionService) {
        super.setRepository(repository);
        this.pipelineDefinitionService = pipelineDefinitionService;
        super.setObjectType(CLASS_TYPE.getSimpleName());
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
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        pipeline.setPipelineDefinitionName(pipelineDefinition.getName());
        List<Pipeline> pipelines = (List<Pipeline>) this.getAll().getObject();
        Pipeline lastPipeline = pipelines
                .stream()
                .filter(p -> p.getPipelineDefinitionId().equals(pipeline.getPipelineDefinitionId()))
                .sorted((p1, p2) -> Integer.compare(p2.getExecutionId(), p1.getExecutionId()))
                .findFirst()
                .orElse(null);
        if (lastPipeline == null) {
            pipeline.setExecutionId(1);
        } else {
            pipeline.setExecutionId(lastPipeline.getExecutionId() + 1);
        }

        this.addMaterialsToPipeline(pipeline);
        this.addStagesToPipeline(pipeline);
        return super.add(pipeline);
    }

    @Override
    public ServiceResult update(Pipeline pipeline) {
        ServiceResult result = super.update(pipeline);
        EndpointConnector.passResultToEndpoint(this.getClass().getSimpleName(), "update", result);

        return result;
    }

    @Override
    public ServiceResult delete(String pipelineId) {
        return super.delete(pipelineId);
    }

    @Override
    public ServiceResult getAllNonupdatedPipelines() {
        ServiceResult result = this.getAll();
        List<Pipeline> pipelines = (List<Pipeline>) result.getObject();

        List<Pipeline> updatedPipelines = pipelines
                .stream()
                .filter(p -> !p.areMaterialsUpdated())
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        result.setObject(updatedPipelines);

        return result;
    }

    @Override
    public ServiceResult getAllUpdatedUnpreparedPipelinesInProgress() {
        ServiceResult result = this.getAll();
        List<Pipeline> pipelines = (List<Pipeline>) result.getObject();

        List<Pipeline> updatedPipelines = pipelines
                .stream()
                .filter(p -> p.areMaterialsUpdated() && !p.isPrepared() && (p.getStatus() == Status.IN_PROGRESS))
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        result.setObject(updatedPipelines);

        return result;
    }

    @Override
    public ServiceResult getAllPreparedPipelinesInProgress() {
        ServiceResult result = this.getAll();
        List<Pipeline> pipelines = (List<Pipeline>) result.getObject();

        List<Pipeline> updatedPipelines = pipelines
                .stream()
                .filter(p -> p.isPrepared() && (p.getStatus() == Status.IN_PROGRESS))
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        result.setObject(updatedPipelines);

        return result;
    }

    private void addMaterialsToPipeline(Pipeline pipeline) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        List<MaterialDefinition> materialDefinitions = pipelineDefinition.getMaterialDefinitions();

        List<Material> materials = new ArrayList<>();
        for (MaterialDefinition materialDefinition : materialDefinitions) {
            Material material = new Material();
            material.setMaterialDefinition(materialDefinition);
            materials.add(material);
        }

        pipeline.setMaterials(materials);
    }

    private void addStagesToPipeline(Pipeline pipeline) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        List<StageDefinition> stageDefinitions = pipelineDefinition.getStageDefinitions();

        List<Stage> stages = new ArrayList<>();
        for (StageDefinition stageDefinition : stageDefinitions) {
            Stage stage = new Stage();
            stage.setPipelineId(pipeline.getId());
            stage.setStageDefinitionId(stageDefinition.getId());
            stage.setStageDefinitionName(stageDefinition.getName());
            stages.add(stage);
            this.addJobsToStage(stageDefinition, stage);
        }

        pipeline.setStages(stages);
    }

    private void addJobsToStage(StageDefinition stageDefinition, Stage stage) {
        List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();

        List<Job> jobs = new ArrayList<>();
        for (JobDefinition jobDefinition : jobDefinitions) {
            Job job = new Job();
            job.setPipelineId(stage.getPipelineId());
            job.setJobDefinitionId(jobDefinition.getId());
            job.setJobDefinitionName(jobDefinition.getName());
            job.setStageId(stage.getId());
            job.setJobDefinitionName(jobDefinition.getName());
            jobs.add(job);
            this.addTasksToJob(jobDefinition, job);
        }

        stage.setJobs(jobs);
    }

    private void addTasksToJob(JobDefinition jobDefinition, Job job) {
        List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();

        List<Task> tasks = new ArrayList<>();
        for (TaskDefinition taskDefinition : taskDefinitions) {

            Task task = new Task();
            if (taskDefinition.getType() == TaskType.FETCH_MATERIAL) {
                FetchMaterialTask fetchMaterialTask = (FetchMaterialTask) taskDefinition;
                task.setMaterialDefinition(fetchMaterialTask.getMaterialDefinition());
            }
            task.setJobId(job.getId());
            task.setStageId(job.getStageId());
            task.setPipelineId(job.getPipelineId());
            task.setTaskDefinition(taskDefinition);
            task.setRunIfCondition(taskDefinition.getRunIfCondition());
            tasks.add(task);
        }

        job.setTasks(tasks);
    }
}
