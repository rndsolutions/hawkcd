package net.hawkengine.core.pipelinescheduler;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.Agent;
import net.hawkengine.services.AgentService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IAgentService;
import org.apache.log4j.Logger;

import java.util.List;

public class JobAssigner implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(PipelinePreparer.class.getName());
    private static final int POLL_INTERVAL = ServerConfiguration.getConfiguration().getPipelineSchedulerPollInterval() * 1000;

    private JobAssignerService jobAssignerService;
    private StatusUpdaterService statusUpdaterService;
    private IAgentService agentService;

    public JobAssigner() {
        this.jobAssignerService = new JobAssignerService();
        this.statusUpdaterService = new StatusUpdaterService();
        this.agentService = new AgentService();
    }

    @Override
    public void run() {
        LOGGER.info(String.format(LoggerMessages.WORKER_STARTED, this.getClass().getSimpleName()));
        try {
            while (true) {
                List<Agent> agents = (List<Agent>) this.agentService.getAll().getObject();

                PipelineService.lock.lock();
                this.statusUpdaterService.updateStatuses();
                this.jobAssignerService.checkUnassignedJobs(agents);
                this.jobAssignerService.checkAwaitingJobs(agents);
                this.jobAssignerService.assignJobs(agents);
                PipelineService.lock.unlock();

                Thread.sleep(POLL_INTERVAL);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
