package net.hawkengine.services;

import net.hawkengine.core.utilities.EndpointConnector;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Agent;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.model.enums.StageStatus;
import net.hawkengine.model.payload.WorkInfo;
import net.hawkengine.services.interfaces.IAgentService;
import net.hawkengine.services.interfaces.IPipelineService;

import java.util.List;
import java.util.stream.Collectors;

public class AgentService extends CrudService<Agent> implements IAgentService {
    private IPipelineService pipelineService;

    public AgentService() {
        super.setRepository(new RedisRepository(Agent.class));
        super.setObjectType("Agent");
        this.pipelineService = new PipelineService();
    }

    public AgentService(IDbRepository repository, IPipelineService pipelineService) {
        super.setRepository(repository);
        super.setObjectType("Agent");
        this.pipelineService = pipelineService;
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

    public ServiceResult getWorkInfo(String agentId) {
        ServiceResult result = new ServiceResult();
        Agent agent = (Agent) this.getById(agentId).getObject();
        if (agent == null) {
            result.setObject(null);
            result.setError(true);
            result.setMessage("This agent has no job assigned.");
        } else if (agent.isAssigned()) {
            List<Pipeline> pipelines = (List<Pipeline>) this.pipelineService.getAllPreparedPipelinesInProgress().getObject();
            for (Pipeline pipeline : pipelines) {
                WorkInfo workInfo = new WorkInfo();
                pipeline.getStages()
                        .stream()
                        .filter(stage -> stage.getStatus() == StageStatus.IN_PROGRESS)
                        .forEach(stage -> stage.getJobs()
                                .stream()
                                .filter(job -> job.getStatus() == JobStatus.SCHEDULED)
                                .filter(job -> job.getAssignedAgentId().equals(agentId))
                                .forEach(job -> {
                                    workInfo.setPipelineExecutionID(pipeline.getExecutionId());
                                    workInfo.setStageExecutionID(stage.getExecutionId());
                                    workInfo.setJob(job);
                                    workInfo.setPipelineDefinitionName(pipeline.getPipelineDefinitionName());

                                    result.setObject(workInfo);
                                    result.setError(false);
                                    result.setMessage("WorkInfo retrieved successfully");
                                }));
            }
        } else {
            result.setObject(null);
            result.setError(true);
            result.setMessage("This agent has no job assigned.");
        }

        return result;
    }
}
