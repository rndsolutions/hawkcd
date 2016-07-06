package net.hawkengine.core.materialupdater;

import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.PipelineDefinition;

public class MaterialTrackerService implements IMaterialTrackerService {

    @Override
    public String checkPipelineForTriggerMaterials(PipelineDefinition pipelineDefinition) {
        StringBuilder triggerMaterials = new StringBuilder();
        for (MaterialDefinition materialDefinition : pipelineDefinition.getMaterials()) {
            if (materialDefinition.isPollingForChanges()) {
                MaterialUpdater materialUpdater = MaterialUpdaterFactory.create(materialDefinition.getType());
                MaterialDefinition latestVersion = materialUpdater.getLatestMaterialVersion(materialDefinition);
                MaterialDefinition dbLatestVersion = this.materialDefinitionService.getLatestMaterialVersion();
                boolean areTheSame = materialUpdater.areMaterialsSameVersion(latestVersion, dbLatestVersion);
                if (!areTheSame) {
                    triggerMaterials.append(materialDefinition.getName());
                }
            }
        }

        return triggerMaterials.toString();
    }
}
