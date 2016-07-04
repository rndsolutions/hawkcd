package net.hawkengine.services.interfaces;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.Service;

public interface IStageDefinitionService extends ICrudService<StageDefinition>{
    ServiceResult getByIdInPipeline(String stageDefinitionId, String pipelineDefinitionId);

    ServiceResult getAllInPipeline(String pipelineDefinitionId);
}
