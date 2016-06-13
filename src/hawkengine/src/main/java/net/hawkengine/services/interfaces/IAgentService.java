package net.hawkengine.services.interfaces;

import net.hawkengine.model.Agent;
import net.hawkengine.model.ConfigState;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.ICrudService;

import java.util.List;

public interface IAgentService extends ICrudService<Agent> {
	ServiceResult getById(String agentId);

	ServiceResult getAll();

	ServiceResult add(Agent agent);

	ServiceResult update(Agent agent);

	ServiceResult delete(String agentId);

	ServiceResult getAllEnabledAgents();

	ServiceResult getAllEnabledIdleAgents();

	ServiceResult setAgentConfigState(String agentId, ConfigState state);
}
