package net.hawkengine.core.materialupdater;

import net.hawkengine.core.utilities.EndpointConnector;
import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.apache.log4j.Logger;

import java.util.List;

public class MaterialTracker implements Runnable{
    private IPipelineDefinitionService pipelineDefinitionService;
    private IPipelineService pipelineService;
    private IMaterialTrackerService materialUpdaterService;
    private static final Logger LOGGER = Logger.getLogger(MaterialTracker.class);

    public MaterialTracker() {
        this.pipelineService = new PipelineService();
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.materialUpdaterService = new MaterialTrackerService();
    }

    @Override
    public void run() {
        LOGGER.info(String.format(LoggerMessages.WORKER_STARTED, this.getClass().getSimpleName()));
        try {
            while (true) {
                List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
                for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
                    String triggerMaterials = this.materialUpdaterService.checkPipelineForTriggerMaterials(pipelineDefinition);
                    if (!triggerMaterials.isEmpty()) {
                        Pipeline pipeline = new Pipeline();
                        pipeline.setPipelineDefinitionId(pipelineDefinition.getId());
                        pipeline.setTriggerReason(triggerMaterials);
                        ServiceResult result = this.pipelineService.add(pipeline);
                        EndpointConnector.passResultToEndpoint(this.getClass().getSimpleName(), "add", result);
                    }
                }

                Thread.sleep(4 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
