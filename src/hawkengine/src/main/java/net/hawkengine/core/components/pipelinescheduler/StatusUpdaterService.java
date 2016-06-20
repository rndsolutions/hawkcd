package net.hawkengine.core.components.pipelinescheduler;

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
import java.util.Stack;
import java.util.stream.Collectors;

public class StatusUpdaterService extends Thread {
    private IPipelineService pipelineService;
    private static final Logger logger = Logger.getLogger(StatusUpdaterService.class.getName());

    public StatusUpdaterService() {
        this.pipelineService = new PipelineService();
    }

    public StatusUpdaterService(IPipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    public void updateStatuses() {
        List<Pipeline> pipelinesInProgress = this.getAllPipelinesInProgress();
        for (Pipeline pipeline : pipelinesInProgress) {
            this.updateAllStatuses(pipeline);
            this.pipelineService.update(pipeline);
        }
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

    public boolean updateAllStatuses(Object pipeline) {
        Stack stack = new Stack();
        stack.push(pipeline);

        while (!stack.isEmpty()) {
            Object node = stack.pop();
            if (node.getClass() == Job.class) {
                Pipeline pipelineToUpdate = (Pipeline) pipeline;
                this.updatePipelineStatus(pipelineToUpdate);
                return true;
            }

            if (node.getClass() == Pipeline.class) {
                Pipeline pipelineNode = (Pipeline) node;
                stack.addAll(pipelineNode.getStages());
            } else {
                Stage stageNode = (Stage) node;
                stack.addAll(stageNode.getJobs());
                this.updateStageStatus(stageNode);
            }
        }

        return false;
    }

    public void updateStageStatus(Stage stage) {
        List<JobStatus> jobStatuses = new ArrayList<>();
        List<Job> jobs = stage.getJobs();

        for (Job job : jobs) {
            JobStatus jobStatus = job.getStatus();
            jobStatuses.add(jobStatus);
        }

        if (jobStatuses.contains(JobStatus.FAILED)) {
            stage.setStatus(Status.FAILED);
        } else if (this.areAllPassed(jobStatuses)) {
            stage.setStatus(Status.PASSED);
        }
    }

    public void updatePipelineStatus(Pipeline pipeline) {
        List<Stage> stages = pipeline.getStages();
        List<Status> stageStatuses = new ArrayList<>();

        for (Stage stage : stages) {
            Status stageStatus = stage.getStatus();
            stageStatuses.add(stageStatus);
        }

        if (stageStatuses.contains(Status.FAILED)) {
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
