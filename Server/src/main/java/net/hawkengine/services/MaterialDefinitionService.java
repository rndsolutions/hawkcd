package net.hawkengine.services;

import net.hawkengine.core.utilities.constants.ConfigurationConstants;
import net.hawkengine.db.DbRepositoryFactory;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.MaterialType;
import net.hawkengine.services.interfaces.IMaterialDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;

import java.util.ArrayList;
import java.util.List;

public class MaterialDefinitionService extends CrudService<MaterialDefinition> implements IMaterialDefinitionService {
    private static final Class CLASS_TYPE = MaterialDefinition.class;

    private IPipelineDefinitionService pipelineDefinitionService;
    private String successMessage = "retrieved successfully";
    private String failureMessage = "not found";

    public MaterialDefinitionService() {
        IDbRepository repository = DbRepositoryFactory.create(ConfigurationConstants.DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        this.pipelineDefinitionService = new PipelineDefinitionService();
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    public MaterialDefinitionService(IPipelineDefinitionService pipelineDefinitionService) {
        this.pipelineDefinitionService = pipelineDefinitionService;
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    @Override
    public ServiceResult getById(String materialDefinitionId) {
        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
        MaterialDefinition result = null;

        for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
            for (MaterialDefinition materialDefinition : pipelineDefinition.getMaterialDefinitions()) {
                if (materialDefinition.getId().equals(materialDefinitionId)) {
                    result = materialDefinition;
                    return super.createServiceResult(result, false, this.successMessage);
                }
            }
        }

        return super.createServiceResult(result, true, this.failureMessage);
    }

    @Override
    public ServiceResult getAll() {
        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
        List<MaterialDefinition> materialDefinitions = new ArrayList<>();

        for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
            materialDefinitions.addAll(pipelineDefinition.getMaterialDefinitions());
        }

        return super.createServiceResultArray(materialDefinitions, false, this.successMessage);
    }

    @Override
    public ServiceResult add(GitMaterial materialDefinition) {
        return this.addMaterialDefinition(materialDefinition);
    }

    @Override
    public ServiceResult add(NugetMaterial materialDefinition) {
        return this.addMaterialDefinition(materialDefinition);
    }

    public ServiceResult addMaterialDefinition(MaterialDefinition materialDefinition) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(materialDefinition.getPipelineDefinitionId()).getObject();
        List<MaterialDefinition> materialDefinitions = pipelineDefinition.getMaterialDefinitions();
        List<MaterialDefinition> allMaterialDefinitions = (List<MaterialDefinition>) this.getAll().getObject();
        for (MaterialDefinition materialDefinitionToCheck : allMaterialDefinitions) {
            if (materialDefinitionToCheck.getId().equals(materialDefinition.getId())) {
                return super.createServiceResult(null, true, "already exists");
            }
        }
        boolean hasNameCollision = this.checkForNameCollision(materialDefinitions, materialDefinition);
        if (hasNameCollision) {
            return super.createServiceResult(materialDefinition, true, "with the same name exists");
        }

        materialDefinitions.add(materialDefinition);
        pipelineDefinition.setMaterialDefinitions(materialDefinitions);
        PipelineDefinition updatedPipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.update(pipelineDefinition).getObject();

        MaterialDefinition result = this.extractMaterialDefinitionFromPipelineDefinition(updatedPipelineDefinition, materialDefinition.getId());
        if (result == null) {
            return super.createServiceResult(result, true, "not added successfully");
        }

        return super.createServiceResult(result, false, "added successfully");
    }

    @Override
    public ServiceResult update(GitMaterial materialDefinition) {
        return this.updateMaterialDefinition(materialDefinition);
    }

    @Override
    public ServiceResult update(NugetMaterial materialDefinition) {
        return this.updateMaterialDefinition(materialDefinition);
    }

