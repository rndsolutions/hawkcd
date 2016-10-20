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

package net.hawkengine.services;

import net.hawkengine.db.DbRepositoryFactory;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.model.enums.StageStatus;
import net.hawkengine.model.payload.WorkInfo;
import net.hawkengine.services.interfaces.IAgentService;
import net.hawkengine.services.interfaces.IJobService;
import net.hawkengine.services.interfaces.IPipelineService;
import net.hawkengine.ws.EndpointConnector;

import java.util.List;
import java.util.stream.Collectors;

public class AgentService extends CrudService<Agent> implements IAgentService {
    private static final Class CLASS_TYPE = Agent.class;

    private IPipelineService pipelineService;
    private IJobService jobService;

    public AgentService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.pipelineService = new PipelineService();
        this.jobService = new JobService();
    }

    public AgentService(IDbRepository repository, IPipelineService pipelineService) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.pipelineService = pipelineService;
    }

    @Override
    public ServiceResult getById(String agentId) {
        return super.getById(agentId);
    }

    @Override
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    public ServiceResult add(Agent agent) {
        ServiceResult result = super.add(agent);
        EndpointConnector.passResultToEndpoint(AgentService.class.getSimpleName(), "add", result);
        return result;
    }

    @Override
    public ServiceResult update(Agent agent) {
        ServiceResult result = super.update(agent);
        EndpointConnector.passResultToEndpoint(AgentService.class.getSimpleName(), "update", result);
        return result;
    }

    @Override
    public ServiceResult delete(String agentId) {
        return super.delete(agentId);
    }

    @Override
    public ServiceResult getAllAssignableAgents() {
        List<Agent> agents = (List<Agent>) super.getAll().getObject();
        List<Agent> assignableAgents = agents
                .stream()
                .filter(a -> a.isConnected() && a.isEnabled() && !a.isRunning() && !a.isAssigned())
                .collect(Collectors.toList());

        ServiceResult result =
                super.createServiceResultArray(assignableAgents, NotificationType.SUCCESS, "retrieved successfully");

        return result;
    }

    public ServiceResult getWorkInfo(String agentId) {
        ServiceResult result = null;
        Agent agent = (Agent) this.getById(agentId).getObject();
        if (agent == null) {
            result = createResult(null, NotificationType.ERROR, "This agent has no job assigned.");
        } else if (agent.isAssigned()) {
            List<Pipeline> pipelines = (List<Pipeline>) this.pipelineService.getAllPreparedPipelinesInProgress().getObject();
            for (Pipeline pipeline : pipelines) {
                WorkInfo workInfo = new WorkInfo();
                Stage stageInProgress = pipeline.getStages()
                        .stream()
                        .filter(s -> s.getStatus() == StageStatus.IN_PROGRESS)
                        .findFirst()
                        .orElse(null);
                if (stageInProgress == null) {
                    continue;
                }

                Job scheduledJob = stageInProgress
                        .getJobs()
                        .stream()
                        .filter(j -> j.getStatus() == JobStatus.ASSIGNED)
                        .filter(j -> j.getAssignedAgentId().equals(agentId))
                        .findFirst()
                        .orElse(null);
                if (scheduledJob == null) {
                    continue;
                }

                workInfo.setPipelineDefinitionName(pipeline.getPipelineDefinitionName());
                workInfo.setPipelineExecutionID(pipeline.getExecutionId());
                workInfo.setStageDefinitionName(stageInProgress.getStageDefinitionName());
                workInfo.setStageExecutionID(stageInProgress.getExecutionId());
                workInfo.setJobDefinitionName(scheduledJob.getJobDefinitionName());
                scheduledJob.setStatus(JobStatus.RUNNING);
                workInfo.setJob(scheduledJob);
                this.jobService.update(scheduledJob);

                result = createResult(workInfo, NotificationType.SUCCESS, "WorkInfo retrieved successfully");
            }
        }

        if (result == null) {
            agent.setAssigned(false);
            this.update(agent);
            result = createResult(null, NotificationType.ERROR, "This agent has no job assigned.");
        }

        return result;
    }

    private ServiceResult createResult(Object object, NotificationType notificationType, String message) {
        ServiceResult result = new ServiceResult(object, notificationType, message);

        return result;
    }
}
