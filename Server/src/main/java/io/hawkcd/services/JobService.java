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
import io.hawkcd.model.Job;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.Stage;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.services.interfaces.IJobService;
import io.hawkcd.services.interfaces.IStageService;

import java.util.ArrayList;
import java.util.List;

public class JobService extends CrudService<Job> implements IJobService {
    private static final Class CLASS_TYPE = Job.class;

    private IStageService stageService;
    private String failureMessage = "not found";
    private String successMessage = "retrieved successfully";

    public JobService() {
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.stageService = new StageService();
    }

    public JobService(IStageService stageService) {
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.stageService = stageService;
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getById(String jobId) {
        List<Stage> allStages = (List<Stage>) this.stageService.getAll().getEntity();
        Job result = null;
        for (Stage stage : allStages) {
            List<Job> jobs = stage.getJobs();
            for (Job job : jobs) {
                if (job.getId().equals(jobId)) {
                    result = job;
                    return super.createServiceResult(result, NotificationType.SUCCESS, this.successMessage);
                }
            }
        }

        return super.createServiceResult(result, NotificationType.ERROR, this.failureMessage);
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.NONE )
    public ServiceResult getAll() {
        List<Stage> allStages = (List<Stage>) this.stageService.getAll().getEntity();
        List<Job> allJobs = new ArrayList<>();

        for (Stage stage : allStages) {
            List<Job> jobs = stage.getJobs();
            allJobs.addAll(jobs);
        }

        return super.createServiceResultArray(allJobs, NotificationType.SUCCESS, this.successMessage);
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.ADMIN )
    public ServiceResult add(Job job) {
        Stage stage = (Stage) this.stageService.getById(job.getStageId()).getEntity();
        List<Job> jobs = stage.getJobs();

        for (Job jobFromDb : jobs) {
            if (jobFromDb.getId().equals(job.getId())) {
                return super.createServiceResult(null, NotificationType.ERROR, "already exists");
            }
        }

        jobs.add(job);
        stage.setJobs(jobs);
        ServiceResult serviceResult = this.stageService.update(stage);

        Job result = this.extractJobFromStage(stage, job.getId());

        if ((result == null) || (serviceResult.getNotificationType() == NotificationType.ERROR)) {
            return super.createServiceResult(null, NotificationType.ERROR, "not created");
        }

        return super.createServiceResult(result, NotificationType.SUCCESS, "created successfully");
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.ADMIN )
    public ServiceResult update(Job job) {
        ServiceResult serviceResult = null;
        Stage stage = (Stage) this.stageService.getById(job.getStageId()).getEntity();
        List<Job> jobs = stage.getJobs();
        int jobsSize = jobs.size();
        for (int i = 0; i < jobsSize; i++) {
            if (jobs.get(i).getId().equals(job.getId())) {
                jobs.set(i, job);
                stage.setJobs(jobs);
                serviceResult = this.stageService.update(stage);
                break;
            }
        }

        if (serviceResult == null) {
            return super.createServiceResult(null, NotificationType.ERROR, "not found");
        }

        if ((serviceResult.getNotificationType() == NotificationType.ERROR)) {
            serviceResult = super.createServiceResult((Job) serviceResult.getEntity(), NotificationType.ERROR, "not updated");
        } else {
            serviceResult = super.createServiceResult(job, NotificationType.SUCCESS, "updated successfully");
        }

        return serviceResult;
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.ADMIN )
    public ServiceResult delete(Job job) {
        Stage stageToUpdate = new Stage();
        Job jobToDelete = null;
        List<Stage> stages = (List<Stage>) this.stageService.getAll().getEntity();

        for (Stage stage : stages) {
            List<Job> jobs = stage.getJobs();
            for (Job j : jobs) {
                if (j.getId().equals(j)) {
                    stageToUpdate = stage;
                    jobToDelete = j;
                }
            }
        }

        if (jobToDelete == null) {
            return super.createServiceResult(jobToDelete, NotificationType.ERROR, "not found");
        }

        List<Job> jobs = stageToUpdate.getJobs();
        jobs.remove(jobToDelete);
        stageToUpdate.setJobs(jobs);
        ServiceResult serviceResult = this.stageService.update(stageToUpdate);

        if ((serviceResult.getNotificationType() == NotificationType.ERROR)) {
            return super.createServiceResult(null, NotificationType.ERROR, "not found");
        }

        return super.createServiceResult(jobToDelete, NotificationType.SUCCESS, "deleted successfully");
    }

    private Job extractJobFromStage(Stage stage, String jobId) {
        Job result = stage.getJobs().stream()
                .filter(job1 -> job1.getId().equals(jobId))
                .findFirst()
                .orElse(null);
        return result;
    }
}