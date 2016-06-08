//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:14 PM
//

package net.hawkengine.core;

import java.util.UUID;

import net.hawkengine.model.ConfigState;
import net.hawkengine.model.payload.AgentStatusReport;
import net.hawkengine.model.payload.ServerResponseToAgent;

public interface IAgentManager {
	void start() throws Exception;

	void updateAgentStatus(AgentStatusReport agentStatus) throws Exception;

	void registerPendingAgent(UUID agentId) throws Exception;

	void setAgentState(UUID agentId, ConfigState state) throws Exception;

	ServerResponseToAgent getWorkForAgent(UUID agentId) throws Exception;

}
