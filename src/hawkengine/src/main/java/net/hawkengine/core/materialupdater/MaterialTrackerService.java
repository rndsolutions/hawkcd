package net.hawkengine.core.materialupdater;

import net.hawkengine.model.Material;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.services.MaterialService;
import net.hawkengine.services.interfaces.IMaterialService;

import java.util.List;

public class MaterialTrackerService implements IMaterialTrackerService {
    private IMaterialService materialService;

    public MaterialTrackerService() {
        this.materialService = new MaterialService();
    }

    @Override
    public String checkPipelineForTriggerMaterials(PipelineDefinition pipelineDefinition) {
        StringBuilder triggerMaterials = new StringBuilder();
        List<MaterialDefinition> materialDefinitions = pipelineDefinition.getMaterials();
        for (MaterialDefinition materialDefinition : materialDefinitions) {
            if (materialDefinition.isPollingForChanges()) {
                MaterialUpdater materialUpdater = MaterialUpdaterFactory.create(materialDefinition.getType());
                MaterialDefinition latestVersion = materialUpdater.getLatestMaterialVersion(materialDefinition);
                if (latestVersion == null) {
                    continue;
                }

                Material dbLatestVersion = (Material) this.materialService.getLatestMaterial(materialDefinition.getId()).getObject();
                boolean areTheSame = materialUpdater.areMaterialsSameVersion(latestVersion, dbLatestVersion.getMaterialDefinition());
                if (!areTheSame) {
                    triggerMaterials.append(materialDefinition.getName()).append(" ");
                }
            }
        }

        return triggerMaterials.toString();
    }
}
