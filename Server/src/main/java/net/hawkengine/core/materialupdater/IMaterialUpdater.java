package net.hawkengine.core.materialupdater;

import net.hawkengine.model.MaterialDefinition;

public interface IMaterialUpdater<T extends MaterialDefinition> {
    MaterialDefinition getLatestMaterialVersion(T materialDefinition);

    boolean areMaterialsSameVersion(T latestMaterial, T dbMaterial);
}
