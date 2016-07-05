package net.hawkengine.core.materialupdater;

import net.hawkengine.model.PipelineDefinition;

public interface IMaterialTrackerService {
    String checkPipelineForTriggerMaterials(PipelineDefinition pipelineDefinition);
}
