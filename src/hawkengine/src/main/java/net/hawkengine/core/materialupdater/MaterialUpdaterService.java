package net.hawkengine.core.materialupdater;

import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import org.apache.log4j.Logger;

public class MaterialUpdaterService implements IMaterialUpdaterService{
    private static final Logger LOGGER = Logger.getLogger(MaterialUpdaterService.class.getName());


    @Override
    public boolean pollMaterialsForChanges(PipelineDefinition pipelineDefinition) {




        return true;
    }

    @Override
    public Pipeline updatePipelineMaterials(Pipeline pipeline) {


        return pipeline;
    }
}
