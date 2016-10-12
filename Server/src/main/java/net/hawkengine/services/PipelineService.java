package net.hawkengine.services;

import net.hawkengine.db.DbRepositoryFactory;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.*;
import net.hawkengine.model.dto.PipelineDto;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.model.enums.PipelineStatus;
import net.hawkengine.model.enums.StageStatus;
import net.hawkengine.model.enums.TaskType;
import net.hawkengine.services.interfaces.IMaterialDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import net.hawkengine.ws.EndpointConnector;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class PipelineService extends CrudService<Pipeline> implements IPipelineService {
    private static final Class CLASS_TYPE = Pipeline.class;
    private static final Logger LOGGER = Logger.getLogger(PipelineService.class.getName());

    //public static Lock lock = new ReentrantLock();

    private IPipelineDefinitionService pipelineDefinitionService;
    private IMaterialDefinitionService materialDefinitionService;

    public PipelineService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.materialDefinitionService = new MaterialDefinitionService();
    }

    public PipelineService(IDbRepository repository, IPipelineDefinitionService pipelineDefinitionService, IMaterialDefinitionService materialDefinitionService) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.pipelineDefinitionService = pipelineDefinitionService;
        this.materialDefinitionService = materialDefinitionService;
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
    public  ServiceResult add(Pipeline pipeline) {
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
            pipelineDefinition.setRevisionCount(pipelineDefinition.getRevisionCount() + 1);
            List<EnvironmentVariable> environmentVariables = pipelineDefinition.getEnvironmentVariables();
            EnvironmentVariable environmentVariable = environmentVariables.stream().filter(e -> e.getKey().equals("COUNT")).findFirst().orElse(null);

            int envAutoIncrement = Integer.parseInt(environmentVariable.getValue()) + 1;

            environmentVariable.setValue(String.valueOf(envAutoIncrement));
            environmentVariables.stream().filter(env -> env.getKey().equals(environmentVariable.getKey())).forEach(env -> {
                env.setValue(environmentVariable.getValue());
            });
            pipelineDefinition.setEnvironmentVariables(environmentVariables);
            ServiceResult result = this.pipelineDefinitionService.update(pipelineDefinition);
            EndpointConnector.passResultToEndpoint("PipelineDefinitionService", "update", result);
        }

        this.addMaterialsToPipeline(pipeline);
        this.addStagesToPipeline(pipeline);

        return super.add(pipeline);
    }

    @Override
    public  ServiceResult update(Pipeline pipeline) {
        ServiceResult result = super.update(pipeline);
        EndpointConnector.passResultToEndpoint(this.getClass().getSimpleName(), "update", result);

        return result;
    }

    @Override
    public  ServiceResult delete(String pipelineId) {
        return super.delete(pipelineId);
    }

    @Override
    public ServiceResult getAllByDefinitionId(String pipelineDefinitionId) {
        ServiceResult result = this.getAll();
        List<Pipeline> pipelines = (List<Pipeline>) result.getObject();

        List<Pipeline> filteredPipelines = pipelines
                .stream()
                .filter(p -> p.getPipelineDefinitionId().equals(pipelineDefinitionId))
                .collect(Collectors.toList());

        result.setObject(filteredPipelines);

        return result;
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
                .filter(p -> p.areMaterialsUpdated() && !p.isPrepared() && (p.getStatus() == PipelineStatus.IN_PROGRESS))
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
                .filter(p -> p.isPrepared() && (p.getStatus() == PipelineStatus.IN_PROGRESS))
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        result.setObject(updatedPipelines);

        return result;
    }

    @Override
    public ServiceResult getAllPreparedAwaitingPipelines() {
        ServiceResult result = this.getAll();
        List<Pipeline> pipelines = (List<Pipeline>) result.getObject();

        List<Pipeline> updatedPipelines = pipelines
                .stream()
                .filter(p -> p.isPrepared() && (p.getStatus() == PipelineStatus.AWAITING))
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        result.setObject(updatedPipelines);

        return result;
    }

    @Override
    public ServiceResult getLastRun(String pipelineDefinitionId) {
        ServiceResult result = this.getAllByDefinitionId(pipelineDefinitionId);
        List<Pipeline> pipelines = (List<Pipeline>) result.getObject();

        Pipeline lastRun = null;
        int lastExecutionId = 0;
        for (Pipeline pipeline : pipelines) {
            if (pipeline.getExecutionId() > lastExecutionId) {
                lastRun = pipeline;
                lastExecutionId = pipeline.getExecutionId();
            }
        }

        result.setObject(lastRun);

        return result;
    }

