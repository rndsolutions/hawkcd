package net.hawkengine.core.materialupdater;

import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;

public interface IMaterialHandlerService {
    String checkPipelineForTriggerMaterials(PipelineDefinition pipelineDefinition);

    Pipeline updatePipelineMaterials(Pipeline pipeline);
}
