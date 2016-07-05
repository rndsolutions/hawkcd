package net.hawkengine.core.materialupdater;

import net.hawkengine.model.GitMaterial;
import net.hawkengine.model.MaterialDefinition;

public class MaterialUpdater implements IMaterialUpdater {

    @Override
    public MaterialDefinition getLatestMaterialVersion(MaterialDefinition materialDefinition) {
        GitMaterial material = (GitMaterial)materialDefinition;

        return material;
    }
}
