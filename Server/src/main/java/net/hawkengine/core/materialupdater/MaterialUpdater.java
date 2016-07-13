package net.hawkengine.core.materialupdater;

import net.hawkengine.model.MaterialDefinition;

public abstract class MaterialUpdater<T extends MaterialDefinition> implements IMaterialUpdater<T> {
    @Override
    public abstract MaterialDefinition getLatestMaterialVersion(T materialDefinition);

    @Override
    public abstract boolean areMaterialsSameVersion(T latestMaterial, T dbMaterial);
}
