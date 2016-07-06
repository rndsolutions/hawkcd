package net.hawkengine.core.materialupdater;

import net.hawkengine.model.MaterialDefinition;

public interface IMaterialUpdater {
    MaterialDefinition getLatestMaterialVersion(MaterialDefinition materialDefinition);

    boolean areMaterialsSameVersion(MaterialDefinition latestMaterial, MaterialDefinition dbMaterial);
}
