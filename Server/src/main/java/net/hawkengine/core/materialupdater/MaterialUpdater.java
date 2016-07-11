package net.hawkengine.core.materialupdater;

import net.hawkengine.model.MaterialDefinition;

public abstract class MaterialUpdater implements IMaterialUpdater {
    @Override
    public abstract MaterialDefinition getLatestMaterialVersion(MaterialDefinition materialDefinition);

    @Override
    public abstract boolean areMaterialsSameVersion(MaterialDefinition latestMaterial, MaterialDefinition dbMaterial);
}
