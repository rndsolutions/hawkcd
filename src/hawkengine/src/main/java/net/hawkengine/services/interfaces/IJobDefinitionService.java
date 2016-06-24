package net.hawkengine.services.interfaces;

import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.ServiceResult;

public interface IJobDefinitionService {
    ServiceResult getById(String jobDefinitionId);

    ServiceResult getAllInStage(String stageDefinitionId);

    ServiceResult getAllInPipeline(String pipelineDefinitionId);

    ServiceResult getAll();

    ServiceResult add(JobDefinition jobDefinition);

    ServiceResult update(JobDefinition jobDefinition);

    ServiceResult delete(String jobDefinitionId);
}