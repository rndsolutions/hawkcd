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

    ServiceResult getAllPipelineHistoryDTOs(String pipelineDefinitionId, Integer numberOfPipelines);

    ServiceResult getAllPipelineHistoryDTOs(String pipelineDefinitionId, Integer numberOfPipelines, String pipelineId);

    ServiceResult getPipelineArtifactDTOs(String searchCriteria, Integer numberOfPipelines);

    ServiceResult getPipelineArtifactDTOs(String searchCriteria, Integer numberOfPipelines, String pipelineId);

    ServiceResult cancelPipeline(String pipelineId);

    ServiceResult pausePipeline(String pipelineId);
}
