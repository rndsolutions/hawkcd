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

package io.hawkcd.services;

import io.hawkcd.core.Message;
import io.hawkcd.core.MessageConverter;
import io.hawkcd.core.security.Authorization;
import io.hawkcd.core.security.AuthorizationFactory;
import io.hawkcd.core.security.AuthorizationManager;
import io.hawkcd.core.session.SessionFactory;
import io.hawkcd.core.session.SessionManager;
import io.hawkcd.db.DbRepositoryFactory;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.model.EnvironmentVariable;
import io.hawkcd.model.FetchMaterialTask;
import io.hawkcd.model.Job;
import io.hawkcd.model.JobDefinition;
import io.hawkcd.model.Material;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.Pipeline;
import io.hawkcd.model.PipelineDefinition;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.SessionDetails;
import io.hawkcd.model.Stage;
import io.hawkcd.model.StageDefinition;
import io.hawkcd.model.User;
import io.hawkcd.model.dto.PipelineDto;
import io.hawkcd.model.enums.*;
import io.hawkcd.services.interfaces.IMaterialDefinitionService;
import io.hawkcd.services.interfaces.IPipelineDefinitionService;
import io.hawkcd.services.interfaces.IPipelineService;
import io.hawkcd.ws.EndpointConnector;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import io.hawkcd.model.Task;
import io.hawkcd.model.TaskDefinition;


/*
* The PipelineService @class gives access to all Pipeline runs in the system
*/
public class PipelineService extends CrudService<Pipeline> implements IPipelineService {
    private static final Class CLASS_TYPE = Pipeline.class;
    private static final Logger LOGGER = Logger.getLogger(PipelineService.class.getName());

    public static Lock lock = new ReentrantLock();

    private IPipelineDefinitionService pipelineDefinitionService;
    private IMaterialDefinitionService materialDefinitionService;

