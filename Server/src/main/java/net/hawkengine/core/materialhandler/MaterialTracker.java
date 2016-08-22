package net.hawkengine.core.materialhandler;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.Material;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.MaterialService;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IMaterialService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import net.hawkengine.ws.EndpointConnector;
import org.apache.log4j.Logger;

import java.util.List;

public class MaterialTracker implements Runnable {
    private IPipelineDefinitionService pipelineDefinitionService;
    private IPipelineService pipelineService;
    private IMaterialService materialService;
    private IMaterialHandlerService materialHandlerService;
    private static final Logger LOGGER = Logger.getLogger(MaterialTracker.class);
    private String name;

    public MaterialTracker() {
        this.pipelineService = new PipelineService();
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.materialService = new MaterialService();
        this.materialHandlerService = new MaterialHandlerService();
        this.name = "MaterialTracker";
    }

    @Override
    public void run() {
        LOGGER.info(String.format(LoggerMessages.WORKER_STARTED, this.getClass().getSimpleName()));
        try {
            while (true) {
                // MaterialTracker
                List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAllAutomaticallyScheduledPipelines().getObject();
                for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
                    String triggerMaterials = this.materialHandlerService.checkPipelineForTriggerMaterials(pipelineDefinition);
                    if (!triggerMaterials.isEmpty()) {
                        Pipeline pipeline = new Pipeline();
                        pipeline.setPipelineDefinitionId(pipelineDefinition.getId());
                        pipeline.setTriggerReason(triggerMaterials);
                        ServiceResult result = this.pipelineService.add(pipeline);
                        EndpointConnector.passResultToEndpoint(PipelineService.class.getSimpleName(), "add", result);
                    }
                }

                // MaterialPreparer
                List<Pipeline> pipelines = (List<Pipeline>) this.pipelineService.getAllNonupdatedPipelines().getObject();
                for (Pipeline pipeline : pipelines) {
                    boolean isPipelineUpdated = true;
                    for (Material material : pipeline.getMaterials()) {
                        this.materialHandlerService.updateMaterial(material, pipeline);
                        if (material == null) {
                            isPipelineUpdated = false;
                            ServiceResult result = this.pipelineService.delete(pipeline.getId());
                            EndpointConnector.passResultToEndpoint(PipelineService.class.getSimpleName(), "delete", result);
                        } else if (material.isUpdated()) {
                            this.materialService.add(material);
                        }
                    }

                    if (isPipelineUpdated) {
                        pipeline.setMaterialsUpdated(true);
                        ServiceResult result = this.pipelineService.update(pipeline);
                        EndpointConnector.passResultToEndpoint(PipelineService.class.getSimpleName(), "update", result);
                    }
                }

                Thread.sleep(ServerConfiguration.getConfiguration().getMaterialTrackerPollInterval() * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
