package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Agent;
import net.hawkengine.model.AgentExecutionState;
import net.hawkengine.model.ConfigState;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;

import java.util.List;
import java.util.stream.Collectors;

public class AgentService extends CrudService<Agent> implements IAgentService {
	private IDbRepository<Agent> repository;
	private ServiceResult serviceResult;

	public AgentService() {
		super.repository = new RedisRepository(Agent.class);
	}

	public AgentService(IDbRepository repository) {
		super.repository = repository;
	}

	@Override
	public ServiceResult getById(String agentId) {
		return super.getById(agentId);
	}

	@Override
	public ServiceResult getAll() {
		return super.getAll();
	}

	@Override
	public ServiceResult add(Agent agent) {
		return super.add(agent);
	}

	@Override
	public ServiceResult update(Agent agent) {
		return super.update(agent);
	}

	@Override
	public ServiceResult delete(String agentId) {
		return super.delete(agentId);
	}

	/**
	 * Gets all agents from the database whom status is set to 'Enabled'.
	 *
	 * @return A list of enabled agents.
	 * @see Agent
	 */

	@Override
	public ServiceResult getAllEnabledAgents() {
		List<Agent> agents = (List<Agent>) super.getAll().getObject();
		agents = agents
				.stream()
				.filter(a -> a.getConfigState() == ConfigState.Enabled)
				.collect(Collectors.toList());

		ServiceResult result = new ServiceResult();
		if(agents == null){
			result.setError(false);
			result.setObject(null);
		}
		else{
			result.setError(false);
			result.setMessage("All enabled " + agents.get(0).getClass().getSimpleName() + "s retrieved successfully.");
			result.setObject(agents);
		}

		return result;
	}

	/**
	 * Gets all agents from the database who are idle and waiting for a job.
	 *
	 * @return A list of idle agents.
	 * @see Agent
	 */

	@Override
	public ServiceResult getAllEnabledIdleAgents() {
		List<Agent> agents = (List<Agent>) super.getAll().getObject();
		agents = agents
				.stream()
				.filter(a -> a.getConfigState() == ConfigState.Enabled && a.getExecutionState() == AgentExecutionState.Idle)
				.collect(Collectors.toList());

		ServiceResult result = new ServiceResult();
		if(agents == null){
			result.setError(false);
			result.setObject(null);
		}
		else{
			result.setError(false);
			result.setMessage("All enabled idle " + agents.get(0).getClass().getSimpleName() + "s retrieved successfully.");
			result.setObject(agents);
		}

		return result;
	}

	/**
	 * This methods sets the agents configuration state which
	 * can be one of two states: 'Enabled' or 'Disabled'.
	 *  @param agentId id of the agent.
	 * @param state   state of the agent.
	 */

	@Override
	public ServiceResult setAgentConfigState(String agentId, ConfigState state) {
		Agent agent = (Agent) super.getById(agentId).getObject();
		ServiceResult result = new ServiceResult();

		if (agent != null) {
			agent.setConfigState(state);
			result = super.update(agent);
		}

		return result;
	}
}
