package net.hawkengine.services.interfaces;

import net.hawkengine.model.Agent;
import net.hawkengine.model.ServiceResult;

public interface IAgentService extends ICrudService<Agent> {
    ServiceResult getAllAssignableAgents();
}
