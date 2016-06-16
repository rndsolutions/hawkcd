package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.enums.Status;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class StatusUpdater extends Thread {
    private final Logger logger = Logger.getLogger(this.getClass());
    private IPipelineService pipelineService;

    public StatusUpdater(){
        this.pipelineService = new PipelineService();
    }

    @Override
    public synchronized void start() {
        super.start();
        this.logger.info(String.format(LoggerMessages.WORKER_STARTED, "Status Updater"));
        this.run();
    }

    @Override
    public void run() {
        try {
            while (true) {

                List<Pipeline> pipelinesInProgress = this.getAllPipelinesInProgress();

                int a = 5;

                Thread.sleep(4 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.run();
    }

    List<Pipeline> getAllPipelinesInProgress(){
        List<Pipeline> allPipelines = (List<Pipeline>) this.pipelineService
                .getAll()
                .getObject();

        List<Pipeline> pipelinesInProgress = allPipelines
                .stream()
                .filter(p -> p.getStatus() == Status.IN_PROGRESS)
                .collect(Collectors.toList());

        return pipelinesInProgress;
    }
}
