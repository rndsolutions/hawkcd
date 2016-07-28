package net.hawkengine.services;

import net.hawkengine.model.Job;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.Stage;
import net.hawkengine.services.interfaces.IJobService;
import net.hawkengine.services.interfaces.IStageService;

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
    public ServiceResult getById(String jobId) {
        List<Stage> allStages = (List<Stage>) this.stageService.getAll().getObject();
        Job result = null;
        for (Stage stage : allStages) {
            List<Job> jobs = stage.getJobs();
            for (Job job : jobs) {
                if (job.getId().equals(jobId)) {
                    result = job;
                    return super.createServiceResult(result, false, this.successMessage);
                }
            }
        }

        return super.createServiceResult(result, true, this.failureMessage);
    }

    @Override
    public ServiceResult getAll() {
        List<Stage> allStages = (List<Stage>) this.stageService.getAll().getObject();
        List<Job> allJobs = new ArrayList<>();

        for (Stage stage : allStages) {
            List<Job> jobs = stage.getJobs();
            allJobs.addAll(jobs);
        }

        return super.createServiceResultArray(allJobs, false, this.successMessage);
    }

    @Override
    public ServiceResult add(Job job) {
        Stage stage = (Stage) this.stageService.getById(job.getStageId()).getObject();
        List<Job> jobs = stage.getJobs();

        for (Job jobFromDb : jobs) {
            if (jobFromDb.getId().equals(job.getId())) {
                return super.createServiceResult(null, true, "already exists");
            }
        }

        jobs.add(job);
        stage.setJobs(jobs);
        ServiceResult serviceResult = this.stageService.update(stage);

        Job result = this.extractJobFromStage(stage, job.getId());

        if ((result == null) || serviceResult.hasError()) {
            return super.createServiceResult(null, true, "not created");
        }

        return super.createServiceResult(result, false, "created successfully");
    }

    @Override
    public ServiceResult update(Job job) {
        ServiceResult serviceResult = null;
        Stage stage = (Stage) this.stageService.getById(job.getStageId()).getObject();
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
            return super.createServiceResult(null, true, "not found");
        }

        if (serviceResult.hasError()) {
            serviceResult = super.createServiceResult((Job) serviceResult.getObject(), true, "not updated");
        } else {
            serviceResult = super.createServiceResult(job, false, "updated successfully");
        }

        return serviceResult;
    }

    @Override
    public ServiceResult delete(String jobId) {
        Stage stageToUpdate = new Stage();
        Job jobToDelete = null;
        List<Stage> stages = (List<Stage>) this.stageService.getAll().getObject();

        for (Stage stage : stages) {
            List<Job> jobs = stage.getJobs();
            for (Job job : jobs) {
                if (job.getId().equals(jobId)) {
                    stageToUpdate = stage;
                    jobToDelete = job;
                }
            }
        }

        if (jobToDelete == null) {
            return super.createServiceResult(jobToDelete, true, "not found");
        }

        List<Job> jobs = stageToUpdate.getJobs();
        jobs.remove(jobToDelete);
        stageToUpdate.setJobs(jobs);
        ServiceResult serviceResult = this.stageService.update(stageToUpdate);

        if (serviceResult.hasError()) {
            return super.createServiceResult(null, true, "not found");
        }

        return super.createServiceResult(jobToDelete, false, "deleted successfully");
    }

    private Job extractJobFromStage(Stage stage, String jobId) {
        Job result = stage.getJobs().stream()
                .filter(job1 -> job1.getId().equals(jobId))
                .findFirst()
                .orElse(null);
        return result;
    }
}