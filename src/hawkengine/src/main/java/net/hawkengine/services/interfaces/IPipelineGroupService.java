package net.hawkengine.services.interfaces;

import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.ServiceResult;

public interface IPipelineGroupService extends ICrudService<PipelineGroup> {
    ServiceResult getAllPipelineGroupDTOs();
}
