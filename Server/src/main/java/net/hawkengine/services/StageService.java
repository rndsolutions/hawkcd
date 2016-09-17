package net.hawkengine.services;

import net.hawkengine.model.*;
import net.hawkengine.model.dto.StageDto;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.services.interfaces.IJobDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import net.hawkengine.services.interfaces.IStageService;

import java.util.ArrayList;
import java.util.List;

public class StageService extends CrudService<Stage> implements IStageService {
    private static final Class CLASS_TYPE = Stage.class;

    private IPipelineService pipelineService;
    private IJobDefinitionService jobDefinitionService;
    private String failureMessage = "not found";
    private String successMessage = "retrieved successfully";

    public StageService() {
        this.pipelineService = new PipelineService();
        this.jobDefinitionService = new JobDefinitionService();
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    //TODO: add tests for JobDefinitionService
    public StageService(IPipelineService pipelineService) {
        this.pipelineService = pipelineService;
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    @Override
    public ServiceResult getById(String stageId) {
        List<Pipeline> allPipelines = (List<Pipeline>) this.pipelineService.getAll().getObject();
        Stage result = null;
        for (Pipeline pipeline : allPipelines) {
            List<Stage> stages = pipeline.getStages();
            for (Stage stage : stages) {
                if (stage.getId().equals(stageId)) {
                    result = stage;

                    return super.createServiceResult(result, NotificationType.SUCCESS, this.successMessage);
                }
            }
        }
        return super.createServiceResult(result, NotificationType.ERROR, this.failureMessage);
    }

    @Override
    public ServiceResult getAll() {
        List<Pipeline> allPipelines = (List<Pipeline>) this.pipelineService.getAll().getObject();
        List<Stage> allStages = new ArrayList<>();

        for (Pipeline pipeline : allPipelines) {
            List<Stage> stages = pipeline.getStages();
            allStages.addAll(stages);
        }
        return super.createServiceResultArray(allStages, NotificationType.SUCCESS, this.successMessage);
    }

    @Override
    public ServiceResult add(Stage stage) {
        Pipeline pipeline = (Pipeline) this.pipelineService.getById(stage.getPipelineId()).getObject();
        List<Stage> stages = pipeline.getStages();

        for (Stage stageFromDb : stages) {
            if (stageFromDb.getId().equals(stage.getId())) {
                return super.createServiceResult(stage, NotificationType.ERROR, "already exist");
            }
        }

        stages.add(stage);
        pipeline.setStages(stages);
        ServiceResult serviceResult = this.pipelineService.update(pipeline);

        if (serviceResult.getNotificationType() == NotificationType.ERROR) {
            return super.createServiceResult(stage, NotificationType.ERROR, "not created");
        }
        Stage result = this.extractStageFromPipeline(pipeline, stage.getId());

        if (result == null) {
            return super.createServiceResult(result, NotificationType.ERROR, "not created");
        }

        return super.createServiceResult(result, NotificationType.SUCCESS, "created successfully");
    }

    @Override
    public ServiceResult update(Stage stage) {
        Pipeline pipeline = (Pipeline) this.pipelineService.getById(stage.getPipelineId()).getObject();
        List<Stage> stages = pipeline.getStages();
        int stageCollectionSize = stages.size();
        boolean isPresent = false;
        for (int i = 0; i < stageCollectionSize; i++) {
            if (stages.get(i).getId().equals(stage.getId())) {
                isPresent = true;
                stages.set(i, stage);
            }
        }

        if (!isPresent) {
            return super.createServiceResult(null, NotificationType.ERROR, "not found");
        }

        pipeline.setStages(stages);
        ServiceResult serviceResult = this.pipelineService.update(pipeline);

        if (serviceResult.getNotificationType() == NotificationType.ERROR) {
            serviceResult = super.createServiceResult((Stage) serviceResult.getObject(), NotificationType.ERROR, "not updated");
        } else {
            serviceResult = super.createServiceResult(stage, NotificationType.SUCCESS, "updated successfully");
        }
        return serviceResult;
    }

    @Override
    public ServiceResult delete(String stageId) {
        Pipeline pipelineToUpdate = new Pipeline();
        List<Pipeline> pipelines = (List<Pipeline>) this.pipelineService.getAll().getObject();

        for (Pipeline pipeline : pipelines) {
            List<Stage> stages = pipeline.getStages();

            for (Stage stage : stages) {
                if (stage.getId().equals(stageId)) {
                    pipelineToUpdate = pipeline;
                }
            }
        }

        boolean isRemoved = false;
        ServiceResult serviceResult = null;
        List<Stage> stages = pipelineToUpdate.getStages();
        Stage stage = stages
                .stream()
                .filter(st -> st.getId().equals(stageId))
                .findFirst()
                .orElse(null);

        if (stage == null) {
            serviceResult = super.createServiceResult(stage, NotificationType.ERROR, "not found");
        }

        if (stages.size() > 1) {
            isRemoved = stages.remove(stage);
        } else {
            return super.createServiceResult(stage, NotificationType.ERROR, "is the last Stage and cannot be deleted");
        }

        if (isRemoved) {
            pipelineToUpdate.setStages(stages);
            serviceResult = this.pipelineService.update(pipelineToUpdate);
            if (serviceResult.getNotificationType() == NotificationType.SUCCESS) {

                serviceResult = super.createServiceResult(stage, NotificationType.SUCCESS, "deleted successfully");
            }
        }
        return serviceResult;
    }

    private Stage extractStageFromPipeline(Pipeline pipline, String stageId) {
        Stage result = pipline.getStages().stream()
                .filter(stage -> stage.getId().equals(stageId))
                .findFirst()
                .orElse(null);

        return result;

    }

    @Override
    public ServiceResult addStageWithSpecificJobs(StageDto stageDto) {
        Stage stageToAdd = stageDto.getStage();
        List<Job> jobs = new ArrayList<>();
        ServiceResult result;

        for (String jobDefinitionId : stageDto.getJobDefinitionIds()) {
            result =  this.jobDefinitionService.getById(jobDefinitionId);

            if (result.getNotificationType() == NotificationType.ERROR){
                return result;
            }
            JobDefinition jobDefinition = (JobDefinition) result.getObject();
            Job jobToAdd = new Job();
            jobToAdd.setPipelineId(stageToAdd.getPipelineId());
            jobToAdd.setJobDefinitionId(jobDefinition.getId());
            jobToAdd.setJobDefinitionName(jobDefinition.getName());
            jobToAdd.setStageId(stageToAdd.getId());
            jobToAdd.setJobDefinitionName(jobDefinition.getName());
            jobs.add(jobToAdd);
        }
        stageToAdd.setJobs(jobs);
        stageToAdd.setExecutionId(stageToAdd.getExecutionId() + 1);

        Pipeline pipeline = (Pipeline) this.pipelineService.getById(stageToAdd.getPipelineId()).getObject();
        pipeline.setStageRun(true);

        result = this.pipelineService.update(pipeline);
        if (result.getNotificationType() == NotificationType.ERROR){
            return result;
        }

        return this.add(stageToAdd);
    }
}