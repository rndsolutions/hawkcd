/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hawkcd.scheduler;

import io.hawkcd.utilities.constants.LoggerMessages;
import io.hawkcd.model.Agent;
import io.hawkcd.model.Stage;
import io.hawkcd.model.enums.StageStatus;
import io.hawkcd.services.AgentService;
import io.hawkcd.services.PipelineService;
import io.hawkcd.services.interfaces.IAgentService;
import io.hawkcd.services.interfaces.IPipelineService;
import io.hawkcd.ws.EndpointConnector;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.hawkcd.model.Job;
import io.hawkcd.model.Pipeline;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.Task;
import io.hawkcd.model.enums.JobStatus;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.PipelineStatus;
import io.hawkcd.model.enums.TaskStatus;

public class StatusUpdaterService {
    private static final Logger LOGGER = Logger.getLogger(StatusUpdaterService.class.getName());
    private IAgentService agentService;
    private IPipelineService pipelineService;

    public StatusUpdaterService() {
        this.agentService = new AgentService();
        this.pipelineService = new PipelineService();
    }

    public StatusUpdaterService(IAgentService agentService, IPipelineService pipelineService) {
        this.agentService = agentService;
        this.pipelineService = pipelineService;
    }

    public void updateStatuses() {
        List<Agent> agents = (List<Agent>) this.agentService.getAll().getEntity();
        for (Agent agent : agents) {
            this.updateAgentStatus(agent);
        }

        List<Pipeline> pipelinesInProgress = (List<Pipeline>) this.pipelineService.getAllPreparedPipelinesInProgress().getEntity();
        for (Pipeline pipeline : pipelinesInProgress) {
            if (pipeline.shouldBeCanceled()) {
                this.cancelPipeline(pipeline);
                LOGGER.info(String.format(LoggerMessages.PIPELINE_CANCELED, pipeline.getExecutionId(), pipeline.getPipelineDefinitionName()));
                ServiceResult result = new ServiceResult(null, NotificationType.WARNING, "Pipeline " + pipeline.getPipelineDefinitionName() + " was successfully canceled");
                EndpointConnector.passResultToEndpoint("NotificationService", "sendMessage", result);
            } else if (pipeline.getStatus() == PipelineStatus.PAUSED) {
                this.pausePipeline(pipeline);
            } else {
                this.updateAllStatuses(pipeline);
            }

            this.pipelineService.update(pipeline);
        }
    }

    public Agent updateAgentStatus(Agent agent) {
        if (agent != null) {
            LocalDateTime lastReportedTime = agent.getLastReportedTime();
            LocalDateTime currentTime = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
            long timeBetweenReports = ChronoUnit.SECONDS.between(lastReportedTime, currentTime);

            if (agent.isConnected() && (timeBetweenReports > 12)) {
                agent.setConnected(false);
                this.agentService.update(agent);
            }
        }

        return agent;
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
            if (currentStage.getStatus() == StageStatus.PAUSED) {
                currentStage.setStatus(StageStatus.IN_PROGRESS);
                currentStage.setTriggeredManually(false);
            }

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
            stage.setEndTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
            stage.setStatus(StageStatus.FAILED);
            LOGGER.info(String.format("Stage %s set to %s", stage.getStageDefinitionName(), JobStatus.FAILED));
        } else if (this.areAllPassed(jobStatuses)) {
            stage.setEndTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
            stage.setStatus(StageStatus.PASSED);
            LOGGER.info(String.format("Stage %s set to %s", stage.getStageDefinitionName(), JobStatus.PASSED));
        }
    }

    public void updatePipelineStatus(Pipeline pipeline) {
        List<Stage> stages = pipeline.getStages();
        List<StageStatus> stageStatuses = new ArrayList<>();
        for (Stage stage : stages) {
            StageStatus stageStatus = stage.getStatus();
            stageStatuses.add(stageStatus);
            if (stage.isTriggeredManually() && (stage.getStatus() == StageStatus.IN_PROGRESS)) {
                pipeline.setStatus(PipelineStatus.PAUSED);
                stage.setStatus(StageStatus.PAUSED);
                String pipelinePaused = String.format("Pipeline %s set to %s", pipeline.getPipelineDefinitionName(), PipelineStatus.PAUSED);
                LOGGER.info(pipelinePaused);
                String stagePaused = String.format("Stage %s must be triggered manually", stage.getStageDefinitionName());
                LOGGER.info(stagePaused);
                String notificationMessage = pipelinePaused + System.lineSeparator() + stagePaused;
                ServiceResult notification = new ServiceResult(null, NotificationType.WARNING, notificationMessage);
                EndpointConnector.passResultToEndpoint("NotificationService", "sendMessage", notification);
            }
        }

        if (stageStatuses.contains(StageStatus.FAILED)) {
            pipeline.setStatus(PipelineStatus.FAILED);
            pipeline.setEndTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
            LOGGER.info(String.format("Pipeline %s set to %s", pipeline.getPipelineDefinitionName(), PipelineStatus.FAILED));
        } else if (this.areAllPassed(stageStatuses)) {
            pipeline.setStatus(PipelineStatus.PASSED);
            pipeline.setEndTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
            LOGGER.info(String.format("Pipeline %s set to %s", pipeline.getPipelineDefinitionName(), PipelineStatus.PASSED));
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
        pipeline.setStatus(PipelineStatus.CANCELED);
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

    private void pausePipeline(Pipeline pipeline) {
        List<Stage> stages = pipeline.getStages();
        for (Stage stage : stages) {
            if (stage.getStatus() == StageStatus.IN_PROGRESS) {
                stage.setStatus(StageStatus.PAUSED);
                break;
            }
        }
    }
}
