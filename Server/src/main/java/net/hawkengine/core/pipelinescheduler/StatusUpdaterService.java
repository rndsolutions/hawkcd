package net.hawkengine.core.pipelinescheduler;

import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.*;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineService;
import net.hawkengine.ws.EndpointConnector;
import org.apache.log4j.Logger;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class StatusUpdaterService {
    private static final Logger LOGGER = Logger.getLogger(StatusUpdaterService.class.getName());
    private IPipelineService pipelineService;

    public StatusUpdaterService() {
        this.pipelineService = new PipelineService();
    }

    public StatusUpdaterService(IPipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    public void updateStatuses() {
        List<Pipeline> pipelinesInProgress = (List<Pipeline>) this.pipelineService.getAllPreparedPipelinesInProgress().getObject();
        for (Pipeline pipeline : pipelinesInProgress) {
            if (pipeline.shouldBeCanceled()) {
                this.cancelPipeline(pipeline);
                LOGGER.info(String.format(LoggerMessages.PIPELINE_CANCELED, pipeline.getExecutionId(), pipeline.getPipelineDefinitionName()));
                ServiceResult result = new ServiceResult(null, NotificationType.WARNING, "Pipeline " + pipeline.getPipelineDefinitionName() + " was sucessfully canceled");
                EndpointConnector.passResultToEndpoint("NotificationService", "sendMessage", result);
            } else {
                this.updateAllStatuses(pipeline);
            }

            this.pipelineService.update(pipeline);
        }
    }

    public boolean updateAllStatuses(Object node) {
        Pipeline pipelineToUpdate = null;
        Queue<Object> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            Object queueNode = queue.poll();
            if (queueNode.getClass() == Job.class) {
                pipelineToUpdate = (Pipeline) node;
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
            if (currentStage.getStatus() == StageStatus.IN_PROGRESS) {
                this.updateStageStatus(currentStage);
                if (currentStage.getStatus() == StageStatus.PASSED) {
                    continue;
                } else {
                    break;
                }
            } else if ((currentStage.getStatus() == StageStatus.NOT_RUN)) {
                currentStage.setStartTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
                currentStage.setStatus(StageStatus.IN_PROGRESS);
                break;
            } else if (currentStage.getStatus() == StageStatus.PASSED || currentStage.getStatus() == StageStatus.UNSCHEDULED) {
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
            stage.setEndTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
            stage.setStatus(StageStatus.FAILED);
            LOGGER.info(String.format("Stage %s set to %s", stage.getStageDefinitionName(), StageStatus.FAILED));
        } else if (this.areAllPassed(jobStatuses)) {
            stage.setEndTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
            stage.setStatus(StageStatus.PASSED);
            LOGGER.info(String.format("Stage %s set to %s", stage.getStageDefinitionName(), StageStatus.PASSED));
        }
    }

    public void updatePipelineStatus(Pipeline pipeline) {
        List<Stage> stages = pipeline.getStages();
        List<StageStatus> stageStatuses = new ArrayList<>();

        for (Stage stage : stages) {
            StageStatus stageStatus = stage.getStatus();
            stageStatuses.add(stageStatus);

            if (stage.isTriggeredManually() && (stage.getStatus() == StageStatus.IN_PROGRESS)) {
                stage.setStatus(StageStatus.NOT_RUN);
                pipeline.setStatus(Status.AWAITING);
                LOGGER.info(String.format("Pipeline %s set to %s", pipeline.getPipelineDefinitionName(), Status.AWAITING));
                String message = String.format("Stage %s is set to be manually triggered", stage.getStageDefinitionName());
                LOGGER.info(message);
                ServiceResult notification = new ServiceResult(null, NotificationType.WARNING, message);
                EndpointConnector.passResultToEndpoint("NotificationService", "sendMessage", notification);
            }
        }

        if (stageStatuses.contains(StageStatus.FAILED)) {
            if (pipeline.isStageRun()) {
                pipeline.setStageRun(false);
            } else {
                pipeline.setStatus(Status.FAILED);
                pipeline.setEndTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
                LOGGER.info(String.format("Pipeline %s set to %s", pipeline.getPipelineDefinitionName(), Status.FAILED));
            }
        } else if (this.areAllPassed(stageStatuses)) {
            if (pipeline.isStageRun()) {
                pipeline.setStageRun(false);
            } else {
                pipeline.setStatus(Status.PASSED);
                pipeline.setEndTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
                LOGGER.info(String.format("Pipeline %s set to %s", pipeline.getPipelineDefinitionName(), Status.PASSED));
            }
        }
    }

    public boolean areAllPassed(List<?> statuses) {
        String[] statusesAsString = new String[statuses.size()];
        int index = 0;

        if (statuses.isEmpty()) {
            return false;
        }

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

    private void cancelPipeline(Pipeline pipeline) {
        pipeline.setShouldBeCanceled(false);
        pipeline.setStatus(Status.CANCELED);
        pipeline.setEndTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        for (Stage stage : pipeline.getStages()) {
            if (stage.getStatus() == StageStatus.IN_PROGRESS || stage.getStatus() == StageStatus.AWAITING) {
                stage.setStatus(StageStatus.CANCELED);
                for (Job job : stage.getJobs()) {
                    job.setStatus(JobStatus.CANCELED);
                    for (Task task : job.getTasks()) {
                        task.setStatus(TaskStatus.CANCELED);
                    }
                }
            }
        }
    }

    private List<Stage> getLastRunsOfStages(List<Stage> stages) {
        int lastExecutionId = 0;
        for (Stage stage : stages) {
            if (stage.getExecutionId() > lastExecutionId) {
                lastExecutionId = stage.getExecutionId();
            }
        }

        List<Stage> lastRuns = new ArrayList<>();
        for (Stage stage : stages) {
            if (stage.getExecutionId() == lastExecutionId) {
                lastRuns.add(stage);
            }
        }

        return lastRuns;
    }
}
