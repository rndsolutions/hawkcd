package net.hawkengine.core.materialhandler;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.materialhandler.materialupdaters.IMaterialUpdater;
import net.hawkengine.core.materialhandler.materialupdaters.MaterialUpdaterFactory;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.MaterialType;
import net.hawkengine.services.MaterialDefinitionService;
import net.hawkengine.services.MaterialService;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.interfaces.IMaterialDefinitionService;
import net.hawkengine.services.interfaces.IMaterialService;
import net.hawkengine.ws.EndpointConnector;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                    ServerConfiguration.getConfiguration().getMaterialsDestination() + File.separator + pipelineName + File.separator + materialDefinition.getName());
        }
    }
}
