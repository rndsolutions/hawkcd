package net.hawkengine.services.interfaces;

import net.hawkengine.model.Pipeline;
import net.hawkengine.model.ServiceResult;

public interface IPipelineService extends ICrudService<Pipeline> {
    ServiceResult getAllByDefinitionId(String pipelineDefinitionId);

    ServiceResult getAllNonupdatedPipelines();

    ServiceResult getAllUpdatedUnpreparedPipelinesInProgress();

    ServiceResult getAllPreparedPipelinesInProgress();

    ServiceResult getAllPreparedAwaitingPipelines();

    ServiceResult getLastRun(String pipelineDefinitionId);

    ServiceResult getAllPipelineHistoryDTOs(String pipelineDefinitionId);

    ServiceResult getAllPipelineArtifactDTOs();

    ServiceResult cancelPipeline(String pipelineId);
}
