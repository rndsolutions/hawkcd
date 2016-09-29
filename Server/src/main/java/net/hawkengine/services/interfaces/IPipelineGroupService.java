package net.hawkengine.services.interfaces;

import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.dto.PipelineGroupDto;

import java.util.List;

public interface IPipelineGroupService extends ICrudService<PipelineGroup> {
    ServiceResult getAllPipelineGroupDTOs();

    List<PipelineGroup> placePipelinesIntoGroups(List<PipelineGroup> pipelineGroups, List<PipelineDefinition> pipelineDefinitions);

    List<PipelineGroupDto> convertGroupsToDtos(List<PipelineGroup> pipelineGroups);
}
