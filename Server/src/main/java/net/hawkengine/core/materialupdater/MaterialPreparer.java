package net.hawkengine.core.materialupdater;

import net.hawkengine.core.utilities.EndpointConnector;
import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.Material;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.MaterialService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IMaterialService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.apache.log4j.Logger;

import java.util.List;

public class MaterialPreparer implements Runnable {
    private IPipelineService pipelineService;
    private IMaterialService materialService;
    private IMaterialHandlerService materialHandlerService;
    private static final Logger LOGGER = Logger.getLogger(MaterialPreparer.class);

    public MaterialPreparer() {
        this.pipelineService = new PipelineService();
        this.materialService = new MaterialService();
        this.materialHandlerService = new MaterialHandlerService();
    }

    @Override
    public void run() {
        LOGGER.info(String.format(LoggerMessages.WORKER_STARTED, this.getClass().getSimpleName()));
        try {
            while (true) {
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
