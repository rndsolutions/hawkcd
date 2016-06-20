package net.hawkengine.services;

import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IStageDefinitionService;

import java.util.List;

public class StageDefinitionService implements IStageDefinitionService {
    private IPipelineDefinitionService pipelineDefinitionService;

    public StageDefinitionService() {
        this.pipelineDefinitionService = new PipelineDefinitionService();
    }

    public StageDefinitionService(IPipelineDefinitionService pipelineDefinitionService) {
        this.pipelineDefinitionService = pipelineDefinitionService;
    }

    @Override
    public ServiceResult getById(String stageDefinitionId) {
        return null;
    }

    @Override
    public ServiceResult getAll() {
        return null;
    }

    @Override
    public ServiceResult add(StageDefinition stageDefinition) {
        String pipelineDefinitionId = stageDefinition.getPipelineDefinitionId();
        PipelineDefinition pipelineFromDatabase = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        List<StageDefinition> stageDefinitions = pipelineFromDatabase.getStageDefinitions();
        stageDefinitions.add(stageDefinition);
        pipelineFromDatabase.setStageDefinitions(stageDefinitions);

        ServiceResult serviceResult = this.pipelineDefinitionService.update(pipelineFromDatabase);

        ServiceResult result = new ServiceResult();
        if (serviceResult.hasError()){
            result.setError(true);
            result.setMessage(stageDefinition.getClass() + " " + stageDefinition.getId() + " already exists.");
        } else {
            result.setError(false);
            result.setMessage(stageDefinition.getClass() + " " + stageDefinition.getId() + " created successfully.");
        }
        return result;
    }

    @Override
    public ServiceResult update(StageDefinition stageDefinition) {
        return null;
    }

    @Override
    public ServiceResult delete(String stageDefinitionId) {
        return null;
    }
}
