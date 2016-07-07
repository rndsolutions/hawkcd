package net.hawkengine.core.materialupdater;

import net.hawkengine.model.Material;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.Pipeline;
import net.hawkengine.services.MaterialService;
import net.hawkengine.services.interfaces.IMaterialService;

import java.time.LocalDateTime;
import java.util.List;

public class MaterialPreparerService implements IMaterialPreparerService {
    private IMaterialService materialService;
    private IMaterialUpdater materialUpdater;

    public MaterialPreparerService() {
        this.materialService = new MaterialService();
    }

    public MaterialPreparerService(IMaterialService materialService, IMaterialUpdater materialUpdater) {
        this.materialService = materialService;
        this.materialUpdater = materialUpdater;
    }

    @Override
    public Pipeline updatePipelineMaterials(Pipeline pipeline) {
        pipeline.setMaterialsUpdated(true);
        List<Material> materials = pipeline.getMaterials();
        for (Material material : materials) {
            this.materialUpdater = MaterialUpdaterFactory.create(material.getMaterialDefinition().getType());
            MaterialDefinition latestVersion = this.materialUpdater.getLatestMaterialVersion(material.getMaterialDefinition());
            if (latestVersion == null) {
                pipeline.setMaterialsUpdated(false);
                break;
            }

            Material dbLatestVersion = (Material) this.materialService.getLatestMaterial(material.getMaterialDefinition().getId()).getObject();
            boolean areTheSame = this.materialUpdater.areMaterialsSameVersion(latestVersion, dbLatestVersion.getMaterialDefinition());
            if (areTheSame) {
                material.setMaterialDefinition(latestVersion);
                material.setChangeDate(LocalDateTime.now());
            }
        }

        return null;
    }
}
