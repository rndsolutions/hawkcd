package net.hawkengine.core.pipelinescheduler;

import net.hawkengine.core.utilities.EndpointConnector;
import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.Agent;
import net.hawkengine.model.Job;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.Stage;
import net.hawkengine.model.enums.StageStatus;
import net.hawkengine.services.AgentService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IAgentService;
import net.hawkengine.services.interfaces.IPipelineService;

import org.apache.log4j.Logger;

import java.util.List;

public class JobAssigner implements Runnable {
    private IAgentService agentService;
    private IPipelineService pipelineService;
    private JobAssignerService jobAssignerService;
    private StatusUpdaterService statusUpdaterService;
    private static final Logger LOGGER = Logger.getLogger(PipelinePreparer.class.getName());

    public JobAssigner() {
        this.agentService = new AgentService();
        this.pipelineService = new PipelineService();
        this.jobAssignerService = new JobAssignerService();
        this.statusUpdaterService = new StatusUpdaterService();
    }

    @Override
    public void run() {
        LOGGER.info(String.format(LoggerMessages.WORKER_STARTED, this.getClass().getSimpleName()));
        try {
            while (true) {
                List<Agent> agents = (List<Agent>) this.agentService.getAllAssignableAgents().getObject();
                List<Pipeline> pipelines = (List<Pipeline>) this.pipelineService.getAllPreparedPipelinesInProgress().getObject();

                for (Pipeline pipeline : pipelines) {
                    for (Stage stage : pipeline.getStages()) {
                        this.statusUpdaterService.updateStatuses();
                        if (stage.getStatus() == StageStatus.IN_PROGRESS) {
                            for (Job job : stage.getJobs()) {
                                Agent agent = this.jobAssignerService.assignAgentToJob(job, agents);
                                ServiceResult result = this.agentService.update(agent);
                                EndpointConnector.passResultToEndpoint(this.getClass().getSimpleName(), "update", result);
                            }
                        }
                    }

                    ServiceResult result = this.pipelineService.update(pipeline);
                    EndpointConnector.passResultToEndpoint(this.getClass().getSimpleName(), "update", result);
                }

                Thread.sleep(4 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
