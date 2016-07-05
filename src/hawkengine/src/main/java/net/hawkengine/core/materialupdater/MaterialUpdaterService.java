package net.hawkengine.core.materialupdater;

import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MaterialUpdaterService implements IMaterialUpdaterService{
    private static final Logger LOGGER = Logger.getLogger(MaterialUpdaterService.class.getName());


    @Override
    public String checkPipelineForTriggerMaterials(PipelineDefinition pipelineDefinition) {
        StrBuilder triggerMaterials = new StrBuilder();



        return triggerMaterials.toString();
    }

    @Override
    public Pipeline updatePipelineMaterials(Pipeline pipeline) {


        return pipeline;
    }
}
