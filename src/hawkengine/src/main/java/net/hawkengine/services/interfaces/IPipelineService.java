package net.hawkengine.services.interfaces;

import net.hawkengine.model.Pipeline;
import net.hawkengine.model.ServiceResult;

public interface IPipelineService extends ICrudService<Pipeline> {
    @Override
    ServiceResult getById(String pipelineId);

    @Override
    ServiceResult getAll();

    @Override
    ServiceResult add(Pipeline pipeline);

    @Override
    ServiceResult update(Pipeline pipeline);

    @Override
    ServiceResult delete(String pipelineId);

    ServiceResult getAllUpdatedPipelines();

    ServiceResult getAllPreparedPipelines();
}
