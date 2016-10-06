package net.hawkengine.core.pipelinescheduler;

import net.hawkengine.model.*;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.model.enums.StageStatus;
import net.hawkengine.model.enums.PipelineStatus;
import net.hawkengine.services.AgentService;
import net.hawkengine.services.JobService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IAgentService;
import net.hawkengine.services.interfaces.IJobService;
import net.hawkengine.services.interfaces.IPipelineService;
import net.hawkengine.ws.EndpointConnector;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class JobAssignerService {
    private static final Logger LOGGER = Logger.getLogger(JobAssignerService.class.getName());

    private IAgentService agentService;
    private IPipelineService pipelineService;
    private IJobService jobService;
    private JobAssignerUtilities jobAssignerUtilities;

    public JobAssignerService() {
        this.agentService = new AgentService();
        this.pipelineService = new PipelineService();
        this.jobService = new JobService();
        this.jobAssignerUtilities = new JobAssignerUtilities();
    }

    public void checkUnassignedJobs(List<Agent> agents) {
        List<Agent> filteredAgents = agents.stream().filter(a -> a.isConnected() && a.isEnabled()).collect(Collectors.toList());
        List<Pipeline> pipelinesInProgress = (List<Pipeline>) this.pipelineService.getAllPreparedPipelinesInProgress().getObject();
        for (Pipeline pipeline : pipelinesInProgress) {
            boolean isSetToAwaiting = false;
            Stage stageInProgress = pipeline.getStages().stream().filter(s -> (s.getStatus() == StageStatus.IN_PROGRESS) && !s.isTriggeredManually()).findFirst().orElse(null);
            if (stageInProgress == null) {
                continue;
            }

            for (Job job : stageInProgress.getJobs()) {
                if (job.getStatus() == JobStatus.UNASSIGNED) {
                    boolean hasAssignableAgent = this.jobAssignerUtilities.hasAssignableAgent(job, filteredAgents);
                    if (!hasAssignableAgent) {
                        job.setStatus(JobStatus.AWAITING);
                        isSetToAwaiting = true;
                        LOGGER.info(String.format("Job %s has no assignable Agents.", job.getJobDefinitionName()));
                    }
                }
            }

            if (isSetToAwaiting) {
                stageInProgress.setStatus(StageStatus.AWAITING);
                pipeline.setStatus(PipelineStatus.AWAITING);
                this.pipelineService.update(pipeline);
                String message = String.format("Pipeline %s set to AWAITING.", pipeline.getPipelineDefinitionName());
                LOGGER.info(message);
                ServiceResult notification = new ServiceResult(null, NotificationType.WARNING, message);
                EndpointConnector.passResultToEndpoint("NotificationService", "sendMessage", notification);
            }
        }
    }

    public void checkAwaitingJobs(List<Agent> agents) {
        List<Agent> filteredAgents = agents.stream().filter(a -> a.isConnected() && a.isEnabled()).collect(Collectors.toList());
        List<Pipeline> awaitingPipelines = (List<Pipeline>) this.pipelineService.getAllPreparedAwaitingPipelines().getObject();
        for (Pipeline pipeline : awaitingPipelines) {
            Stage awaitingStage = pipeline.getStages().stream().filter(s -> s.getStatus() == StageStatus.AWAITING).findFirst().orElse(null);
            if (awaitingStage == null) {
                continue;
            }

            for (Job job : awaitingStage.getJobs()) {
                if (job.getStatus() == JobStatus.AWAITING) {
                    boolean hasAssignableAgent = this.jobAssignerUtilities.hasAssignableAgent(job, filteredAgents);
                    if (hasAssignableAgent) {
                        job.setStatus(JobStatus.UNASSIGNED);
                        LOGGER.info(String.format("Job %s set back to IN_PROGRESS.", job.getJobDefinitionName()));
                    }
                }
            }

            boolean hasAwaitingJobs = awaitingStage.getJobs().stream().anyMatch(j -> j.getStatus() == JobStatus.AWAITING);
            if (!hasAwaitingJobs) {
                awaitingStage.setStatus(StageStatus.IN_PROGRESS);
                pipeline.setStatus(PipelineStatus.IN_PROGRESS);
                this.pipelineService.update(pipeline);
                LOGGER.info(String.format("Pipeline %s set back to IN_PROGRESS.", pipeline.getPipelineDefinitionName()));
            }
        }
    }

    public void assignJobs(List<Agent> agents) {
        List<Agent> filteredAgents = agents.stream().filter(a -> a.isConnected() && a.isEnabled() && !a.isRunning()).collect(Collectors.toList());
        List<Pipeline> pipelines = (List<Pipeline>) this.pipelineService.getAllPreparedPipelinesInProgress().getObject();
        for (Pipeline pipeline : pipelines) {
            for (Stage stage : pipeline.getStages()) {
                if ((stage.getStatus() == StageStatus.IN_PROGRESS) && !stage.isTriggeredManually()) {
                    for (Job job : stage.getJobs()) {
                        if (filteredAgents.size() != 0) {
                            Agent agent = this.jobAssignerUtilities.assignAgentToJob(job, filteredAgents);
                            if (agent != null) {
                                this.jobService.update(job);
                                ServiceResult result = this.agentService.update(agent);
                                EndpointConnector.passResultToEndpoint(AgentService.class.getSimpleName(), "update", result);
                            }
                        }
                    }
                }
            }
//            EndpointConnector.passResultToEndpoint(PipelineService.class.getSimpleName(), "update", result);
        }
    }
}
