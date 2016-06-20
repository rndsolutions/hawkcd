package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.core.utilities.constants.LoggerMessages;
import org.apache.log4j.Logger;

public class JobAssigner implements Runnable {
    private JobAssignerService jobAssignerService;
    private StatusUpdaterService statusUpdaterService;
    private static final Logger logger = Logger.getLogger(PipelinePreparer.class.getName());

    public JobAssigner() {
        this.jobAssignerService = new JobAssignerService();
        this.statusUpdaterService = new StatusUpdaterService();
    }

    @Override
    public void run() {
        logger.info(String.format(LoggerMessages.WORKER_STARTED, "Job Assigner"));
        try {
            while (true) {
                this.jobAssignerService.assignJobs();
                this.statusUpdaterService.updateStatuses();
                Thread.sleep(4 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
