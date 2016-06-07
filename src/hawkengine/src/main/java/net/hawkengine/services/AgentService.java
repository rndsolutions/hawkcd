package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Agent;
import net.hawkengine.model.AgentExecutionState;
import net.hawkengine.model.ConfigState;

import java.util.List;
import java.util.stream.Collectors;

public class AgentService implements IAgentService {
	private IDbRepository<Agent> repository;


	/**
	 * The AgentService object provides a service that handles
	 * agent configuration management to its caller.
	 * It calls add, update, delete and more on the database
	 * for a specific agent.
	 */
	public AgentService() {
		this.repository = new RedisRepository(Agent.class);
	}

	public AgentService(IDbRepository<Agent> repository) {
		this.repository = repository;
	}

	/**
	 * Gets an Agent by ID.
	 *
	 * @param agentId The id of the Agent we want to access.
	 * @return The agent of the specified id.
	 * @throws Exception
	 * @see Agent
	 */
	@Override
	public Agent getAgentById(String agentId) throws Exception {
		if (agentId == null || agentId.trim().length() == 0) {
			return null;
		}

		Agent agent = this.repository.getById(agentId);
		return agent;
	}

	/**
	 * Returns a list of all agents that are in the database.
	 *
	 * @return list of all agents.
	 * @see Agent
	 */
	@Override
	public List<Agent> getAllAgents() throws Exception {
		return this.repository.getAll();
	}

	/**
	 * Adds new agent to the database.
	 *
	 * @param agent Type stored in  a list.
	 * @see Agent
	 */
	@Override
	public String addAgent(Agent agent) throws Exception {
		StringBuilder errorMessage = new StringBuilder("");
		if (agent != null) {
			this.repository.add(agent);
		} else {
			errorMessage.append("Unable to save Agent.");
		}

		return errorMessage.toString();
	}

	/**
	 * Updates a specific Agent in the database, specified by the methods caller.
	 *
	 * @param agent Type stored in a list.
	 * @see Agent
	 */
	@Override
	public Agent updateAgent(Agent agent) throws Exception {
		StringBuilder errorMessage = new StringBuilder("");
		Agent updatedAgent = new Agent();
		if (agent != null) {
			updatedAgent = repository.update(agent);
		}

		return agent;
	}

	/**
	 * Deletes an agent from the database.
	 *
	 * @param agentId Type stored in a list.
	 * @see Agent
	 */
	@Override
	public String deleteAgent(String agentId) throws Exception {
		StringBuilder errorMessage = new StringBuilder("");
		boolean isDeleted = repository.delete(agentId);
		if (!isDeleted) {
			errorMessage.append("Agent not deleted.");
		}

		return errorMessage.toString();
	}

	/**
	 * Gets all agents from the database whom status is set to 'Enabled'.
	 *
	 * @return A list of enabled agents.
	 * @see Agent
	 */
	@Override
	public List<Agent> getAllEnabledAgents() throws Exception {
		List<Agent> agents = repository.getAll()
				.stream()
				.filter(a -> a.getConfigState() == ConfigState.Enabled)
				.collect(Collectors.toList());

		return agents;
	}

	/**
	 * Gets all agents from the database who are idle and waiting for a job.
	 *
	 * @return A list of idle agents.
	 * @see Agent
	 */
	@Override
	public List<Agent> getAllEnabledIdleAgents() throws Exception {
		List<Agent> agents = repository.getAll()
				.stream()
				.filter(a -> a.getConfigState() == ConfigState.Enabled && a.getExecutionState() == AgentExecutionState.Idle)
				.collect(Collectors.toList());

		return agents;
	}

	/**
	 * This methods sets the agents configuration state which
	 * can be one of two states: 'Enabled' or 'Disabled'.
	 *  @param agentId id of the agent.
	 * @param state   state of the agent.
	 */
	@Override
	public Agent setAgentConfigState(String agentId, ConfigState state) throws Exception {
		Agent agent = this.repository.getById(agentId);

		if (agent != null) {
			agent.setConfigState(state);
			agent = this.repository.update(agent);
		}

		return agent;
	}
}
