package net.hawkengine.services;

import net.hawkengine.model.Agent;
import net.hawkengine.model.ConfigState;

import java.util.List;

public interface IAgentService {
	Agent getAgentById(String agentId) throws Exception;

	List<Agent> getAllAgents() throws Exception;

	String addAgent(Agent agent) throws Exception;

	Agent updateAgent(Agent agent) throws Exception;

	String deleteAgent(String agentId) throws Exception;

	List<Agent> getAllEnabledAgents() throws Exception;

	List<Agent> getAllEnabledIdleAgents() throws Exception;

	Agent setAgentConfigState(String agentId, ConfigState state) throws Exception;
}
