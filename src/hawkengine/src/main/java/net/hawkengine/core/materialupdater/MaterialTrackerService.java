package net.hawkengine.core.materialupdater;

import net.hawkengine.model.Material;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;

public class MaterialTrackerService implements IMaterialTrackerService {
    private static final Logger LOGGER = Logger.getLogger(MaterialTrackerService.class.getName());


    @Override
    public String checkPipelineForTriggerMaterials(PipelineDefinition pipelineDefinition) {
        StrBuilder triggerMaterials = new StrBuilder();



        return triggerMaterials.toString();
    }

    @Override
    public Pipeline updatePipelineMaterials(Pipeline pipeline) {
        for (Material material : pipeline.getMaterials()) {

        }

        return pipeline;
    }
}
