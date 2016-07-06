package net.hawkengine.core.materialupdater;

import net.hawkengine.core.utilities.EndpointConnector;
import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.apache.log4j.Logger;

import java.util.List;

public class MaterialPreparer implements Runnable {
    private IPipelineService pipelineService;
    private IMaterialPreparerService materialPreparerService;
    private static final Logger LOGGER = Logger.getLogger(MaterialPreparer.class);

    public MaterialPreparer() {
        this.pipelineService = new PipelineService();
        this.materialPreparerService = new MaterialPreparerService();
    }

    @Override
    public void run() {
        LOGGER.info(String.format(LoggerMessages.WORKER_STARTED, this.getClass().getSimpleName()));
        try {
            while (true) {
                List<Pipeline> pipelines = (List<Pipeline>) this.pipelineService.getAllNonupdatedPipelines().getObject();
                for (Pipeline pipeline : pipelines) {
                    Pipeline updatedPipeline = this.materialPreparerService.updatePipelineMaterials(pipeline);
                    if (updatedPipeline.areMaterialsUpdated()) {
                        ServiceResult result = this.pipelineService.update(updatedPipeline);
                        EndpointConnector.passResultToEndpoint(this.getClass().getSimpleName(), "update", result);
                    } else {
                        ServiceResult result = this.pipelineService.delete(updatedPipeline.getId());
                        EndpointConnector.passResultToEndpoint(this.getClass().getSimpleName(), "delete", result);
                    }
                }

                Thread.sleep(4 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
