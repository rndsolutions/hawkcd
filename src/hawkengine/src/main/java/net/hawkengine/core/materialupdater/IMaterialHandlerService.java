package net.hawkengine.core.materialupdater;

import net.hawkengine.model.Material;
import net.hawkengine.model.PipelineDefinition;

public interface IMaterialHandlerService {
    String checkPipelineForTriggerMaterials(PipelineDefinition pipelineDefinition);

    Material updateMaterial(Material material);
}
