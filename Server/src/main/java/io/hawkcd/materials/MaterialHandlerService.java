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

package io.hawkcd.materials;

import io.hawkcd.Config;
import io.hawkcd.materials.materialupdaters.IMaterialUpdater;
import io.hawkcd.materials.materialupdaters.MaterialUpdaterFactory;
import io.hawkcd.model.Material;
import io.hawkcd.model.Pipeline;
import io.hawkcd.model.PipelineDefinition;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.enums.MaterialType;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.services.MaterialDefinitionService;
import io.hawkcd.services.MaterialService;
import io.hawkcd.services.PipelineDefinitionService;
import io.hawkcd.services.interfaces.IMaterialDefinitionService;
import io.hawkcd.services.interfaces.IMaterialService;
import io.hawkcd.ws.EndpointConnector;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.hawkcd.model.GitMaterial;

public class MaterialHandlerService implements IMaterialHandlerService {
    private IMaterialDefinitionService materialDefinitionService;
    private IMaterialService materialService;
    private IMaterialUpdater materialUpdater;

    public MaterialHandlerService() {
        this.materialDefinitionService = new MaterialDefinitionService();
        this.materialService = new MaterialService();
    }

    public MaterialHandlerService(IMaterialDefinitionService materialDefinitionService, IMaterialService materialService, IMaterialUpdater materialUpdater) {
        this.materialDefinitionService = materialDefinitionService;
        this.materialService = materialService;
        this.materialUpdater = materialUpdater;
    }

    @Override
    public String checkPipelineForTriggerMaterials(PipelineDefinition pipelineDefinition) {
        List<String> triggerMaterials = new ArrayList<>();
        List<MaterialDefinition> materialDefinitions =
                (List<MaterialDefinition>) this.materialDefinitionService.getAllFromPipelineDefinition(pipelineDefinition.getId()).getObject();
        for (MaterialDefinition materialDefinition : materialDefinitions) {
            if (materialDefinition.isPollingForChanges()) {
                this.materialUpdater = MaterialUpdaterFactory.create(materialDefinition.getType());
                String oldError = materialDefinition.getErrorMessage();
                setDestinationOfGitMaterial(materialDefinition, pipelineDefinition.getName());
                MaterialDefinition latestVersion = materialUpdater.getLatestMaterialVersion(materialDefinition);
                String newError = materialDefinition.getErrorMessage();
                if (!oldError.equals(newError)) {
                    ServiceResult result = this.materialDefinitionService.update(latestVersion);
                    EndpointConnector.passResultToEndpoint(MaterialDefinitionService.class.getSimpleName(), "update", result);
                }

                if (!latestVersion.getErrorMessage().isEmpty()) {
                    continue;
                }

                Material dbLatestVersion = (Material) this.materialService.getLatestMaterial(materialDefinition.getId(), pipelineDefinition.getId()).getObject();

                boolean areTheSame = false;
                if (dbLatestVersion != null) {
                    areTheSame = this.materialUpdater.areMaterialsSameVersion(latestVersion, dbLatestVersion.getMaterialDefinition());
                }

                if (!areTheSame) {
                    triggerMaterials.add(materialDefinition.getName());
                }
            }
        }

        return String.join(", ", triggerMaterials);
    }

    @Override
    public Material updateMaterial(Material material, Pipeline pipeline) {
        this.materialUpdater = MaterialUpdaterFactory.create(material.getMaterialDefinition().getType());
        String oldError = material.getMaterialDefinition().getErrorMessage();
        setDestinationOfGitMaterial(material.getMaterialDefinition(), pipeline.getPipelineDefinitionName());
        MaterialDefinition latestVersion = this.materialUpdater.getLatestMaterialVersion(material.getMaterialDefinition());
        String newError = material.getMaterialDefinition().getErrorMessage();
        if (!oldError.equals(newError)) {
            ServiceResult result = this.materialDefinitionService.update(latestVersion);
            EndpointConnector.passResultToEndpoint(PipelineDefinitionService.class.getSimpleName(), "update", result);
        }

        if (!material.getMaterialDefinition().getErrorMessage().isEmpty()) {
            return null;
        }

        Material dbLatestVersion = (Material) this.materialService.getLatestMaterial(material.getMaterialDefinition().getId(), pipeline.getPipelineDefinitionId()).getObject();

        boolean areTheSame = false;
        if (dbLatestVersion != null) {
            areTheSame = this.materialUpdater.areMaterialsSameVersion(latestVersion, dbLatestVersion.getMaterialDefinition());
        }

        if (!areTheSame) {
            material.setChangeDate(LocalDateTime.now());
            material.setMaterialDefinition(latestVersion);
            material.setUpdated(true);
        }

        return material;
    }

    private void setDestinationOfGitMaterial(MaterialDefinition materialDefinition, String pipelineName) {
        if (materialDefinition.getType() == MaterialType.GIT) {
            ((GitMaterial) materialDefinition).setDestination(
                    Config.getConfiguration().getMaterialsDestination() + File.separator + pipelineName + File.separator + materialDefinition.getName());
        }
    }
}
