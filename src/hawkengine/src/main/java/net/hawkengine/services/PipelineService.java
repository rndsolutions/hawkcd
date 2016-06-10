package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Pipeline;

public class PipelineService extends CrudService<Pipeline> {
    public PipelineService() {
        super.repository = new RedisRepository(Pipeline.class);
    }

    public PipelineService(IDbRepository repository) {
        super.repository = repository;
    }
}
