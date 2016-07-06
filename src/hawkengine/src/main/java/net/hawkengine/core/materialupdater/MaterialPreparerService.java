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

    public MaterialPreparerService() {
        this.materialService = new MaterialService();
    }

    @Override
    public Pipeline updatePipelineMaterials(Pipeline pipeline) {
        pipeline.setMaterialsUpdated(true);
        List<Material> materials = pipeline.getMaterials();
        for (Material material : materials) {
            MaterialUpdater materialUpdater = MaterialUpdaterFactory.create(material.getMaterialDefinition().getType());
            MaterialDefinition latestVersion = materialUpdater.getLatestMaterialVersion(material.getMaterialDefinition());
            if (latestVersion == null) {
                pipeline.setMaterialsUpdated(false);
                break;
            }

            Material dbLatestVersion = (Material) this.materialService.getLatestMaterial(material.getMaterialDefinition().getId()).getObject();
            boolean areTheSame = materialUpdater.areMaterialsSameVersion(latestVersion, dbLatestVersion.getMaterialDefinition());
            if (areTheSame) {
                material.setMaterialDefinition(latestVersion);
                material.setChangeDate(LocalDateTime.now());
            }
        }

        return null;
    }
}
