package net.hawkengine.core.materialupdater;

import net.hawkengine.model.Material;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.services.MaterialService;
import net.hawkengine.services.interfaces.IMaterialService;

import java.util.List;

public class MaterialTrackerService implements IMaterialTrackerService {
    private IMaterialService materialService;
    private IMaterialUpdater materialUpdater;

    public MaterialTrackerService() {
        this.materialService = new MaterialService();
    }

    public MaterialTrackerService(IMaterialService materialService, IMaterialUpdater materialUpdater) {
        this.materialService = materialService;
        this.materialUpdater = materialUpdater;
    }

    @Override
    public String checkPipelineForTriggerMaterials(PipelineDefinition pipelineDefinition) {
        StringBuilder triggerMaterials = new StringBuilder();
        List<MaterialDefinition> materialDefinitions = pipelineDefinition.getMaterials();
        for (MaterialDefinition materialDefinition : materialDefinitions) {
            if (materialDefinition.isPollingForChanges()) {
                this.materialUpdater = MaterialUpdaterFactory.create(materialDefinition.getType());
                MaterialDefinition latestVersion = this.materialUpdater.getLatestMaterialVersion(materialDefinition);
                if (latestVersion == null) {
                    continue;
                }

                Material dbLatestVersion = (Material) this.materialService.getLatestMaterial(materialDefinition.getId()).getObject();
                boolean areTheSame = this.materialUpdater.areMaterialsSameVersion(latestVersion, dbLatestVersion.getMaterialDefinition());
                if (!areTheSame) {
                    triggerMaterials.append(materialDefinition.getName()).append(" ");
                }
            }
        }

        return triggerMaterials.toString();
    }
}
