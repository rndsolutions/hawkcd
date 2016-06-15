package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;

public class PipelineDefinitionService extends CrudService<PipelineDefinition> implements IPipelineDefinitionService {
    public PipelineDefinitionService() {
        super.setRepository(new RedisRepository(PipelineDefinition.class));
        super.setObjectType("PipelineDefinition");
    }

    public PipelineDefinitionService(IDbRepository repository) {
        super.setRepository(repository);
        super.setObjectType("PipelineDefinition");
    }

    @Override
    public ServiceResult getById(String pipelineDefinitionId) {
        return super.getById(pipelineDefinitionId);
    }

    @Override
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    public ServiceResult add(PipelineDefinition pipelineDefinition) {
        return super.add(pipelineDefinition);
    }

    @Override
    public ServiceResult update(PipelineDefinition pipelineDefinition) {
        return super.update(pipelineDefinition);
    }

    @Override
    public ServiceResult delete(String pipelineDefinitionId) {
        return super.delete(pipelineDefinitionId);
    }
}
