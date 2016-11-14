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

import io.hawkcd.core.security.Authorization;
import io.hawkcd.core.security.Permission;
import io.hawkcd.core.security.Scope;
import io.hawkcd.db.DbRepositoryFactory;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.interfaces.IMaterialDefinitionService;
import io.hawkcd.services.interfaces.IPipelineDefinitionService;
import io.hawkcd.model.EnvironmentVariable;
import io.hawkcd.services.interfaces.IPipelineService;
import io.hawkcd.ws.EndpointConnector;

import java.util.List;
import java.util.stream.Collectors;

import io.hawkcd.model.GitMaterial;
import io.hawkcd.model.JobDefinition;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.Pipeline;
import io.hawkcd.model.PipelineDefinition;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.StageDefinition;
import io.hawkcd.model.TaskDefinition;

public class PipelineDefinitionService extends CrudService<PipelineDefinition> implements IPipelineDefinitionService {
    private static final Class CLASS_TYPE = PipelineDefinition.class;
    private IPipelineService pipelineService;

    private IMaterialDefinitionService materialDefinitionService;

    public PipelineDefinitionService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    public PipelineDefinitionService(IDbRepository repository) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    public PipelineDefinitionService(IDbRepository repository, IMaterialDefinitionService materialDefinitionService) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.materialDefinitionService = materialDefinitionService;
    }

    public PipelineDefinitionService(IDbRepository repository, IPipelineService pipelineService) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.pipelineService = pipelineService;
    }

    @Override
    @Authorization( scope = Scope.PIPELINE, permission = Permission.VIEWER )
    public ServiceResult getById(String pipelineDefinitionId) {
        return super.getById(pipelineDefinitionId);
    }

    @Override
    @Authorization( scope = Scope.PIPELINE, permission = Permission.VIEWER )
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override

    @Authorization( scope = Scope.PIPELINE_GROUP, permission = Permission.ADMIN )
    public ServiceResult add(PipelineDefinition pipelineDefinition) {
        EnvironmentVariable environmentVariable = new EnvironmentVariable();
        environmentVariable.setKey("COUNT");
        environmentVariable.setValue("1");
        environmentVariable.setDeletable(false);
        pipelineDefinition.getEnvironmentVariables().add(environmentVariable);

        List<StageDefinition> stageDefinitions = pipelineDefinition.getStageDefinitions();
        for (StageDefinition stageDefinition : stageDefinitions) {
            stageDefinition.setPipelineDefinitionId(pipelineDefinition.getId());

            List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();
            for (JobDefinition jobDefinition : jobDefinitions) {
                jobDefinition.setPipelineDefinitionId(pipelineDefinition.getId());
                jobDefinition.setStageDefinitionId(stageDefinition.getId());

                List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();
                for (TaskDefinition taskDefinition : taskDefinitions) {
                    taskDefinition.setPipelineDefinitionId(pipelineDefinition.getId());
                    taskDefinition.setStageDefinitionId(stageDefinition.getId());
                    taskDefinition.setJobDefinitionId(jobDefinition.getId());
                }
            }
        }

        return super.add(pipelineDefinition);
    }

    @Override
    @Authorization( scope = Scope.PIPELINE_GROUP, permission = Permission.ADMIN )
    public ServiceResult add(PipelineDefinition pipelineDefinition, MaterialDefinition materialDefinition) {
        if (this.materialDefinitionService == null) {
            this.materialDefinitionService = new MaterialDefinitionService();
        }

        ServiceResult serviceResult = this.materialDefinitionService.add(materialDefinition);
        if ((serviceResult.getNotificationType() == NotificationType.ERROR)) {
            return super.createServiceResult(null, NotificationType.ERROR, "could not be created");
        }

        EndpointConnector.passResultToEndpoint("MaterialDefinitionService", "add", serviceResult);
        pipelineDefinition.getMaterialDefinitionIds().add(materialDefinition.getId());

        return this.add(pipelineDefinition);
    }

    @Override
    @Authorization( scope = Scope.PIPELINE_GROUP, permission = Permission.ADMIN )
    public ServiceResult addWithMaterialDefinition(PipelineDefinition pipelineDefinition, GitMaterial materialDefinition) {
        return this.add(pipelineDefinition, materialDefinition);
    }

    @Override
    @Authorization( scope = Scope.PIPELINE_GROUP, permission = Permission.ADMIN )
    public ServiceResult addWithMaterialDefinition(PipelineDefinition pipelineDefinition, String materialDefinitionId) {
        if (this.materialDefinitionService == null) {
            this.materialDefinitionService = new MaterialDefinitionService();
        }

        this.materialDefinitionService = new MaterialDefinitionService();
        ServiceResult serviceResult = this.materialDefinitionService.getById(materialDefinitionId);
        if ((serviceResult.getNotificationType() == NotificationType.ERROR)) {
            return super.createServiceResult(null, NotificationType.ERROR, "could not be created");
        }

        pipelineDefinition.getMaterialDefinitionIds().add(materialDefinitionId);

        return this.add(pipelineDefinition);
    }

    @Override
    @Authorization( scope = Scope.PIPELINE, permission = Permission.ADMIN )
    public ServiceResult update(PipelineDefinition pipelineDefinition) {
        return super.update(pipelineDefinition);
    }

    @Override
    @Authorization( scope = Scope.PIPELINE, permission = Permission.ADMIN )
    public ServiceResult delete(String pipelineDefinitionId) {
        if (this.pipelineService == null) {
            this.pipelineService = new PipelineService();
        }
        List<Pipeline> pipelinesFromDb = (List<Pipeline>) this.pipelineService.getAll().getObject();

        if (pipelinesFromDb != null) {
            List<Pipeline> pipelineWithinThePipelineDefinition = pipelinesFromDb.stream().filter(p -> p.getPipelineDefinitionId().equals(pipelineDefinitionId)).collect(Collectors.toList());
            for (Pipeline pipeline : pipelineWithinThePipelineDefinition) {
                ServiceResult result = this.pipelineService.delete(pipeline.getId());
                if ((result.getNotificationType() == NotificationType.ERROR)) {
                    return result;
                }
            }
        }
        return super.delete(pipelineDefinitionId);
    }

    @Override
    public ServiceResult getAllAutomaticallyScheduledPipelines() {
        ServiceResult result;
        List<PipelineDefinition> allPipelines = (List<PipelineDefinition>) this.getAll().getObject();
        List<PipelineDefinition> scheduledPipelines = allPipelines.stream().filter(p -> p.isAutoSchedulingEnabled()).collect(Collectors.toList());
        result = super.createServiceResultArray(scheduledPipelines, NotificationType.SUCCESS, "retrieved successfully");
        return result;
    }

    @Override
    @Authorization( scope = Scope.PIPELINE, permission = Permission.ADMIN )
    public ServiceResult unassignPipelineFromGroup(String pipelineDefinitionId) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.getById(pipelineDefinitionId).getObject();
        if (pipelineDefinition == null) {
            return super.createServiceResult(null, NotificationType.ERROR, "could not be found");
        }

        pipelineDefinition.setPipelineGroupId("");
        pipelineDefinition.setGroupName("");

        return this.update(pipelineDefinition);
    }

    @Override
    @Authorization( scope = Scope.PIPELINE, permission = Permission.ADMIN )
    public ServiceResult assignPipelineToGroup(String pipelineDefinitionId, String pipelineGroupId, String pipelineGroupName) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.getById(pipelineDefinitionId).getObject();
        if (pipelineDefinition == null) {
            return super.createServiceResult(null, NotificationType.ERROR, "could not be found");
        }

        pipelineDefinition.setPipelineGroupId(pipelineGroupId);
        pipelineDefinition.setGroupName(pipelineGroupName);

        return this.update(pipelineDefinition);
    }
}
