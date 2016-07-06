package net.hawkengine.core.materialupdater;

import net.hawkengine.model.enums.MaterialType;

public class MaterialUpdaterFactory {
    public static MaterialUpdater create(MaterialType materialType) {
        switch (materialType) {
            case GIT:
                return new GitMaterialUpdater();
            default:
                return null;
        }
    }
}
