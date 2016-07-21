package net.hawkengine.core.materialhandler.materialupdaters;

import net.hawkengine.core.materialhandler.materialservices.INuGetService;
import net.hawkengine.core.materialhandler.materialservices.NuGetService;
import net.hawkengine.model.MaterialDefinition;

public class NuGetMaterialUpdater extends MaterialUpdater {
    private INuGetService nuGetService;

    public NuGetMaterialUpdater() {
        this.nuGetService = new NuGetService();
    }

    public NuGetMaterialUpdater(INuGetService nuGetService) {
        this.nuGetService = nuGetService;
    }

    @Override
    public MaterialDefinition getLatestMaterialVersion(MaterialDefinition materialDefinition) {
        return null;
    }

    @Override
    public boolean areMaterialsSameVersion(MaterialDefinition latestMaterial, MaterialDefinition dbMaterial) {
        return false;
    }
}