    @Override
    public ServiceResult updateMaterialDefinition(MaterialDefinition materialDefinition) {
        MaterialDefinition result = null;

        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(materialDefinition.getPipelineDefinitionId()).getObject();
        List<MaterialDefinition> materialDefinitions = pipelineDefinition.getMaterialDefinitions();
        boolean hasNameCollision = this.checkForNameCollision(materialDefinitions, materialDefinition);
        if (hasNameCollision) {
            return super.createServiceResult(materialDefinition, true, "with the same name exists");
        }

        int lengthOfMaterialDefinitions = materialDefinitions.size();
        for (int i = 0; i < lengthOfMaterialDefinitions; i++) {
            MaterialDefinition definition = materialDefinitions.get(i);
            if (definition.getId().equals(materialDefinition.getId())) {
                Class resultMaterialClass = this.getMaterialDefinitionType(materialDefinition);
                try {
                    result = (MaterialDefinition) resultMaterialClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                materialDefinitions.set(i, materialDefinition);
                pipelineDefinition.setMaterialDefinitions(materialDefinitions);
                PipelineDefinition updatedPipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.update(pipelineDefinition).getObject();
                result = this.extractMaterialDefinitionFromPipelineDefinition(updatedPipelineDefinition, materialDefinition.getId());
                break;
            }
        }

        if (result == null) {
            return super.createServiceResult(result, true, "not found");
        }

        return super.createServiceResult(result, false, "updated successfully");
    }

    public ServiceResult delete(String materialDefinitionId) {
        boolean isRemoved = false;
        MaterialDefinition materialDefinitionToDelete = (MaterialDefinition) this.getById(materialDefinitionId).getObject();
        if (materialDefinitionToDelete == null) {
            return super.createServiceResult(materialDefinitionToDelete, true, "does not exist");
        }

        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService
                .getById(materialDefinitionToDelete.getPipelineDefinitionId())
                .getObject();
        List<MaterialDefinition> materialDefinitions = pipelineDefinition.getMaterialDefinitions();

        int lengthOfMaterialDefinitions = materialDefinitions.size();
        if (lengthOfMaterialDefinitions > 1) {
            for (int i = 0; i < lengthOfMaterialDefinitions; i++) {
                MaterialDefinition definition = materialDefinitions.get(i);
                if (definition.getId().equals(materialDefinitionToDelete.getId())) {
                    materialDefinitions.remove(definition);
                    isRemoved = true;
                    break;
                }
            }
        } else {
            return super.createServiceResult(materialDefinitionToDelete, true, "cannot delete the last material definition");
        }

        if (!isRemoved) {
            return super.createServiceResult(materialDefinitionToDelete, true, "not deleted");
        }

        pipelineDefinition.setMaterialDefinitions(materialDefinitions);
        PipelineDefinition updatedPipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.update(pipelineDefinition).getObject();
        MaterialDefinition result = this.extractMaterialDefinitionFromPipelineDefinition(updatedPipelineDefinition, materialDefinitionId);
        if (result != null) {
            return super.createServiceResult(result, true, "not deleted successfully");
        }

        return super.createServiceResult(result, false, "deleted successfully");
    }

    private boolean checkForNameCollision(List<MaterialDefinition> materialDefinitions, MaterialDefinition materialDefinitionToAdd) {
        for (MaterialDefinition materialDefinition : materialDefinitions) {
            if (materialDefinition.getId().equals(materialDefinitionToAdd.getId())) {
                continue;
            }

            if (materialDefinition.getName().equals(materialDefinitionToAdd.getName())) {
                return true;
            }
        }

        return false;
    }

    private MaterialDefinition extractMaterialDefinitionFromPipelineDefinition(PipelineDefinition pipelineDefinition, String materialDefinitionId) {
        MaterialDefinition result = pipelineDefinition
                .getMaterialDefinitions()
                .stream()
                .filter(md -> md.getId().equals(materialDefinitionId))
                .findFirst()
                .orElse(null);

        return result;
    }

    private Class<?> getMaterialDefinitionType(MaterialDefinition materialDefinition) {
        MaterialType materialDefinitionType = materialDefinition.getType();
        Class result = null;
        switch (materialDefinitionType) {
            case GIT:
                result = GitMaterial.class;
                break;
            case NUGET:
                result = NugetMaterial.class;
                break;
        }

        return result;
    }
}
