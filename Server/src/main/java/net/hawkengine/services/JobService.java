package net.hawkengine.services;

import net.hawkengine.model.Job;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.Stage;
import net.hawkengine.services.interfaces.IJobService;
import net.hawkengine.services.interfaces.IStageService;
import java.util.ArrayList;
import java.util.List;

public class JobService extends CrudService<Job> implements IJobService{
    private IStageService stageService;
    private String failureMessage = "not found";
    private String successMessage = "retrieved successfully";

    public JobService(){
        super.setObjectType("Job");
        this.stageService = new StageService();
    }

    public JobService(IStageService stageService){
        super.setObjectType("Job");
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
        Stage stage = (Stage)this.stageService.getById(job.getStageId()).getObject();
        Job result = null;
        List<Job> jobs = stage.getJobs();
        jobs.add(job);
        stage.setJobs(jobs);
        ServiceResult serviceResult = this.stageService.update(stage);

        if (!serviceResult.hasError()) {
            result = job;
        }

        if (result == null ) {
            return super.createServiceResult(result, true, "not created");
        }
        return super.createServiceResult(result,false,"created successfully");
    }

    @Override
    public ServiceResult update(Job job) {
        ServiceResult serviceResult = new ServiceResult();
        Stage stage = (Stage)this.stageService.getById(job.getStageId()).getObject();
        List<Job> jobList = stage.getJobs();
        int jobsListSize = jobList.size();
        boolean isPresent = false;
        for (int i = 0; i < jobsListSize; i++){
            if(jobList.get(i).getId().equals(job.getId())){
                jobList.set(i,job);
                isPresent = true;
                stage.setJobs(jobList);
                serviceResult = this.stageService.update(stage);
                break;
            }
        }

        if (!isPresent){
            return super.createServiceResult((Job)serviceResult.getObject(),true,"not found");
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
        List<Stage> stages = (List<Stage>) this.stageService.getAll().getObject();

        for (Stage stage : stages){
            List <Job> jobs = stage.getJobs();
            for (Job job : jobs){
                if (job.getId().equals(jobId)){
                    stageToUpdate = stage;
                }
            }
        }

        boolean isRemoved = false;
        ServiceResult serviceResult;
        List<Job> jobs = stageToUpdate.getJobs();
        Job job = jobs.stream()
                .filter(j-> j.getId().equals(jobId))
                .findFirst()
                .orElse(null);
        if (jobs.size() > 1){
            isRemoved = jobs.remove(job);
        } else {
            return super.createServiceResult(job, true, "is the last Job and cannot be deleted");
        }

        if (isRemoved){
            stageToUpdate.setJobs(jobs);
            serviceResult = this.stageService.update(stageToUpdate);
            if (!serviceResult.hasError()) {
                return super.createServiceResult(job, false, "deleted successfully");
            }
        } else {
            return super.createServiceResult(job, true, "not found");
        }
        return serviceResult;
    }


}