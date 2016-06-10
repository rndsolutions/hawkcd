package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.IPipelineService;

public class PipelineService extends CrudService<Pipeline> implements IPipelineService {
    public PipelineService() {
        super.repository = new RedisRepository(Pipeline.class);
        super.objectType = "Pipeline";
    }

    public PipelineService(IDbRepository repository) {
        super.repository = repository;
    }

    @Override
    public ServiceResult getById(String pipelineId) {
        return super.getById(pipelineId);
    }

    @Override
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    public ServiceResult add(Pipeline pipeline) {
        return super.add(pipeline);
    }

    @Override
    public ServiceResult update(Pipeline pipeline) {
        return super.update(pipeline);
    }

    @Override
    public ServiceResult delete(String pipelineId) {
        return super.delete(pipelineId);
    }
}
