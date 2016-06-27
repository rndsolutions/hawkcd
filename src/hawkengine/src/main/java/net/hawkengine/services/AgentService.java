package net.hawkengine.services;

import net.hawkengine.core.utilities.EndpointConnector;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Agent;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.IAgentService;

import java.util.List;
import java.util.stream.Collectors;

public class AgentService extends CrudService<Agent> implements IAgentService {
    public AgentService() {
        super.setRepository(new RedisRepository(Agent.class));
        super.setObjectType("Agent");
    }

    public AgentService(IDbRepository repository) {
        super.setRepository(repository);
        super.setObjectType("Agent");
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
        ServiceResult result = super.update(agent);
        EndpointConnector.passResultToEndpoint(this.getClass().getSimpleName(), this.getClass().getPackage().getName(), "update", result);

        return result;
    }

    @Override
    public ServiceResult delete(String agentId) {
        return super.delete(agentId);
    }

    @Override
    public ServiceResult getAllAssignableAgents() {
        List<Agent> agents = (List<Agent>) super.getAll().getObject();
        List<Agent> assignableAgents = agents
                .stream()
                .filter(a -> a.isConnected() && a.isEnabled() && !a.isRunning() && !a.isAssigned())
                .collect(Collectors.toList());

        ServiceResult result =
                super.createServiceResultArray(assignableAgents, false, "retrieved successfully");

        return result;
    }
}
