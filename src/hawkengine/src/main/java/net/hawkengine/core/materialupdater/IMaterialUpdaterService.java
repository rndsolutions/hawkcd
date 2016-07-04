package net.hawkengine.core.materialupdater;

import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;

public interface IMaterialUpdaterService {
    public boolean pollMaterialsForChanges(PipelineDefinition pipelineDefinition);

    public Pipeline updatePipelineMaterials(Pipeline pipeline);
}
