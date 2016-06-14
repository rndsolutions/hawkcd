package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Agent;
import net.hawkengine.model.AgentExecutionState;
import net.hawkengine.model.enums.ConfigState;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.IAgentService;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("UnnecessarySuperQualifier")
public class AgentService extends CrudService<Agent> implements IAgentService {
    public AgentService() {
        super.repository = new RedisRepository(Agent.class);
        super.objectType = "Agent";
    }

    public AgentService(IDbRepository repository) {
        super.repository = repository;
        super.objectType = "Agent";
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
     * Gets all agents from the database whom status is set to 'ENABLED'.
     *
     * @return A list of enabled agents.
     * @see Agent
     */

    @Override
    public ServiceResult getAllEnabledAgents() {
        List<Agent> agents = (List<Agent>) super.getAll().getObject();
        agents = agents
                .stream()
                .filter(a -> a.isEnabled())
                .collect(Collectors.toList());

        ServiceResult result = new ServiceResult();
        result.setError(false);
        result.setMessage("All enabled " + super.objectType + "s retrieved successfully.");
        result.setObject(agents);

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
                .filter(a -> a.isEnabled() && !a.isRunning())
                .collect(Collectors.toList());

        ServiceResult result = new ServiceResult();
        result.setError(false);
        result.setMessage("All enabled idle " + super.objectType + "s retrieved successfully.");
        result.setObject(agents);

        return result;
    }
}
