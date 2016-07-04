package net.hawkengine.core.materialupdater;

import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.apache.log4j.Logger;

import java.util.List;

public class MaterialUpdater implements Runnable{
    private IPipelineDefinitionService pipelineDefinitionService;
    private IPipelineService pipelineService;
    private IMaterialUpdaterService materialUpdaterService;
    private static final Logger LOGGER = Logger.getLogger(MaterialUpdater.class);

    public MaterialUpdater() {
        this.pipelineService = new PipelineService();
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.materialUpdaterService = new MaterialUpdaterService();
    }

    @Override
    public void run() {
        LOGGER.info(String.format(LoggerMessages.WORKER_STARTED, this.getClass().getSimpleName()));
        try {
            while (true) {
                List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
                for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
                    boolean result = this.materialUpdaterService.pollMaterialsForChanges(pipelineDefinition);
                    if (result) {
                        Pipeline newPipeline = new Pipeline();
                        newPipeline.setPipelineDefinitionId(pipelineDefinition.getId());
                    }
                }


                Thread.sleep(4 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
