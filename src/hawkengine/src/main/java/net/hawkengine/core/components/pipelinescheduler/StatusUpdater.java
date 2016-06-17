package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.Job;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.Stage;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.model.enums.Status;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatusUpdater extends Thread {
    private final Logger logger = Logger.getLogger(this.getClass());
    private IPipelineService pipelineService;

    public StatusUpdater() {
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
                this.getStageForUpdate(pipelinesInProgress);
                int a = 5;

                Thread.sleep(4 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.run();
    }

    public List<Pipeline> getAllPipelinesInProgress() {
        List<Pipeline> allPipelines = (List<Pipeline>) this.pipelineService
                .getAll()
                .getObject();

        List<Pipeline> pipelinesInProgress = allPipelines
                .stream()
                .filter(p -> p.getStatus() == Status.IN_PROGRESS)
                .collect(Collectors.toList());

        return pipelinesInProgress;
    }

    public void updateStageStatus(Stage stage){
        List<JobStatus> jobStatuses = new ArrayList<>();
        List<Job> jobs = stage.getJobs();
        for (Job job: jobs) {
            JobStatus jobStatus = job.getStatus();
            jobStatuses.add(jobStatus);
        }
        if (jobStatuses.contains(JobStatus.FAILED)){
            stage.setStatus(Status.FAILED);
        }
        else if (this.areAllPassed(jobStatuses)){
            stage.setStatus(Status.PASSED);
        }
        int a = 5;
    }

    public void getStageForUpdate(List<Pipeline> pipelines){
        for (Pipeline pipeline: pipelines) {
            List<Stage> stages = pipeline.getStages();

            for (Stage stage: stages) {
                updateStageStatus(stage);
            }
        }
    }
    public boolean areAllPassed(List<JobStatus> jobStatuses){
        boolean areAllPassedStatus = false;
        for (JobStatus jobStatus: jobStatuses) {
            if (jobStatus == JobStatus.PASSED){
                areAllPassedStatus = true;
            }
            else{
                areAllPassedStatus = false;
                return areAllPassedStatus;
            }
        }
        return areAllPassedStatus;
    }
}
