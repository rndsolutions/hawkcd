package net.hawkengine.services.interfaces;

import net.hawkengine.model.ServiceResult;

public interface IJobDefinitionService {
    ServiceResult getAllInStage(String stageDefinitionId);

    ServiceResult getAllInPipeline(String pipelineDefinitionId);
}