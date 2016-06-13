package net.hawkengine.services.interfaces;

import net.hawkengine.model.Pipeline;
import net.hawkengine.model.ServiceResult;

public interface IPipelineService extends ICrudService<Pipeline> {
    ServiceResult getById(String pipelineId);

    ServiceResult getAll();

    ServiceResult add(Pipeline pipeline);

    ServiceResult update(Pipeline pipeline);

    ServiceResult delete(String pipelineId);
}
