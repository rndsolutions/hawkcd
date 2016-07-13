package net.hawkengine.core.materialhandler.materialupdaters;

import net.hawkengine.model.MaterialDefinition;

public abstract class MaterialUpdater<T extends MaterialDefinition> implements IMaterialUpdater<T> {
    @Override
    public abstract T getLatestMaterialVersion(T materialDefinition);

    @Override
    public abstract boolean areMaterialsSameVersion(T latestMaterial, T dbMaterial);
}
