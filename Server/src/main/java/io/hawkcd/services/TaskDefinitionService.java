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

import io.hawkcd.model.ExecTask;
import io.hawkcd.model.FetchArtifactTask;
import io.hawkcd.model.FetchMaterialTask;
import io.hawkcd.model.JobDefinition;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.UploadArtifactTask;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.TaskType;
import io.hawkcd.services.interfaces.IJobDefinitionService;
import io.hawkcd.services.interfaces.ITaskDefinitionService;

import java.util.ArrayList;
import java.util.List;

import io.hawkcd.model.TaskDefinition;

public class TaskDefinitionService extends CrudService<TaskDefinition> implements ITaskDefinitionService {
    private static final Class CLASS_TYPE = TaskDefinition.class;

    private IJobDefinitionService jobDefinitionService;
    private String successMessage = "retrieved successfully";
    private String failureMessage = "not found";

    public TaskDefinitionService() {
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.jobDefinitionService = new JobDefinitionService();
    }

    public TaskDefinitionService(IJobDefinitionService jobDefinitionService) {
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.jobDefinitionService = jobDefinitionService;
    }

    @Override
    public ServiceResult getById(String taskDefinitionId) {
        List<JobDefinition> jobDefinitions = (List<JobDefinition>) this.jobDefinitionService.getAll().getObject();
        TaskDefinition result = null;

        for (JobDefinition jobDefinition : jobDefinitions) {
            List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();
            for (TaskDefinition taskDefinition : taskDefinitions) {
                if (taskDefinition.getId().equals(taskDefinitionId)) {
                    result = taskDefinition;
                    return super.createServiceResult(result, NotificationType.SUCCESS, this.successMessage);
                }
            }
        }

        return super.createServiceResult(result, NotificationType.ERROR, this.failureMessage);
    }

    @Override
    public ServiceResult getAll() {
        List<JobDefinition> jobDefinitions = (List<JobDefinition>) this.jobDefinitionService.getAll().getObject();
        List<TaskDefinition> taskDefinitions = new ArrayList<>();

        for (JobDefinition jobDefinition : jobDefinitions) {
            List<TaskDefinition> taskJobDefinitions = jobDefinition.getTaskDefinitions();
            taskDefinitions.addAll(taskJobDefinitions);
        }

        return super.createServiceResultArray(taskDefinitions, NotificationType.SUCCESS, this.successMessage);
    }

    @Override
    public ServiceResult add(ExecTask taskDefintion) {
        ServiceResult result = this.addTask(taskDefintion);
        return result;
    }

    @Override
    public ServiceResult add(FetchMaterialTask taskDefintion) {
        ServiceResult result = this.addTask(taskDefintion);
        return result;
    }

    @Override
    public ServiceResult add(FetchArtifactTask taskDefinition) {
        ServiceResult result = this.addTask(taskDefinition);
        return result;
    }

    @Override
    public ServiceResult add(UploadArtifactTask taskDefinition) {
        ServiceResult result = this.addTask(taskDefinition);
        return result;
    }

    public ServiceResult addTask(TaskDefinition taskDefinition) {
        JobDefinition jobDefinition = (JobDefinition) this.jobDefinitionService.getById(taskDefinition.getJobDefinitionId()).getObject();
        List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();
        taskDefinitions.add(taskDefinition);
        jobDefinition.setTaskDefinitions(taskDefinitions);
        JobDefinition updatedJobDefinition = (JobDefinition) this.jobDefinitionService.update(jobDefinition).getObject();

        TaskDefinition result = this.extractTaskDefinitionFromJobDefinition(updatedJobDefinition, taskDefinition.getId());
        if (result == null) {
            return super.createServiceResult(result, NotificationType.ERROR, "not added successfully");
        }

        return super.createServiceResult(result, NotificationType.SUCCESS, "added successfully");
    }

    @Override
    public ServiceResult update(ExecTask taskDefintion) {
        ServiceResult result = this.updateTask(taskDefintion);
        return result;
    }

    @Override
    public ServiceResult update(FetchMaterialTask taskDefintion) {
        ServiceResult result = this.updateTask(taskDefintion);
        return result;
    }

    @Override
    public ServiceResult update(FetchArtifactTask taskDefinition) {
        ServiceResult result = this.updateTask(taskDefinition);
        return result;
    }

