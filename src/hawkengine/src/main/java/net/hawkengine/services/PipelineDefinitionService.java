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
    //TODO: Find a way to make CrudService know what type the child is
    //public String type = "PipelineDefinition";

    public PipelineDefinitionService() {
        super.repository = new RedisRepository(PipelineDefinition.class);
        super.type = "Pipeline Definition";
    }

    public PipelineDefinitionService(IDbRepository repository) {
        super.repository = repository;
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
