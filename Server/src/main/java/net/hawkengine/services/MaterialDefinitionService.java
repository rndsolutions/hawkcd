package net.hawkengine.services;

import net.hawkengine.core.utilities.constants.ServerMessages;
import net.hawkengine.db.DbRepositoryFactory;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.TaskType;
import net.hawkengine.services.interfaces.IMaterialDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;

import java.util.ArrayList;
import java.util.List;

public class MaterialDefinitionService extends CrudService<MaterialDefinition> implements IMaterialDefinitionService {
    private static final Class CLASS_TYPE = MaterialDefinition.class;

    private IPipelineDefinitionService pipelineDefinitionService;

    public MaterialDefinitionService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.pipelineDefinitionService = new PipelineDefinitionService();
    }

    public MaterialDefinitionService(IDbRepository repository, IPipelineDefinitionService pipelineDefinitionService) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.pipelineDefinitionService = pipelineDefinitionService;
    }

    @Override
    public ServiceResult getById(String materialDefinitionId) {
        return super.getById(materialDefinitionId);
    }

    @Override
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    public ServiceResult getAllFromPipelineDefinition(String pipelineDefinitionId) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        List<MaterialDefinition> materialDefinitions = new ArrayList<>();
        for (String materialDefinitionId : pipelineDefinition.getMaterialDefinitionIds()) {
            MaterialDefinition materialDefinition = (MaterialDefinition) this.getById(materialDefinitionId).getObject();
            if (materialDefinition != null) {
                materialDefinitions.add(materialDefinition);
            }
        }

        return super.createServiceResultArray(materialDefinitions, false, ServerMessages.RETRIEVED_SUCCESSFULLY);
    }

    @Override
    public ServiceResult add(MaterialDefinition materialDefinition) {
        return super.add(materialDefinition);
    }

    @Override
    public ServiceResult add(GitMaterial materialDefinition) {
        return super.add(materialDefinition);
    }

    @Override
    public ServiceResult add(NugetMaterial materialDefinition) {
        return super.add(materialDefinition);
    }

    @Override
    public ServiceResult update(MaterialDefinition materialDefinition) {
        return super.update(materialDefinition);
    }

    @Override
    public ServiceResult update(GitMaterial materialDefinition) {
        return super.update(materialDefinition);
    }

    @Override
    public ServiceResult update(NugetMaterial materialDefinition) {
        return super.update(materialDefinition);
    }

    @Override
    public ServiceResult delete(String materialDefinitionId) {
        MaterialDefinition materialDefinition = (MaterialDefinition) this.getById(materialDefinitionId).getObject();
        if (materialDefinition == null) {
            return super.createServiceResult(materialDefinition, true, ServerMessages.NOT_FOUND);
        }

        List<TaskDefinition> taskDefinitions = null;
        for (TaskDefinition taskDefinition : taskDefinitions) {
            TaskType taskType = taskDefinition.getType();
            if (taskType == TaskType.FETCH_MATERIAL) {
                FetchMaterialTask fetchMaterialTask = (FetchMaterialTask) taskDefinition;
                String materialName = fetchMaterialTask.getMaterialName();
                if (materialDefinition.getName().equals(materialName)) {
                    return super.createServiceResult(null, true, String.format(ServerMessages.COULD_NOT_BE_DELETED, fetchMaterialTask.getPipelineName()));
                }
            }
        }

        ServiceResult result = super.delete(materialDefinitionId);
        if (result.hasError()) {
            return result;
        }

        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
        for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
            List<String> materialDefinitionIds = pipelineDefinition.getMaterialDefinitionIds();
            for (String definitionId : materialDefinitionIds) {
                if (materialDefinitionId.equals(definitionId)) {
                    materialDefinitionIds.remove(definitionId);
                }
            }
        }

        return result;
    }
}
