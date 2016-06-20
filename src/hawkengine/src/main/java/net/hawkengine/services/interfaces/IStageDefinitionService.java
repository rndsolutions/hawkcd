package net.hawkengine.services.interfaces;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;

public interface IStageDefinitionService{

    ServiceResult getById(String stageDefinitionId, String pipelineDefinitionId);

    ServiceResult getAll(String pipelineDefinitionId);

    ServiceResult add(StageDefinition stageDefinition);

    ServiceResult update(StageDefinition stageDefinition);

    ServiceResult delete(String stageDefinitionId, String pipelineDefinitionId);
}