    public PipelineService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.materialDefinitionService = new MaterialDefinitionService();
    }

    public PipelineService(IDbRepository repository, IPipelineDefinitionService pipelineDefinitionService, IMaterialDefinitionService materialDefinitionService) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.pipelineDefinitionService = pipelineDefinitionService;
        this.materialDefinitionService = materialDefinitionService;
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getById(String pipelineId) {
        return super.getById(pipelineId);
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.OPERATOR )
    public ServiceResult add(Pipeline pipeline) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getEntity();
        pipeline.setPipelineDefinitionName(pipelineDefinition.getName());
        List<Pipeline> pipelines = (List<Pipeline>) this.getAll().getEntity();
        Pipeline lastPipeline = pipelines
                .stream()
                .filter(p -> p.getPipelineDefinitionId().equals(pipeline.getPipelineDefinitionId()))
                .sorted((p1, p2) -> Integer.compare(p2.getExecutionId(), p1.getExecutionId()))
                .findFirst()
                .orElse(null);
        if (lastPipeline == null) {
            pipeline.setExecutionId(1);
        } else {
            pipeline.setExecutionId(lastPipeline.getExecutionId() + 1);
            pipelineDefinition.setRevisionCount(pipelineDefinition.getRevisionCount() + 1);
            List<EnvironmentVariable> environmentVariables = pipelineDefinition.getEnvironmentVariables();
            EnvironmentVariable environmentVariable = environmentVariables.stream().filter(e -> e.getKey().equals("COUNT")).findFirst().orElse(null);

            int envAutoIncrement = Integer.parseInt(environmentVariable.getValue()) + 1;

            environmentVariable.setValue(String.valueOf(envAutoIncrement));
            environmentVariables.stream().filter(env -> env.getKey().equals(environmentVariable.getKey())).forEach(env -> {
                env.setValue(environmentVariable.getValue());
            });
            pipelineDefinition.setEnvironmentVariables(environmentVariables);
            ServiceResult result = this.pipelineDefinitionService.update(pipelineDefinition);
            EndpointConnector.passResultToEndpoint("PipelineDefinitionService", "update", result);
        }

        this.addMaterialsToPipeline(pipeline);
        this.addStagesToPipeline(pipeline);

        return super.add(pipeline);
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.ADMIN )
    public ServiceResult update(Pipeline pipeline) {
        ServiceResult result = super.update(pipeline);

        Message message = AuthorizationFactory.getAuthorizationManager().getAllUsersWithPermissionsMap(result);
        


        //MessageConverter.
        //Create Message
        //Extracts users from all active sessions
        //Build the user permission Map and fill in the Message class
        //if(isSingleNode){ publish } {else { sendToAllAuthorizedSEssions}}




        //AuthorizationFactory.getAuthorizationManager().

        //EndpointConnector.passResultToEndpoint(this.getClass().getSimpleName(), "update", result);

        return result;
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.ADMIN )
    public ServiceResult delete(Pipeline pipeline) {
        return super.delete(pipeline);
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAllByDefinitionId(String pipelineDefinitionId) {
        ServiceResult result = this.getAll();
        List<Pipeline> pipelines = (List<Pipeline>) result.getEntity();

        List<Pipeline> filteredPipelines = pipelines
                .stream()
                .filter(p -> p.getPipelineDefinitionId().equals(pipelineDefinitionId))
                .collect(Collectors.toList());

        result.setEntity(filteredPipelines);

        return result;
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAllNonupdatedPipelines() {
        ServiceResult result = this.getAll();
        List<Pipeline> pipelines = (List<Pipeline>) result.getEntity();

        List<Pipeline> updatedPipelines = pipelines
                .stream()
                .filter(p -> !p.areMaterialsUpdated())
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        result.setEntity(updatedPipelines);

        return result;
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAllUpdatedUnpreparedPipelinesInProgress() {
        ServiceResult result = this.getAll();
        List<Pipeline> pipelines = (List<Pipeline>) result.getEntity();

        List<Pipeline> updatedPipelines = pipelines
                .stream()
                .filter(p -> p.areMaterialsUpdated() && !p.isPrepared() && (p.getStatus() == PipelineStatus.IN_PROGRESS))
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        result.setEntity(updatedPipelines);

        return result;
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAllPreparedPipelinesInProgress() {
        ServiceResult result = this.getAll();
        List<Pipeline> pipelines = (List<Pipeline>) result.getEntity();

        List<Pipeline> updatedPipelines = pipelines
                .stream()
                .filter(p -> p.isPrepared() && (p.getStatus() == PipelineStatus.IN_PROGRESS))
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        result.setEntity(updatedPipelines);

        return result;
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAllPreparedAwaitingPipelines() {
        ServiceResult result = this.getAll();
        List<Pipeline> pipelines = (List<Pipeline>) result.getEntity();

        List<Pipeline> updatedPipelines = pipelines
                .stream()
                .filter(p -> p.isPrepared() && (p.getStatus() == PipelineStatus.AWAITING))
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        result.setEntity(updatedPipelines);

        return result;
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getLastRun(String pipelineDefinitionId) {
        ServiceResult result = this.getAllByDefinitionId(pipelineDefinitionId);
        List<Pipeline> pipelines = (List<Pipeline>) result.getEntity();

        Pipeline lastRun = null;
        int lastExecutionId = 0;
        for (Pipeline pipeline : pipelines) {
            if (pipeline.getExecutionId() > lastExecutionId) {
                lastRun = pipeline;
                lastExecutionId = pipeline.getExecutionId();
            }
        }

        result.setEntity(lastRun);

        return result;
    }

//    @Override
//    public ServiceResult getAllPipelineHistoryDTOs(String pipelineDefinitionId) {
//        ServiceResult result = this.getAllByDefinitionId(pipelineDefinitionId);
//        List<Pipeline> pipelines = (List<Pipeline>) result.getObject();
//
//        List<PipelineDto> pipelineDtos = new ArrayList<>();
//        for (Pipeline pipeline : pipelines) {
//            PipelineDto pipelineDto = new PipelineDto();
//            pipelineDto.constructHistoryPipelineDto(pipeline);
//            pipelineDtos.add(pipelineDto);
//        }
//
//        result.setObject(pipelineDtos);
//
//        return result;
//    }

    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAllPipelineHistoryDTOs(String pipelineDefinitionId, Integer numberOfPipelines) {
        return this.getAllPipelineHistoryDTOs(pipelineDefinitionId, numberOfPipelines, null);
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAllPipelineHistoryDTOs(String pipelineDefinitionId, Integer numberOfPipelines, String pipelineId) {
        ServiceResult result = this.getAllByDefinitionId(pipelineDefinitionId);
        List<Pipeline> pipelines = (List<Pipeline>) result.getEntity();
        List<Pipeline> filteredPipelines = pipelines
                .stream()
                .sorted((p1, p2) -> p2.getStartTime().compareTo(p1.getStartTime()))
                .collect(Collectors.toList());

        int indexOfPipeline = this.getIndexOfPipeline(filteredPipelines, pipelineId);
        if (indexOfPipeline == -1) {
            filteredPipelines = filteredPipelines
                    .stream()
                    .limit(numberOfPipelines)
                    .collect(Collectors.toList());
        } else {
            filteredPipelines = filteredPipelines
                    .stream()
                    .skip(indexOfPipeline + 1)
                    .limit(numberOfPipelines)
                    .collect(Collectors.toList());
        }

        List<PipelineDto> pipelineDtos = new ArrayList<>();
        for (Pipeline pipeline : filteredPipelines) {
            PipelineDto pipelineDto = new PipelineDto();
            pipelineDto.constructArtifactPipelineDto(pipeline);
            pipelineDtos.add(pipelineDto);
        }

        result.setEntity(pipelineDtos);

        return result;
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAllPipelineArtifactDTOs(String searchCriteria, Integer numberOfPipelines) {
        return this.getAllPipelineArtifactDTOs(searchCriteria, numberOfPipelines, null);
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAllPipelineArtifactDTOs(String searchCriteria, Integer numberOfPipelines, String pipelineId) {
        ServiceResult result = this.getAll();
        List<Pipeline> pipelines = (List<Pipeline>) result.getEntity();
        List<Pipeline> filteredPipelines = pipelines
                .stream()
                .filter(p -> p.getPipelineDefinitionName().toLowerCase().contains(searchCriteria.toLowerCase()))
                .sorted((p1, p2) -> p2.getStartTime().compareTo(p1.getStartTime()))
                .collect(Collectors.toList());

        int indexOfPipeline = this.getIndexOfPipeline(filteredPipelines, pipelineId);
        if (indexOfPipeline == -1) {
            filteredPipelines = filteredPipelines
                    .stream()
                    .limit(numberOfPipelines)
                    .collect(Collectors.toList());
        } else {
            filteredPipelines = filteredPipelines
                    .stream()
                    .skip(indexOfPipeline + 1)
                    .limit(numberOfPipelines)
                    .collect(Collectors.toList());
        }

        List<PipelineDto> pipelineDtos = new ArrayList<>();
        for (Pipeline pipeline : filteredPipelines) {
            PipelineDto pipelineDto = new PipelineDto();
            pipelineDto.constructArtifactPipelineDto(pipeline);
            pipelineDtos.add(pipelineDto);
        }

        result.setEntity(pipelineDtos);

        return result;
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.OPERATOR )
    public ServiceResult cancelPipeline(String pipelineId) {
        ServiceResult result = this.getById(pipelineId);
        if (result.getNotificationType() == NotificationType.ERROR) {
            return result;
        }

        Pipeline pipeline = (Pipeline) result.getEntity();
        pipeline.setShouldBeCanceled(true);
        pipeline.setStatus(PipelineStatus.IN_PROGRESS);
        return this.update(pipeline);
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.OPERATOR )
    public ServiceResult pausePipeline(String pipelineId) {
        ServiceResult result = this.getById(pipelineId);
        if (result.getNotificationType() == NotificationType.ERROR) {
            return result;
        }

        Pipeline pipeline = (Pipeline) result.getEntity();
        if (pipeline.getStatus() == PipelineStatus.IN_PROGRESS) {
            pipeline.setStatus(PipelineStatus.PAUSED);
            result.setNotificationType(NotificationType.WARNING);
            String message = String.format("Pipeline %s set to PAUSED.", pipeline.getPipelineDefinitionName());
            result.setMessage(message);
            LOGGER.info(message);
            List<Stage> stages = pipeline.getStages();
            for (Stage stage : stages) {
                if (stage.getStatus() == StageStatus.IN_PROGRESS) {
                    stage.setStatus(StageStatus.PAUSED);
                }
            }
        } else {
            pipeline.setStatus(PipelineStatus.IN_PROGRESS);
            String message = String.format("Pipeline %s set to IN_PROGRESS.", pipeline.getPipelineDefinitionName());
            LOGGER.info(message);
            List<Stage> stages = pipeline.getStages();
            for (Stage stage : stages) {
                if (stage.getStatus() == StageStatus.PAUSED) {
                    stage.setStatus(StageStatus.IN_PROGRESS);
                    stage.setTriggeredManually(false);
                }
            }
        }

        return this.update(pipeline);
    }

    private void addMaterialsToPipeline(Pipeline pipeline) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getEntity();
        List<MaterialDefinition> materialDefinitions =
                (List<MaterialDefinition>) this.materialDefinitionService.getAllFromPipelineDefinition(pipelineDefinition.getId()).getEntity();

        List<Material> materials = new ArrayList<>();
        for (MaterialDefinition materialDefinition : materialDefinitions) {
            Material material = new Material();
            material.setPipelineDefinitionId(pipeline.getPipelineDefinitionId());
            material.setMaterialDefinition(materialDefinition);
            materials.add(material);
        }

        pipeline.setMaterials(materials);
    }

    private void addStagesToPipeline(Pipeline pipeline) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getEntity();
        List<StageDefinition> stageDefinitions = pipelineDefinition.getStageDefinitions();

        List<Stage> stages = new ArrayList<>();
        for (StageDefinition stageDefinition : stageDefinitions) {
            Stage stage = new Stage();
            stage.setPipelineId(pipeline.getId());
            stage.setStageDefinitionId(stageDefinition.getId());
            stage.setStageDefinitionName(stageDefinition.getName());
            stages.add(stage);
            this.addJobsToStage(stageDefinition, stage);
        }

        pipeline.setStages(stages);
    }

    private void addJobsToStage(StageDefinition stageDefinition, Stage stage) {
        List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();

        List<Job> jobs = new ArrayList<>();
        for (JobDefinition jobDefinition : jobDefinitions) {
            Job job = new Job();
            job.setPipelineId(stage.getPipelineId());
            job.setJobDefinitionId(jobDefinition.getId());
            job.setJobDefinitionName(jobDefinition.getName());
            job.setStageId(stage.getId());
            job.setJobDefinitionName(jobDefinition.getName());
            jobs.add(job);
            this.addTasksToJob(jobDefinition, job);
        }

        stage.setJobs(jobs);
    }

    private void addTasksToJob(JobDefinition jobDefinition, Job job) {
        List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();

        List<Task> tasks = new ArrayList<>();
        for (TaskDefinition taskDefinition : taskDefinitions) {

            Task task = new Task();
            if (taskDefinition.getType() == TaskType.FETCH_MATERIAL) {
                FetchMaterialTask fetchMaterialTask = (FetchMaterialTask) taskDefinition;
                task.setTaskDefinition(fetchMaterialTask);
            }

            task.setJobId(job.getId());
            task.setStageId(job.getStageId());
            task.setPipelineId(job.getPipelineId());
            task.setTaskDefinition(taskDefinition);
            task.setRunIfCondition(taskDefinition.getRunIfCondition());
            tasks.add(task);
        }

        job.setTasks(tasks);
    }

    private int getIndexOfPipeline(List<Pipeline> pipelines, String pipelineId) {
        int indexOfPipeline = -1;

        if (pipelineId != null) {
            int collectionSize = pipelines.size();
            for (int i = 0; i < collectionSize; i++) {
                if (pipelines.get(i).getId().equals(pipelineId)) {
                    indexOfPipeline = i;
                    break;
                }
            }
        }

        return indexOfPipeline;
    }
}
