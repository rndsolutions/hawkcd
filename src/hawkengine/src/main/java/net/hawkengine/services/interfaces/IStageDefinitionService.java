package net.hawkengine.services.interfaces;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;

public interface IStageDefinitionService extends ICrudService<StageDefinition> {
    @Override
    ServiceResult getById(String stageDefinitionId);

    @Override
    ServiceResult getAll();

    @Override
    ServiceResult add(StageDefinition stageDefinition);

    @Override
    ServiceResult update(StageDefinition stageDefinition);

    @Override
    ServiceResult delete(String stageDefinitionId);
}
