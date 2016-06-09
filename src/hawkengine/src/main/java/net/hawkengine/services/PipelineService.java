package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Pipeline;
import net.hawkengine.services.interfaces.IPipelineService;

public class PipelineService extends CrudService<Pipeline>  implements IPipelineService {
    public PipelineService() {
        super.repository = new RedisRepository(Pipeline.class);
    }

    public PipelineService(IDbRepository repository) {
        super.repository = repository;
    }
}
