package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.Agent;
import net.hawkengine.model.Job;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.Stage;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.services.AgentService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IAgentService;
import net.hawkengine.services.interfaces.IPipelineService;

import org.apache.log4j.Logger;

import java.util.List;

public class JobAssigner implements Runnable {
    private static final Logger logger = Logger.getLogger(PipelinePreparer.class.getName());
    private IAgentService agentService;
    private IPipelineService pipelineService;

    public JobAssigner() {
        this.agentService = new AgentService();
        this.pipelineService = new PipelineService();
    }

    public JobAssigner(IAgentService agentService, IPipelineService pipelineService) {
        this.agentService = agentService;
        this.pipelineService = pipelineService;
    }

    @Override
    public void run() {
        logger.info(String.format(LoggerMessages.WORKER_STARTED, "Job Assigner"));
        try {
            while (true) {
                // TODO: call getAllWorkingIdleAgents
                List<Agent> agents = (List<Agent>) this.agentService.getAll().getObject();
                List<Pipeline> pipelines =
                        (List<Pipeline>) this.pipelineService.getAllPreparedPipelines().getObject();

                // TODO: Replace iterations with call to JobService
                for (Pipeline pipeline : pipelines) {
                    for (Stage stage : pipeline.getStages()) {
                        for (Job job : stage.getJobs()) {
                            if (job.getStatus() == JobStatus.AWAITING) {
                                // TODO: Assign Agent to Job method
                            } else if (job.getStatus() == JobStatus.SCHEDULED) {
                                // TODO: Check if Agent is still eligible method
                                if (!true) {
                                    // TODO: Assign Agent to Job method
                                }
                            }
                        }
                    }
                }

                Thread.sleep(4 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
