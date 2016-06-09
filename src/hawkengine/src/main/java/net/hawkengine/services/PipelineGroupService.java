package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.ServiceResult;

import java.util.List;

public class PipelineGroupService extends CrudService<PipelineGroup> implements IPipelineGroupService {

	private IDbRepository<PipelineGroup> repository;
	private ServiceResult serviceResult;

	public PipelineGroupService() {
		super.repository = new RedisRepository(PipelineGroup.class);
	}

	public PipelineGroupService(IDbRepository repository) {
		super.repository = repository;
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
