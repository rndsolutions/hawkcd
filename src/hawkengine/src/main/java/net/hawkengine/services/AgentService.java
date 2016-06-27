package net.hawkengine.services;

import net.hawkengine.core.utilities.EndpointConnector;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Agent;
import net.hawkengine.model.Job;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.Stage;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.model.enums.StageStatus;
import net.hawkengine.model.payload.WorkInfo;
import net.hawkengine.services.interfaces.IAgentService;

import java.util.List;
import java.util.UUID;
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

    public ServiceResult getWorkInfo(String agentId){
        boolean isAssigned = false;
        ServiceResult result = new ServiceResult();
        Agent agent = (Agent)this.getById(agentId).getObject();
        if (agent.isAssigned()){
            PipelineService pipelineService =  new PipelineService();
            List<Pipeline> pipelines = (List <Pipeline>) pipelineService.getAllPreparedPipelinesInProgress().getObject();
            for (Pipeline pipeline : pipelines){
                for (Stage stage : pipeline.getStages()){
                    if (stage.getStatus() == StageStatus.IN_PROGRESS){
                        for (Job job : stage.getJobs()){
                            if (job.getStatus() == JobStatus.SCHEDULED){
                                if (job.getAssignedAgentId().equals(agentId)){
                                    isAssigned = true;
                                    WorkInfo workInfo = new WorkInfo();
                                    workInfo.setPipelineId(UUID.fromString(agent.getId()));
                                    workInfo.setPipelineExecutionID(pipeline.getExecutionId());
                                    workInfo.setPipelineName(null); //to be added to pipeline
                                    workInfo.setPipelineEnvironmentName(null); // to be added pipeline
                                    workInfo.setPipelineTriggerReason(pipeline.getTriggerReason());
                                    workInfo.setLabelTemplate(null); // to be added to pipeline
                                    workInfo.setStageId(UUID.fromString(stage.getId()));
                                    workInfo.setStageExecutionID(stage.getExecutionId());
                                    workInfo.setStageName(null); // to be added to stage;
                                    workInfo.setStageTriggerReason(null); //to be added to stage
                                    workInfo.setShouldFetchMaterials(false); // to be added to stage
                                    workInfo.setJob(job);
                                    workInfo.setMaterials(pipeline.getMaterials()); // NOT SURE ABOUT THIS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                    workInfo.setEnvironmentVariables(pipeline.getEnvironmentVariables());
                                    result.setObject(workInfo);
                                }
                            }
                        }
                    }
                }
            }
        }else {
            result.setObject(isAssigned);
            result.setMessage("This agent has no job assigned.");
            return result;
        }
        return result;
    }
}
