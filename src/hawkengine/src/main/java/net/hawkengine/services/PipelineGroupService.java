package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.PipelineGroup;

import java.util.List;

public class PipelineGroupService implements IPipelineGroupService {
	private IDbRepository<PipelineGroup> repository;

	public PipelineGroupService() {
		this.repository = new RedisRepository(PipelineGroup.class);
	}

	public PipelineGroupService(IDbRepository<PipelineGroup> repository) {
		this.repository = repository;
	}

	@Override
	public List<PipelineGroup> getAllPipelineGroups() throws Exception {
		return this.repository.getAll();
	}

	@Override
	public PipelineGroup getPipelineGroup(String pipelineGroupName) throws Exception {
		return null;
	}

	@Override
	public PipelineGroup getPipelineGroupWithLatestPipelineExecution(String pipelineGroupName) throws Exception {
		return null;
	}

	@Override
	public String addPipelineGroup(PipelineGroup pipelineGroup) throws Exception {
		return null;
	}

	@Override
	public String updatePipelineGroup(String pipelineGroupName, PipelineGroup newPipelineGroup) throws Exception {
		return null;
	}

	@Override
	public String deletePipelineGroup(String pipelineGroupName) throws Exception {
		return null;
	}
}
