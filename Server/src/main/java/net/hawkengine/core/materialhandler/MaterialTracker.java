package net.hawkengine.core.materialhandler;

import net.hawkengine.core.utilities.EndpointConnector;
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
import org.apache.log4j.Logger;

import java.util.List;

public class MaterialTracker implements Runnable {
    private IPipelineDefinitionService pipelineDefinitionService;
    private IPipelineService pipelineService;
    private IMaterialService materialService;
    private IMaterialHandlerService materialHandlerService;
    private static final Logger LOGGER = Logger.getLogger(MaterialTracker.class);

    public MaterialTracker() {
        this.pipelineService = new PipelineService();
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.materialService = new MaterialService();
        this.materialHandlerService = new MaterialHandlerService();
    }

    @Override
    public void run() {
        LOGGER.info(String.format(LoggerMessages.WORKER_STARTED, this.getClass().getSimpleName()));
        try {
            while (true) {
                // MaterialTracker
                List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
                for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
                    String triggerMaterials = this.materialHandlerService.checkPipelineForTriggerMaterials(pipelineDefinition);
                    if (!triggerMaterials.isEmpty()) {
                        Pipeline pipeline = new Pipeline();
                        pipeline.setPipelineDefinitionId(pipelineDefinition.getId());
                        pipeline.setTriggerReason(triggerMaterials);
                        ServiceResult result = this.pipelineService.add(pipeline);
                        EndpointConnector.passResultToEndpoint(this.getClass().getSimpleName(), "add", result);
                    }
                }

                // MaterialPreparer
                List<Pipeline> pipelines = (List<Pipeline>) this.pipelineService.getAllNonupdatedPipelines().getObject();
                for (Pipeline pipeline : pipelines) {
                    for (Material material : pipeline.getMaterials()) {
                        Material updatedMaterial = this.materialHandlerService.updateMaterial(material);
                        if (updatedMaterial == null) {
                            ServiceResult result = this.pipelineService.delete(pipeline.getId());
                            EndpointConnector.passResultToEndpoint(this.getClass().getSimpleName(), "delete", result);
                        }

                        if (updatedMaterial.isUpdated()) {
                            this.materialService.add(material);
                        }
                    }

                    pipeline.setMaterialsUpdated(true);
                    ServiceResult result = this.pipelineService.update(pipeline);
                    EndpointConnector.passResultToEndpoint(this.getClass().getSimpleName(), "update", result);
                }

                Thread.sleep(4 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
