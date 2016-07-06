package net.hawkengine.services.interfaces;

import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.ServiceResult;

public interface IJobDefinitionService extends ICrudService<JobDefinition> {
    ServiceResult getAllInStage(String stageDefinitionId);

    ServiceResult getAllInPipeline(String pipelineDefinitionId);
}