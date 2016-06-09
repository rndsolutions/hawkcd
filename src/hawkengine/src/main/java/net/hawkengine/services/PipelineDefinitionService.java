package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;

import java.util.ArrayList;
import java.util.List;

public class PipelineDefinitionService extends CrudService<PipelineDefinition> implements IPipelineDefinitionService {

    private IDbRepository<PipelineDefinition> repository;
    private ServiceResult serviceResult;

    public PipelineDefinitionService() {
        super.repository = new RedisRepository(PipelineDefinition.class);
    }

    public PipelineDefinitionService(IDbRepository repository) {
        super.repository = repository;
    }

    public ServiceResult getById(String pipelineDefinitionId) {
        return super.getById(pipelineDefinitionId);
    }

    public ServiceResult getAll() {
        return super.getAll();
    }

    public ServiceResult add(PipelineDefinition pipelineDefinition) {
        return super.add(pipelineDefinition);
    }

    public ServiceResult update(PipelineDefinition pipelineDefinition) {
        return super.update(pipelineDefinition);
    }

    public ServiceResult delete(String pipelineDefinitionId) {
        return super.delete(pipelineDefinitionId);
    }
}
