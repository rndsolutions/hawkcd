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
import io.hawkcd.model.JobDefinition;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.StageDefinition;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.services.interfaces.IJobDefinitionService;
import io.hawkcd.services.interfaces.IStageDefinitionService;

import java.util.ArrayList;
import java.util.List;

public class JobDefinitionService extends CrudService<JobDefinition> implements IJobDefinitionService {
    private static final Class CLASS_TYPE = JobDefinition.class;

    private IStageDefinitionService stageDefinitionService;
    private String successMessage = "retrieved successfully";
    private String failureMessage = "not found";

    public JobDefinitionService() {
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.stageDefinitionService = new StageDefinitionService();
    }

    public JobDefinitionService(IStageDefinitionService stageDefinitionService) {
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.stageDefinitionService = stageDefinitionService;
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getById(String jobDefinitionId) {
        List<StageDefinition> stageDefinitions = (List<StageDefinition>) this.stageDefinitionService.getAll().getObject();
        JobDefinition result = null;

        for (StageDefinition stageDefinition : stageDefinitions) {
            List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();
            for (JobDefinition jobDefinition : jobDefinitions) {
                if (jobDefinition.getId().equals(jobDefinitionId)) {
                    result = jobDefinition;
                    return super.createServiceResult(result, NotificationType.SUCCESS, this.successMessage);
                }
            }
        }

        return super.createServiceResult(result, NotificationType.ERROR, this.failureMessage);
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAll() {
        List<StageDefinition> stageDefinitions = (List<StageDefinition>) this.stageDefinitionService.getAll().getObject();
        List<JobDefinition> jobDefinitions = new ArrayList<>();

        for (StageDefinition stageDefinition : stageDefinitions) {
            List<JobDefinition> currentJobDefinitions = stageDefinition.getJobDefinitions();
            jobDefinitions.addAll(currentJobDefinitions);
        }

        return super.createServiceResultArray(jobDefinitions, NotificationType.SUCCESS, this.successMessage);
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.ADMIN )
    public ServiceResult add(JobDefinition jobDefinition) {
        List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();
        if (!taskDefinitions.isEmpty()) {
            taskDefinitions.stream()
                    .filter(taskDefinition -> taskDefinition.getJobDefinitionId() == null)
                    .forEach(taskDefinition -> taskDefinition.setJobDefinitionId(jobDefinition.getId()));
        }
        StageDefinition stageDefinition = (StageDefinition) this.stageDefinitionService.getById(jobDefinition.getStageDefinitionId()).getObject();
        List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();
        boolean hasNameCollision = this.checkForNameCollision(jobDefinitions, jobDefinition);
        if (hasNameCollision) {
            return super.createServiceResult(jobDefinition, NotificationType.ERROR, "with the same name exists");
        }

        jobDefinitions.add(jobDefinition);
        stageDefinition.setJobDefinitions(jobDefinitions);
        StageDefinition updatedStageDefinition = (StageDefinition) this.stageDefinitionService.update(stageDefinition).getObject();

        JobDefinition result = this.extractJobDefinitionsFromStageDefinition(updatedStageDefinition, jobDefinition.getId());
        if (result == null) {
            return super.createServiceResult(result, NotificationType.ERROR, "not added successfully");
        }

        return super.createServiceResult(result, NotificationType.SUCCESS, "added successfully");
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.ADMIN )
    public ServiceResult update(JobDefinition jobDefinition) {
        JobDefinition result = new JobDefinition();
        StageDefinition stageDefinition = (StageDefinition) this.stageDefinitionService.getById(jobDefinition.getStageDefinitionId()).getObject();
        List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();
        boolean hasNameCollision = this.checkForNameCollision(jobDefinitions, jobDefinition);
        if (hasNameCollision) {
            return super.createServiceResult(jobDefinition, NotificationType.ERROR, "with the same name exists");
        }

        int lengthOfJobDefinitions = jobDefinitions.size();
        for (int i = 0; i < lengthOfJobDefinitions; i++) {
            JobDefinition definition = jobDefinitions.get(i);
            if (definition.getId().equals(jobDefinition.getId())) {
                jobDefinitions.set(i, jobDefinition);
                stageDefinition.setJobDefinitions(jobDefinitions);
                StageDefinition updatedStageDefinition = (StageDefinition) this.stageDefinitionService.update(stageDefinition).getObject();
                result = this.extractJobDefinitionsFromStageDefinition(updatedStageDefinition, jobDefinition.getId());
                break;
            }
        }

        if (result == null) {
            return super.createServiceResult(result, NotificationType.ERROR, "not updated successfully");
        }

        return super.createServiceResult(result, NotificationType.SUCCESS, "updated successfully");
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.ADMIN )
    public ServiceResult delete(JobDefinition jobDefinition) {
        boolean isRemoved = false;
        JobDefinition jobDefinitionToDelete = (JobDefinition) this.getById(jobDefinition.getId()).getObject();
        if (jobDefinitionToDelete == null) {
            return super.createServiceResult(jobDefinitionToDelete, NotificationType.ERROR, "does not exists");
        }

        StageDefinition stageDefinition = (StageDefinition) this.stageDefinitionService
                .getById(jobDefinitionToDelete.getStageDefinitionId())
                .getObject();
        List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();

        int lengthOfJobDefinitions = jobDefinitions.size();
        if (lengthOfJobDefinitions > 1) {
            for (int i = 0; i < lengthOfJobDefinitions; i++) {
                JobDefinition definition = jobDefinitions.get(i);
                if (definition.getId().equals(jobDefinitionToDelete.getId())) {
                    jobDefinitions.remove(definition);
                    isRemoved = true;
                    break;
                }
            }
        } else {
            return super.createServiceResult(jobDefinitionToDelete, NotificationType.ERROR, "cannot delete the last job definition");
        }

        if (!isRemoved) {
            return super.createServiceResult(jobDefinitionToDelete, NotificationType.ERROR, "not deleted");
        }

        stageDefinition.setJobDefinitions(jobDefinitions);
        StageDefinition updatedStageDefinition = (StageDefinition) this.stageDefinitionService.update(stageDefinition).getObject();
        JobDefinition result = this.extractJobDefinitionsFromStageDefinition(updatedStageDefinition, jobDefinition.getId());
        if (result != null) {
            return super.createServiceResult(result, NotificationType.ERROR, "not deleted successfully");
        }

        return super.createServiceResult(result, NotificationType.SUCCESS, "deleted successfully");
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAllInStage(String stageDefinitionId) {
        StageDefinition currentStage = (StageDefinition) this.stageDefinitionService.getById(stageDefinitionId).getObject();
        List<JobDefinition> allJobDefinitionsInStage = currentStage.getJobDefinitions();
        return super.createServiceResultArray(allJobDefinitionsInStage, NotificationType.SUCCESS, this.successMessage);
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAllInPipeline(String pipelineDefinitionId) {
        List<StageDefinition> stageDefinitions = (List<StageDefinition>) this.stageDefinitionService.getAllInPipeline(pipelineDefinitionId).getObject();
        List<JobDefinition> allJobsInPipeline = new ArrayList<>();
        this.extractJobDefinitionsFromStageDefinitions(stageDefinitions, allJobsInPipeline);
        return super.createServiceResultArray(allJobsInPipeline, NotificationType.SUCCESS, this.successMessage);
    }

    /**
     * Method void for extracting JobDefinitions from PipelineDefinition provided. Fills in a
     * provided List.
     */
    private void extractJobDefinitionsFromStageDefinitions(List<StageDefinition> stageDefinitions, List<JobDefinition> jobDefinitions) {
        for (StageDefinition stageDefinition : stageDefinitions) {
            List<JobDefinition> jobDefinitionsList = stageDefinition.getJobDefinitions();
            jobDefinitions.addAll(jobDefinitionsList);
        }
    }

    /**
     * Method boolean accepts a list of JobDefinitions, performs name check and decides wheather it
     * has name collision or not.
     */
    private boolean checkForNameCollision(List<JobDefinition> jobDefinitions, JobDefinition jobDefinitionToAdd) {
        for (JobDefinition jobDefinition : jobDefinitions) {
            if (jobDefinition.getId().equals(jobDefinitionToAdd.getId())) {
                continue;
            }
            if (jobDefinition.getName().equals(jobDefinitionToAdd.getName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method return JobDefinition, accepts StageDefinition and JobDefinition Id, filters all
     * JobDefinitions in the provided stage. Returns null if no JobDefinition with provided id is
     * found.
     */
    private JobDefinition extractJobDefinitionsFromStageDefinition(StageDefinition stageDefinition, String jobDefinitionId) {
        JobDefinition result = stageDefinition
                .getJobDefinitions()
                .stream()
                .filter(jd -> jd.getId().equals(jobDefinitionId))
                .findFirst()
                .orElse(null);

        return result;
    }
}