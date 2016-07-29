package net.hawkengine.services;

import net.hawkengine.core.utilities.constants.ServerMessages;
import net.hawkengine.db.DbRepositoryFactory;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.*;
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
        List<MaterialDefinition> materialDefinitions = new ArrayList<>();

        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        if (pipelineDefinition != null) {
            for (String materialDefinitionId : pipelineDefinition.getMaterialDefinitionIds()) {
                MaterialDefinition materialDefinition = (MaterialDefinition) this.getById(materialDefinitionId).getObject();
                if (materialDefinition != null) {
                    materialDefinitions.add(materialDefinition);
                }
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
        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
        List<String> assignedIds = new ArrayList<>();
        for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
            for (String assignedId : pipelineDefinition.getMaterialDefinitionIds()) {
                if (assignedId.equals(materialDefinitionId)) {
                    assignedIds.add(assignedId);
                }
            }
        }

        if (!assignedIds.isEmpty()) {
            String assignedIdsAsString = String.join(", ", assignedIds);
            return super.createServiceResult(null, true, String.format(ServerMessages.COULD_NOT_BE_DELETED, assignedIdsAsString));
        }

        return super.delete(materialDefinitionId);
    }
}
