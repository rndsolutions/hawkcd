package net.hawkengine.services;

import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IStageDefinitionService;

import java.util.List;

public class StageDefinitionService extends Service<StageDefinition> implements IStageDefinitionService {
    private IPipelineDefinitionService pipelineDefinitionService;

    public StageDefinitionService() {
        this.pipelineDefinitionService = new PipelineDefinitionService();
        super.setObjectType("StageDefinition");
    }

    public StageDefinitionService(IPipelineDefinitionService pipelineDefinitionService) {
        this.pipelineDefinitionService = pipelineDefinitionService;
    }

    @Override
    public ServiceResult getById(String stageDefinitionId, String pipelineDefinitionId) {
        ServiceResult serviceResult;
        PipelineDefinition pipelineFromDatabase = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();

        StageDefinition stage = pipelineFromDatabase
                .getStageDefinitions()
                .stream()
                .filter(st -> st.getId().equals(stageDefinitionId))
                .findFirst()
                .orElse(null);

        if (stage != null) {
            serviceResult = super.createServiceResult(stage, false, "retrieved successfully");
        } else {
            serviceResult = super.createServiceResult(stage, true, "not found");
        }

        return serviceResult;
    }

    @Override
    public ServiceResult getAll(String pipelineDefinitionId) {
        PipelineDefinition pipelineFromDatabase = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        List<StageDefinition> stageDefinitions = pipelineFromDatabase.getStageDefinitions();

        ServiceResult result = super.createServiceResultArray(stageDefinitions, false, "retrieved successfully");

        return result;
    }

    @Override
    public ServiceResult add(StageDefinition stageDefinition) {
        String pipelineDefinitionId = stageDefinition.getPipelineDefinitionId();
        PipelineDefinition pipeline = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        List<StageDefinition> stageDefinitions = pipeline.getStageDefinitions();
        stageDefinitions.add(stageDefinition);
        pipeline.setStageDefinitions(stageDefinitions);

        ServiceResult serviceResult = this.pipelineDefinitionService.update(pipeline);

        ServiceResult result = new ServiceResult();
        if (!serviceResult.hasError()) {
            result = super.createServiceResult(stageDefinition, false, "created successfully");
        } else {
            result = super.createServiceResult((StageDefinition) result.getObject(), true, "already exists");
        }
        return result;
    }

    @Override
    public ServiceResult update(StageDefinition stageDefinition) {
        ServiceResult serviceResult;
        String pipelineDefinitionId = stageDefinition.getPipelineDefinitionId();
        PipelineDefinition pipeline = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        List<StageDefinition> stageDefinitions = pipeline.getStageDefinitions();

        int stageDefinitionLength = stageDefinitions.size();
        for (int i = 0; i < stageDefinitionLength; i++) {
            if (stageDefinitions.get(i).getId().equals(stageDefinition.getId())) {
                stageDefinitions.set(i, stageDefinition);
            }
        }

        pipeline.setStageDefinitions(stageDefinitions);
        ServiceResult pipelineServiceResult = this.pipelineDefinitionService.update(pipeline);
        if (!pipelineServiceResult.hasError()) {
            serviceResult = super.createServiceResult(stageDefinition, false, "updated successfully");
        } else {
            serviceResult = super.createServiceResult(stageDefinition, true, "not found");
        }
        return serviceResult;
    }

    @Override
    public ServiceResult delete(String stageDefinitionId, String pipelineDefinitionId) {
        PipelineDefinition pipeline = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        ServiceResult serviceResult;
        List<StageDefinition> stageDefinitions = pipeline.getStageDefinitions();
        StageDefinition stageDefinition = stageDefinitions
                .stream()
                .filter(st -> st.getId().equals(stageDefinitionId))
                .findFirst()
                .orElse(null);
        boolean isRemoved = stageDefinitions.remove(stageDefinition);

        if (isRemoved) {
            pipeline.setStageDefinitions(stageDefinitions);
            this.pipelineDefinitionService.update(pipeline);
            serviceResult = super.createServiceResult(stageDefinition, false, "deleted successfully");
        } else {
            serviceResult = super.createServiceResult(stageDefinition, true, "not found");
        }

        return serviceResult;
    }
}