    @Override
    public ServiceResult update(UploadArtifactTask taskDefinition) {
        ServiceResult result = this.updateTask(taskDefinition);
        return result;
    }

    public ServiceResult updateTask(TaskDefinition taskDefinition) {
        TaskDefinition result = null;

        JobDefinition jobDefinition = (JobDefinition) this.jobDefinitionService.getById(taskDefinition.getJobDefinitionId()).getObject();
        List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();
        int lengthOfTaskDefinitions = taskDefinitions.size();
        for (int i = 0; i < lengthOfTaskDefinitions; i++) {
            TaskDefinition definition = taskDefinitions.get(i);
            if (definition.getId().equals(taskDefinition.getId())) {
                Class resultTaskClass = this.getTaskDefinitionType(taskDefinition);
                try {
                    result = (TaskDefinition) resultTaskClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                taskDefinitions.set(i, taskDefinition);
                jobDefinition.setTaskDefinitions(taskDefinitions);
                JobDefinition updatedJobDefinition = (JobDefinition) this.jobDefinitionService.update(jobDefinition).getObject();
                result = this.extractTaskDefinitionFromJobDefinition(updatedJobDefinition, taskDefinition.getId());
                break;
            }
        }

        if (result == null) {
            return super.createServiceResult(result, NotificationType.ERROR, "not found");
        }

        return super.createServiceResult(result, NotificationType.SUCCESS, "updated successfully");
    }

    @Override
    public ServiceResult delete(String taskDefinitionId) {
        boolean isRemoved = false;
        TaskDefinition taskDefinitionToDelete = (TaskDefinition) this.getById(taskDefinitionId).getObject();
        if (taskDefinitionToDelete == null) {
            return super.createServiceResult(taskDefinitionToDelete, NotificationType.ERROR, "does not exists");
        }

        JobDefinition jobDefinition = (JobDefinition) this.jobDefinitionService
                .getById(taskDefinitionToDelete.getJobDefinitionId())
                .getObject();
        List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();

        int lengthOfTaskDefinitions = taskDefinitions.size();
        if (lengthOfTaskDefinitions > 1) {
            for (int i = 0; i < lengthOfTaskDefinitions; i++) {
                TaskDefinition definition = taskDefinitions.get(i);
                if (definition.getId().equals(taskDefinitionToDelete.getId())) {
                    taskDefinitions.remove(definition);
                    isRemoved = true;
                    break;
                }
            }
        } else {
            return super.createServiceResult(taskDefinitionToDelete, NotificationType.ERROR, "cannot delete the last task definition");
        }

        if (!isRemoved) {
            return super.createServiceResult(taskDefinitionToDelete, NotificationType.ERROR, "not deleted");
        }

        jobDefinition.setTaskDefinitions(taskDefinitions);
        JobDefinition updatedJobDefinition = (JobDefinition) this.jobDefinitionService.update(jobDefinition).getObject();
        TaskDefinition result = this.extractTaskDefinitionFromJobDefinition(updatedJobDefinition, taskDefinitionId);
        if (result != null) {
            return super.createServiceResult(result, NotificationType.ERROR, "not deleted successfully");
        }

        return super.createServiceResult(result, NotificationType.SUCCESS, "deleted successfully");
    }

    /**
     * Method return TaskDefinition, accepts JobDefinition and TaskDefinition Id, filters all
     * TaskDefinitions in the provided job. Returns null if no TaskDefinition with provided id is
     * found.
     */
    private TaskDefinition extractTaskDefinitionFromJobDefinition(JobDefinition jobDefinition, String taskDefinitionId) {
        TaskDefinition result = jobDefinition
                .getTaskDefinitions()
                .stream()
                .filter(td -> td.getId().equals(taskDefinitionId))
                .findFirst()
                .orElse(null);

        return result;
    }

    private Class<?> getTaskDefinitionType(TaskDefinition taskDefinition) {
        TaskType taskDefinitionType = taskDefinition.getType();
        Class result = null;
        switch (taskDefinitionType) {
            case EXEC:
                result = ExecTask.class;
                break;
            case FETCH_ARTIFACT:
                result = FetchArtifactTask.class;
                break;
            case FETCH_MATERIAL:
                result = FetchMaterialTask.class;
                break;
            case UPLOAD_ARTIFACT:
                result = UploadArtifactTask.class;
                break;
        }

        return result;
    }
}