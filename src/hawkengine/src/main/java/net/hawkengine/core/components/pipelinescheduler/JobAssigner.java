package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.Agent;
import net.hawkengine.model.Job;
import net.hawkengine.model.Pipeline;
import net.hawkengine.services.AgentService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IAgentService;
import net.hawkengine.services.interfaces.IPipelineService;

import org.apache.log4j.Logger;

import java.util.List;

public class JobAssigner extends Thread {
    private IAgentService agentService;
    private IPipelineService pipelineService;
    private static final Logger logger = Logger.getLogger(PipelinePreparer.class.getName());

    public JobAssigner() {
        this.agentService = new AgentService();
        this.pipelineService = new PipelineService();
    }

    public JobAssigner(IAgentService agentService, IPipelineService pipelineService) {
        this.agentService = agentService;
        this.pipelineService = pipelineService;
    }

    @Override
    public synchronized void start() {
        super.start();
        logger.info(String.format(LoggerMessages.WORKER_STARTED, "Job Assigner"));
        this.run();
    }

    @Override
    public void run() {
        try {
            while (true) {
                List<Agent> agents = (List<Agent>) this.agentService.getAll().getObject();
                // TODO: Filter agents or write method

                List<Pipeline> pipelines = 
                        (List<Pipeline>) this.pipelineService.getAllPreparedPipelines().getObject();

                for (Pipeline pipeline : pipelines) {
                    for (Pipeline pipeline1 : pipeline.getJ) {
                        
                    }
                }



                Thread.sleep(4 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
