package net.hawkengine.core.materialupdater;

import net.hawkengine.model.PipelineDefinition;
import org.apache.commons.lang.text.StrBuilder;

public class MaterialTrackerService implements IMaterialTrackerService {

    @Override
    public String checkPipelineForTriggerMaterials(PipelineDefinition pipelineDefinition) {
        StrBuilder triggerMaterials = new StrBuilder();


        return triggerMaterials.toString();
    }
}