//    @Override
//    public ServiceResult getAllPipelineHistoryDTOs(String pipelineDefinitionId) {
//        ServiceResult result = this.getAllByDefinitionId(pipelineDefinitionId);
//        List<Pipeline> pipelines = (List<Pipeline>) result.getObject();
//
//        List<PipelineDto> pipelineDtos = new ArrayList<>();
//        for (Pipeline pipeline : pipelines) {
//            PipelineDto pipelineDto = new PipelineDto();
//            pipelineDto.constructHistoryPipelineDto(pipeline);
//            pipelineDtos.add(pipelineDto);
//        }
//
//        result.setObject(pipelineDtos);
//
//        return result;
//    }

    @Override
    public ServiceResult getAllPipelineHistoryDTOs(String pipelineDefinitionId, Integer numberOfPipelines) {
        return this.getAllPipelineHistoryDTOs(pipelineDefinitionId, numberOfPipelines, null);
    }

    @Override
    public ServiceResult getAllPipelineHistoryDTOs(String pipelineDefinitionId, Integer numberOfPipelines, String pipelineId) {
        ServiceResult result = this.getAllByDefinitionId(pipelineDefinitionId);
        List<Pipeline> pipelines = (List<Pipeline>) result.getObject();
        List<Pipeline> filteredPipelines = pipelines
                .stream()
                .sorted((p1, p2) -> p2.getStartTime().compareTo(p1.getStartTime()))
                .collect(Collectors.toList());

        int indexOfPipeline = this.getIndexOfPipeline(filteredPipelines, pipelineId);
        if (indexOfPipeline == -1) {
            filteredPipelines = filteredPipelines
                    .stream()
                    .limit(numberOfPipelines)
                    .collect(Collectors.toList());
        } else {
            filteredPipelines = filteredPipelines
                    .stream()
                    .skip(indexOfPipeline + 1)
                    .limit(numberOfPipelines)
                    .collect(Collectors.toList());
        }

        List<PipelineDto> pipelineDtos = new ArrayList<>();
        for (Pipeline pipeline : filteredPipelines) {
            PipelineDto pipelineDto = new PipelineDto();
            pipelineDto.constructArtifactPipelineDto(pipeline);
            pipelineDtos.add(pipelineDto);
        }

        result.setObject(pipelineDtos);

        return result;
    }

    @Override
    public ServiceResult getPipelineArtifactDTOs(String searchCriteria, Integer numberOfPipelines) {
        return this.getPipelineArtifactDTOs(searchCriteria, numberOfPipelines, null);
    }

    @Override
    public ServiceResult getPipelineArtifactDTOs(String searchCriteria, Integer numberOfPipelines, String pipelineId) {
        ServiceResult result = this.getAll();
        List<Pipeline> pipelines = (List<Pipeline>) result.getObject();
        List<Pipeline> filteredPipelines = pipelines
                .stream()
                .filter(p -> p.getPipelineDefinitionName().toLowerCase().contains(searchCriteria.toLowerCase()))
                .sorted((p1, p2) -> p2.getStartTime().compareTo(p1.getStartTime()))
                .collect(Collectors.toList());

        int indexOfPipeline = this.getIndexOfPipeline(filteredPipelines, pipelineId);
        if (indexOfPipeline == -1) {
            filteredPipelines = filteredPipelines
                    .stream()
                    .limit(numberOfPipelines)
                    .collect(Collectors.toList());
        } else {
            filteredPipelines = filteredPipelines
                    .stream()
                    .skip(indexOfPipeline + 1)
                    .limit(numberOfPipelines)
                    .collect(Collectors.toList());
        }

        List<PipelineDto> pipelineDtos = new ArrayList<>();
        for (Pipeline pipeline : filteredPipelines) {
            PipelineDto pipelineDto = new PipelineDto();
            pipelineDto.constructArtifactPipelineDto(pipeline);
            pipelineDtos.add(pipelineDto);
        }

        result.setObject(pipelineDtos);

        return result;
    }

    @Override
    public ServiceResult cancelPipeline(String pipelineId) {
        ServiceResult result = this.getById(pipelineId);
        if (result.getNotificationType() == NotificationType.ERROR) {
            return result;
        }

        Pipeline pipeline = (Pipeline) result.getObject();
        pipeline.setShouldBeCanceled(true);
        pipeline.setStatus(PipelineStatus.IN_PROGRESS);
        return this.update(pipeline);
    }

    @Override
    public ServiceResult pausePipeline(String pipelineId) {
        ServiceResult result = this.getById(pipelineId);
        if (result.getNotificationType() == NotificationType.ERROR) {
            return result;
        }

        Pipeline pipeline = (Pipeline) result.getObject();
        if (pipeline.getStatus() == PipelineStatus.IN_PROGRESS) {
            pipeline.setStatus(PipelineStatus.PAUSED);
            result.setNotificationType(NotificationType.WARNING);
            String message = String.format("Pipeline %s set to PAUSED.", pipeline.getPipelineDefinitionName());
            result.setMessage(message);
            LOGGER.info(message);
            List<Stage> stages = pipeline.getStages();
            for (Stage stage : stages) {
                if (stage.getStatus() == StageStatus.IN_PROGRESS) {
                    stage.setStatus(StageStatus.PAUSED);
                }
            }
        } else {
            pipeline.setStatus(PipelineStatus.IN_PROGRESS);
            String message = String.format("Pipeline %s set to IN_PROGRESS.", pipeline.getPipelineDefinitionName());
            LOGGER.info(message);
            List<Stage> stages = pipeline.getStages();
            for (Stage stage : stages) {
                if (stage.getStatus() == StageStatus.PAUSED) {
                    stage.setStatus(StageStatus.IN_PROGRESS);
                    stage.setTriggeredManually(false);
                }
            }
        }

        return this.update(pipeline);
    }

    private void addMaterialsToPipeline(Pipeline pipeline) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        List<MaterialDefinition> materialDefinitions =
                (List<MaterialDefinition>) this.materialDefinitionService.getAllFromPipelineDefinition(pipelineDefinition.getId()).getObject();

        List<Material> materials = new ArrayList<>();
        for (MaterialDefinition materialDefinition : materialDefinitions) {
            Material material = new Material();
            material.setPipelineDefinitionId(pipeline.getPipelineDefinitionId());
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
                task.setTaskDefinition(fetchMaterialTask);
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

    private int getIndexOfPipeline(List<Pipeline> pipelines, String pipelineId) {
        int indexOfPipeline = -1;

        if (pipelineId != null) {
            int collectionSize = pipelines.size();
            for (int i = 0; i < collectionSize; i++) {
                if (pipelines.get(i).getId().equals(pipelineId)) {
                    indexOfPipeline = i;
                    break;
                }
            }
        }

        return indexOfPipeline;
    }
}
