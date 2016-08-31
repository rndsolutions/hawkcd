package net.hawkengine.services;

import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IStageDefinitionService;

import java.util.ArrayList;
import java.util.List;

public class StageDefinitionService extends CrudService<StageDefinition> implements IStageDefinitionService {
    private static final Class CLASS_TYPE = StageDefinition.class;

    private IPipelineDefinitionService pipelineDefinitionService;

    public StageDefinitionService() {
        this.pipelineDefinitionService = new PipelineDefinitionService();
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    public StageDefinitionService(IPipelineDefinitionService pipelineDefinitionService) {
        this.pipelineDefinitionService = pipelineDefinitionService;
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    @Override
    public ServiceResult getById(String stageDefinitionId) {
        ServiceResult serviceResult = super.createServiceResult(null, NotificationType.ERROR, "not found");
        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();

        for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
            List<StageDefinition> stageDefinitions = pipelineDefinition.getStageDefinitions();

            for (StageDefinition stageDefinition : stageDefinitions) {
                if (stageDefinition.getId().equals(stageDefinitionId)) {
                    serviceResult = this.getByIdInPipeline(stageDefinitionId, pipelineDefinition.getId());
                }
            }
        }

        return serviceResult;
    }

    @Override
    public ServiceResult getByIdInPipeline(String stageDefinitionId, String pipelineDefinitionId) {
        ServiceResult serviceResult;
        PipelineDefinition pipelineFromDatabase = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();

        StageDefinition stage = pipelineFromDatabase
                .getStageDefinitions()
                .stream()
                .filter(st -> st.getId().equals(stageDefinitionId))
                .findFirst()
                .orElse(null);

        if (stage != null) {
            serviceResult = super.createServiceResult(stage, NotificationType.SUCCESS, "retrieved successfully");
        } else {
            serviceResult = super.createServiceResult(stage, NotificationType.ERROR, "not found");
        }

        return serviceResult;
    }

    @Override
    public ServiceResult getAll() {
        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
        List<StageDefinition> stageDefinitions = new ArrayList<>();

        for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
            stageDefinitions.addAll(pipelineDefinition.getStageDefinitions());
        }
        ServiceResult result = super.createServiceResultArray(stageDefinitions, NotificationType.SUCCESS, "retrieved successfully");

        return result;
    }

    @Override
    public ServiceResult getAllInPipeline(String pipelineDefinitionId) {
        PipelineDefinition pipelineFromDatabase = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        List<StageDefinition> stageDefinitions = pipelineFromDatabase.getStageDefinitions();

        ServiceResult result = super.createServiceResultArray(stageDefinitions, NotificationType.SUCCESS, "retrieved successfully");

        return result;
    }

    @Override
    public ServiceResult add(StageDefinition stageDefinition) {
        ServiceResult result = null;
        String pipelineDefinitionId = stageDefinition.getPipelineDefinitionId();
        PipelineDefinition pipeline = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        List<StageDefinition> stageDefinitions = pipeline.getStageDefinitions();

        for (StageDefinition stDefinition : stageDefinitions) {
            if (stDefinition.getId().equals(stageDefinition.getId())) {
                return super.createServiceResult(null, NotificationType.ERROR, "already exists");
            }
        }

        if ((this.isPresentWithSameName(stageDefinitions, stageDefinition))) {
            return super.createServiceResult(null, NotificationType.ERROR, "with that name already exists");
        }

        List<JobDefinition> stageJobDefinitions = stageDefinition.getJobDefinitions();
        for (JobDefinition jobDefinition : stageJobDefinitions) {
            jobDefinition.setStageDefinitionId(stageDefinition.getId());
        }
        stageDefinitions.add(stageDefinition);
        pipeline.setStageDefinitions(stageDefinitions);

        ServiceResult serviceResult = this.pipelineDefinitionService.update(pipeline);

        if (serviceResult.getNotificationType() == NotificationType.ERROR) {
            result = super.createServiceResult(null, NotificationType.ERROR, "not created");
        } else {
            result = super.createServiceResult(stageDefinition, NotificationType.SUCCESS, "created successfully");
        }

        return result;
    }

    @Override
    public ServiceResult update(StageDefinition stageDefinition) {
        ServiceResult serviceResult = null;
        String pipelineDefinitionId = stageDefinition.getPipelineDefinitionId();
        PipelineDefinition pipeline = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        List<StageDefinition> stageDefinitions = pipeline.getStageDefinitions();

        int stageDefinitionLength = stageDefinitions.size();
        boolean isPresent = false;
        for (int i = 0; i < stageDefinitionLength; i++) {
            if (stageDefinitions.get(i).getId().equals(stageDefinition.getId())) {
                isPresent = true;
                if (this.isPresentWithSameName(stageDefinitions, stageDefinition)) {
                    return super.createServiceResult(null, NotificationType.ERROR, "with that name already exists");
                } else {
                    stageDefinitions.set(i, stageDefinition);
                }
            }
        }

        if (!isPresent) {
            return super.createServiceResult(null, NotificationType.ERROR, "not found");
        }

        pipeline.setStageDefinitions(stageDefinitions);
        ServiceResult pipelineServiceResult = this.pipelineDefinitionService.update(pipeline);

        if (pipelineServiceResult.getNotificationType() == NotificationType.ERROR) {
            serviceResult = super.createServiceResult((StageDefinition) serviceResult.getObject(), NotificationType.ERROR, "not updated");
        } else {
            serviceResult = super.createServiceResult(stageDefinition, NotificationType.SUCCESS, "updated successfully");
        }

        return serviceResult;
    }

    @Override
    public ServiceResult delete(String stageDefinitionId) {

        PipelineDefinition pipeline = new PipelineDefinition();
        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();

        for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
            List<StageDefinition> stageDefinitions = pipelineDefinition.getStageDefinitions();

            for (StageDefinition stageDefinition : stageDefinitions) {
                if (stageDefinition.getId().equals(stageDefinitionId)) {
                    pipeline = pipelineDefinition;
                }
            }
        }

        boolean isRemoved = false;
        ServiceResult serviceResult = null;
        List<StageDefinition> stageDefinitions = pipeline.getStageDefinitions();
        StageDefinition stageDefinition = stageDefinitions
                .stream()
                .filter(st -> st.getId().equals(stageDefinitionId))
                .findFirst()
                .orElse(null);

        if (stageDefinition == null) {
            serviceResult = super.createServiceResult(stageDefinition, NotificationType.ERROR, "not found");
        }

        if (stageDefinitions.size() > 1) {
            isRemoved = stageDefinitions.remove(stageDefinition);
        } else {
             super.createServiceResult(stageDefinition, NotificationType.ERROR, "is the last Stage Definition and cannot be deleted");
        }

        if (isRemoved) {
            pipeline.setStageDefinitions(stageDefinitions);
            this.pipelineDefinitionService.update(pipeline);
            serviceResult = super.createServiceResult(stageDefinition, NotificationType.SUCCESS, "deleted successfully");
        }

        return serviceResult;
    }

    private boolean isPresentWithSameName(List<StageDefinition> stageDefinitions, StageDefinition stageDefinition) {
        boolean isPresent = false;

        for (StageDefinition stDefinition : stageDefinitions) {
            if (stDefinition.getName() != null) {
                if (stDefinition.getName().equals(stageDefinition.getName()) && !stageDefinition.getId().equals(stDefinition.getId())) {
                    isPresent = true;
                    return isPresent;
                }
            }
        }

        return isPresent;
    }
}