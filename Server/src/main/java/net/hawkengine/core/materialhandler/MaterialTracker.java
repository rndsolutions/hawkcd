/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
                        String message = String.format("Pipeline %s triggered by %s", pipelineDefinition.getName(), triggerMaterials);
                        LOGGER.info(message);
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
                            String message = String.format("Pipeline %s material could not be updated", pipeline.getPipelineDefinitionName());
                            LOGGER.info(message);
                        } else if (material.isUpdated()) {
                            this.materialService.add(material);
                        }
                    }

                    if (isPipelineUpdated) {
                        pipeline.setMaterialsUpdated(true);
                        this.pipelineService.update(pipeline);
                        String message = String.format("Pipeline %s materials updated", pipeline.getPipelineDefinitionName());
                        LOGGER.info(message);
                    }
                }

                Thread.sleep(ServerConfiguration.getConfiguration().getMaterialTrackerPollInterval() * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
