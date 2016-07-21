package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Material;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.IMaterialService;

import java.util.List;

public class MaterialService extends CrudService<Material> implements IMaterialService {
    public MaterialService() {
        super.setRepository(new RedisRepository(Material.class));
        super.setObjectType("Material");
    }

    public MaterialService(IDbRepository repository) {
        super.setRepository(repository);
        super.setObjectType("Material");
    }

    @Override
    public ServiceResult getById(String materialId) {
        return super.getById(materialId);
    }

    @Override
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    public ServiceResult add(Material material) {
        return super.add(material);
    }

    @Override
    public ServiceResult update(Material material) {
        return super.update(material);
    }

    @Override
    public ServiceResult delete(String materialId) {
        return super.delete(materialId);
    }

    @Override
    public ServiceResult getLatestMaterial(String materialDefinitionId) {
        List<Material> materials = (List<Material>) this.getAll().getObject();
        Material latestMaterial = materials
                .stream()
                .filter(m -> m.getMaterialDefinition().getId().equals(materialDefinitionId))
                .sorted((m1, m2) -> m2.getChangeDate().compareTo(m1.getChangeDate()))
                .findFirst()
                .orElse(null);

        if (latestMaterial != null) {
            return super.createServiceResult(latestMaterial, false, "retrieved successfully");
        } else {
            return super.createServiceResult(latestMaterial, true, "not found");
        }
    }
}
