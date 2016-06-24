package net.hawkengine.core.components.pipelinescheduler;

import net.hawkengine.model.Job;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.Stage;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.model.enums.StageStatus;
import net.hawkengine.model.enums.Status;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class StatusUpdaterService extends Thread {
    private static final Logger logger = Logger.getLogger(StatusUpdaterService.class.getName());
    private IPipelineService pipelineService;

    public StatusUpdaterService() {
        this.pipelineService = new PipelineService();
    }

    public StatusUpdaterService(IPipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    public void updateStatuses() {
        List<Pipeline> pipelinesInProgress = (List<Pipeline>) this.pipelineService.getAllPipelinesInProgress().getObject();
        for (Pipeline pipeline : pipelinesInProgress) {
            this.updateAllStatuses(pipeline);
            this.pipelineService.update(pipeline);
        }
    }

    public boolean updateAllStatuses(Pipeline pipeline) {
        Pipeline pipelineToUpdate = null;
        Queue<Object> queue = new LinkedList<>();
        queue.add(pipeline);

        while (!queue.isEmpty()) {
            Object queueNode = queue.poll();
            if (queueNode.getClass() == Job.class) {
                pipelineToUpdate = pipeline;
                this.updatePipelineStatus(pipelineToUpdate);
                return true;
            }

            if (queueNode.getClass() == Pipeline.class) {
                pipelineToUpdate = (Pipeline) queueNode;
                queue.addAll(pipelineToUpdate.getStages());
                this.updateStageStatusesInSequence(pipelineToUpdate.getStages());
            } else {
                Stage stageNode = (Stage) queueNode;
                queue.addAll(stageNode.getJobs());
            }
        }

        return false;
    }

    public void updateStageStatusesInSequence(List<Stage> stages) {
        for (Stage currentStage : stages) {
            this.updateStageStatus(currentStage);
            if (currentStage.getStatus() == StageStatus.NOT_RUN) {
                currentStage.setStatus(StageStatus.IN_PROGRESS);
                break;
            } else if (currentStage.getStatus() == StageStatus.PASSED) {
                continue;
            } else {
                break;
            }
        }
    }

    public void updateStageStatus(Stage stage) {
        List<JobStatus> jobStatuses = new ArrayList<>();
        List<Job> jobs = stage.getJobs();

        for (Job job : jobs) {
            JobStatus jobStatus = job.getStatus();
            jobStatuses.add(jobStatus);
        }

        if (jobStatuses.contains(JobStatus.FAILED)) {
            stage.setStatus(StageStatus.FAILED);
        } else if (this.areAllPassed(jobStatuses)) {
            stage.setStatus(StageStatus.PASSED);
        }
    }

    public void updatePipelineStatus(Pipeline pipeline) {
        List<Stage> stages = pipeline.getStages();
        List<StageStatus> stageStatuses = new ArrayList<>();

        for (Stage stage : stages) {
            StageStatus stageStatus = stage.getStatus();
            stageStatuses.add(stageStatus);
        }

        if (stageStatuses.contains(StageStatus.FAILED)) {
            pipeline.setStatus(Status.FAILED);
        } else if (this.areAllPassed(stageStatuses)) {
            pipeline.setStatus(Status.PASSED);
        }
    }

    public boolean areAllPassed(List<?> statuses) {
        String[] statusesAsString = new String[statuses.size()];
        int index = 0;

        for (Object status : statuses) {
            statusesAsString[index] = status.toString();
            index++;
        }

        for (String aStatusesAsString : statusesAsString) {
            if (!aStatusesAsString.equals("PASSED")) {
                return false;
            }
        }

        return true;
    }
}
