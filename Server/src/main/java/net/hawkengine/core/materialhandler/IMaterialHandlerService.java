package net.hawkengine.core.materialhandler;

import net.hawkengine.model.Material;
import net.hawkengine.model.PipelineDefinition;

public interface IMaterialHandlerService {
    String checkPipelineForTriggerMaterials(PipelineDefinition pipelineDefinition);

    Material updateMaterial(Material material, String pipelineName);
}
