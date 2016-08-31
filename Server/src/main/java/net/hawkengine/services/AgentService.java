package net.hawkengine.services;

import net.hawkengine.db.DbRepositoryFactory;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.model.enums.StageStatus;
import net.hawkengine.model.payload.WorkInfo;
import net.hawkengine.services.interfaces.IAgentService;
import net.hawkengine.services.interfaces.IJobService;
import net.hawkengine.services.interfaces.IPipelineService;
import net.hawkengine.ws.EndpointConnector;

import java.util.List;
import java.util.stream.Collectors;

public class AgentService extends CrudService<Agent> implements IAgentService {
    private static final Class CLASS_TYPE = Agent.class;

    private IPipelineService pipelineService;
    private IJobService jobService;

    public AgentService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.pipelineService = new PipelineService();
        this.jobService = new JobService();
    }

    public AgentService(IDbRepository repository, IPipelineService pipelineService) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
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
        ServiceResult result = super.add(agent);
        EndpointConnector.passResultToEndpoint(AgentService.class.getSimpleName(), "add", result);
        return result;
    }

    @Override
    public ServiceResult update(Agent agent) {
        ServiceResult result = super.update(agent);
        EndpointConnector.passResultToEndpoint(AgentService.class.getSimpleName(), "update", result);
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
            result = updateResult(result, null, true, "This agent has no job assigned.");
        } else if (agent.isAssigned()) {
            List<Pipeline> pipelines = (List<Pipeline>) this.pipelineService.getAllPreparedPipelinesInProgress().getObject();
            for (Pipeline pipeline : pipelines) {
                WorkInfo workInfo = new WorkInfo();
                Stage stageInProgress = pipeline.getStages()
                        .stream()
                        .filter(s -> s.getStatus() == StageStatus.IN_PROGRESS)
                        .findFirst()
                        .orElse(null);
                if (stageInProgress == null) {
                    continue;
                }

                Job scheduledJob = stageInProgress
                        .getJobs()
                        .stream()
                        .filter(j -> j.getStatus() == JobStatus.ASSIGNED)
                        .filter(j -> j.getAssignedAgentId().equals(agentId))
                        .findFirst()
                        .orElse(null);
                if (scheduledJob == null) {
                    continue;
                }

                workInfo.setPipelineDefinitionName(pipeline.getPipelineDefinitionName());
                workInfo.setPipelineExecutionID(pipeline.getExecutionId());
                workInfo.setStageDefinitionName(stageInProgress.getStageDefinitionName());
                workInfo.setStageExecutionID(stageInProgress.getExecutionId());
                workInfo.setJobDefinitionName(scheduledJob.getJobDefinitionName());
                scheduledJob.setStatus(JobStatus.RUNNING);
                workInfo.setJob(scheduledJob);
                this.jobService.update(scheduledJob);

                result = updateResult(result, workInfo, false, "WorkInfo retrieved successfully");
            }
        } else {
            result = updateResult(result, null, true, "This agent has no job assigned.");
        }

        if (result.getMessage() == null) {
            agent.setAssigned(false);
            this.update(agent);
            result = updateResult(result, null, true, "This agent has no job assigned.");
        }

        return result;
    }

    private ServiceResult updateResult(ServiceResult result, Object object, boolean error, String message) {
        result.setObject(object);
        result.setError(error);
        result.setMessage(message);

        return result;
    }
}
