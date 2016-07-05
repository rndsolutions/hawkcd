package net.hawkengine.core.materialupdater;

import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;

import java.util.List;

public interface IMaterialTrackerService {
    String checkPipelineForTriggerMaterials(PipelineDefinition pipelineDefinition);

    Pipeline updatePipelineMaterials(Pipeline pipeline);
}
