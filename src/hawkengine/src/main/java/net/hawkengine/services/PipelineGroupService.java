package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.IPipelineGroupService;

public class PipelineGroupService extends CrudService<PipelineGroup> implements IPipelineGroupService {
    public PipelineGroupService() {
        super.setRepository(new RedisRepository(PipelineGroup.class));
        super.setObjectType("PipelineGroup");
    }

    public PipelineGroupService(IDbRepository repository) {
        super.setRepository(repository);
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
}
