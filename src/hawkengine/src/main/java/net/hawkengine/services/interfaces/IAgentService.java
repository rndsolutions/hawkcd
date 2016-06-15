package net.hawkengine.services.interfaces;

import net.hawkengine.model.Agent;
import net.hawkengine.model.ServiceResult;

public interface IAgentService extends ICrudService<Agent> {
    @Override
    ServiceResult getById(String agentId);

    @Override
    ServiceResult getAll();

    @Override
    ServiceResult add(Agent agent);

    @Override
    ServiceResult update(Agent agent);

    @Override
    ServiceResult delete(String agentId);

    ServiceResult getAllEnabledAgents();

    ServiceResult getAllEnabledIdleAgents();
}
