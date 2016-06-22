package net.hawkengine.services.interfaces;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.Service;

public interface IStageDefinitionService extends ICrudService<StageDefinition>{

    @Override
    ServiceResult getById(String id);

    ServiceResult getByIdInPipeline(String stageDefinitionId, String pipelineDefinitionId);

    @Override
    ServiceResult getAll();

    ServiceResult getAllInPipeline(String pipelineDefinitionId);

    @Override
    ServiceResult add(StageDefinition stageDefinition);

    @Override
    ServiceResult update(StageDefinition stageDefinition);

    @Override
    ServiceResult delete(String stageDefinitionId);
}
