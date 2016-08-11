package net.hawkengine.services;

import net.hawkengine.db.DbRepositoryFactory;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineGroupService;

import java.util.ArrayList;
import java.util.List;

public class PipelineGroupService extends CrudService<PipelineGroup> implements IPipelineGroupService {
    private static final Class CLASS_TYPE = PipelineGroup.class;

    private IPipelineDefinitionService pipelineDefinitionService;

    public PipelineGroupService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.pipelineDefinitionService = new PipelineDefinitionService();
    }

    public PipelineGroupService(IDbRepository repository) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    @Override
    public ServiceResult getById(String pipelineGroupId) {
        return super.getById(pipelineGroupId);
    }

    @Override
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    public ServiceResult add(PipelineGroup pipelineGroup) {
        return super.add(pipelineGroup);
    }

    @Override
    public ServiceResult update(PipelineGroup pipelineGroup) {
        return super.update(pipelineGroup);
    }

    @Override
    public ServiceResult delete(String pipelineGroupId) {
        return super.delete(pipelineGroupId);
    }

    @Override
    public ServiceResult getAllPipelineGroupDTOs() {
        List<PipelineGroup> pipelineGroups = (List<PipelineGroup>) super.getAll().getObject();
        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) pipelineDefinitionService.getAll().getObject();

        ServiceResult result = new ServiceResult();
        for (PipelineGroup pipelineGroup : pipelineGroups) {
            List<PipelineDefinition> pipelineDefinitionsToAdd = new ArrayList<>();
            for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
                if (!pipelineDefinition.getPipelineGroupId().isEmpty() && pipelineDefinition.getPipelineGroupId().equals(pipelineGroup.getId())) {
                    pipelineDefinitionsToAdd.add(pipelineDefinition);
                }
            }

            pipelineGroup.setPipelines(pipelineDefinitionsToAdd);
        }

        result.setError(false);
        result.setMessage("All Pipeline Group DTOs retrieved successfully.");
        result.setObject(pipelineGroups);

        return result;
    }
}
