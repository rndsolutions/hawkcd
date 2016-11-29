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

import io.hawkcd.model.*;
import io.hawkcd.model.enums.JobStatus;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.PipelineStatus;
import io.hawkcd.model.enums.StageStatus;
import io.hawkcd.services.AgentService;
import io.hawkcd.services.JobService;
import io.hawkcd.services.PipelineService;
import io.hawkcd.services.interfaces.IAgentService;
import io.hawkcd.services.interfaces.IJobService;
import io.hawkcd.services.interfaces.IPipelineService;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class JobAssignerService {
    private static final Logger LOGGER = Logger.getLogger(JobAssignerService.class.getName());

    private IAgentService agentService;
    private IPipelineService pipelineService;
    private IJobService jobService;
    private JobAssignerUtilities jobAssignerUtilities;

    public JobAssignerService() {
        this.agentService = new AgentService();
        this.pipelineService = new PipelineService();
        this.jobService = new JobService();
        this.jobAssignerUtilities = new JobAssignerUtilities();
    }

    public void checkUnassignedJobs(List<Agent> agents) {
        List<Agent> filteredAgents = agents.stream().filter(a -> a.isConnected() && a.isEnabled()).collect(Collectors.toList());
        List<Pipeline> pipelinesInProgress = (List<Pipeline>) this.pipelineService.getAllPreparedPipelinesInProgress().getEntity();
        for (Pipeline pipeline : pipelinesInProgress) {
            boolean isSetToAwaiting = false;
            Stage stageInProgress = pipeline.getStages().stream().filter(s -> (s.getStatus() == StageStatus.IN_PROGRESS) && !s.isTriggeredManually()).findFirst().orElse(null);
            if (stageInProgress == null) {
                continue;
            }

            for (Job job : stageInProgress.getJobs()) {
                if (job.getStatus() == JobStatus.UNASSIGNED) {
                    boolean hasAssignableAgent = this.jobAssignerUtilities.hasAssignableAgent(job, filteredAgents);
                    if (!hasAssignableAgent) {
                        job.setStatus(JobStatus.AWAITING);
                        isSetToAwaiting = true;
                        LOGGER.info(String.format("Job %s has no assignable Agents.", job.getJobDefinitionName()));
                    }
                }
            }

            if (isSetToAwaiting) {
                stageInProgress.setStatus(StageStatus.AWAITING);
                pipeline.setStatus(PipelineStatus.AWAITING);
                this.pipelineService.update(pipeline);
                String message = String.format("Pipeline %s set to AWAITING.", pipeline.getPipelineDefinitionName());
                LOGGER.info(message);
                ServiceResult notification = new ServiceResult(null, NotificationType.WARNING, message);
            }
        }
    }

    public void checkAwaitingJobs(List<Agent> agents) {
        List<Agent> filteredAgents = agents.stream().filter(a -> a.isConnected() && a.isEnabled()).collect(Collectors.toList());
        List<Pipeline> awaitingPipelines = (List<Pipeline>) this.pipelineService.getAllPreparedAwaitingPipelines().getEntity();
        for (Pipeline pipeline : awaitingPipelines) {
            Stage awaitingStage = pipeline.getStages().stream().filter(s -> s.getStatus() == StageStatus.AWAITING).findFirst().orElse(null);
            if (awaitingStage == null) {
                continue;
            }

            for (Job job : awaitingStage.getJobs()) {
                if (job.getStatus() == JobStatus.AWAITING) {
                    boolean hasAssignableAgent = this.jobAssignerUtilities.hasAssignableAgent(job, filteredAgents);
                    if (hasAssignableAgent) {
                        job.setStatus(JobStatus.UNASSIGNED);
                        LOGGER.info(String.format("Job %s set back to IN_PROGRESS.", job.getJobDefinitionName()));
                    }
                }
            }

            boolean hasAwaitingJobs = awaitingStage.getJobs().stream().anyMatch(j -> j.getStatus() == JobStatus.AWAITING);
            if (!hasAwaitingJobs) {
                awaitingStage.setStatus(StageStatus.IN_PROGRESS);
                pipeline.setStatus(PipelineStatus.IN_PROGRESS);
                this.pipelineService.update(pipeline);
                LOGGER.info(String.format("Pipeline %s set back to IN_PROGRESS.", pipeline.getPipelineDefinitionName()));
            }
        }
    }

    public void assignJobs(List<Agent> agents) {
        List<Agent> filteredAgents = agents.stream().filter(a -> a.isConnected() && a.isEnabled() && !a.isRunning()).collect(Collectors.toList());
        List<Pipeline> pipelines = (List<Pipeline>) this.pipelineService.getAllPreparedPipelinesInProgress().getEntity();
        for (Pipeline pipeline : pipelines) {
            for (Stage stage : pipeline.getStages()) {
                if ((stage.getStatus() == StageStatus.IN_PROGRESS) && !stage.isTriggeredManually()) {
                    for (Job job : stage.getJobs()) {
                        if (filteredAgents.size() != 0) {
                            Agent agent = this.jobAssignerUtilities.assignAgentToJob(job, filteredAgents);
                            if (agent != null) {
                                this.jobService.update(job);
                                ServiceResult result = this.agentService.update(agent);
                            }
                        }
                    }
                }
            }
//            EndpointConnector.passResultToEndpoint(PipelineService.class.getSimpleName(), "update", result);
        }
    }
}
