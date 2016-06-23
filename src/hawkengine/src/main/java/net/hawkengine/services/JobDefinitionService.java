package net.hawkengine.services;

import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.interfaces.IJobDefinitionService;
import net.hawkengine.services.interfaces.IStageDefinitionService;

import java.util.ArrayList;
import java.util.List;

public class JobDefinitionService extends CrudService<JobDefinition> implements IJobDefinitionService {
    private IStageDefinitionService stageDefinitionService;
    private String successMessage = "retrieved successfully";
    private String failureMessage = "not found";


    public JobDefinitionService() {
        super.setObjectType("JobDefinition");
        this.stageDefinitionService = new StageDefinitionService();
    }

    public JobDefinitionService(IStageDefinitionService stageDefinitionService) {
        super.setObjectType("JobDefinition");
        this.stageDefinitionService = stageDefinitionService;
    }

    @Override
    public ServiceResult getById(String jobDefinitionId) {
        List<StageDefinition> stageDefinitions = (List<StageDefinition>) this.stageDefinitionService.getAll().getObject();
        JobDefinition result = null;

        for (StageDefinition stageDefinition : stageDefinitions) {
            List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();
            for (JobDefinition jobDefinition : jobDefinitions) {
                if (jobDefinition.getId().equals(jobDefinitionId)) {
                    result = jobDefinition;
                    return super.createServiceResult(result, false, this.successMessage);
                }
            }
        }

        return super.createServiceResult(result, true, this.failureMessage);
    }

    @Override
    public ServiceResult getAll() {
        List<StageDefinition> stageDefinitions = (List<StageDefinition>) this.stageDefinitionService.getAll().getObject();
        List<JobDefinition> jobDefinitions = new ArrayList<>();

        for (StageDefinition stageDefinition : stageDefinitions) {
            List<JobDefinition> currentJobDefinitions = stageDefinition.getJobDefinitions();
            jobDefinitions.addAll(currentJobDefinitions);
        }

        return super.createServiceResultArray(jobDefinitions, false, this.successMessage);
    }

    @Override
    public ServiceResult add(JobDefinition jobDefinition) {
        StageDefinition stageDefinition = (StageDefinition) this.stageDefinitionService.getById(jobDefinition.getStageDefinitionId()).getObject();
        List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();
        boolean hasNameCollision = this.checkForNameCollision(jobDefinitions, jobDefinition);
        if (hasNameCollision) {
            return super.createServiceResult(jobDefinition, true, "with the same name exists");
        }

        jobDefinitions.add(jobDefinition);
        stageDefinition.setJobDefinitions(jobDefinitions);
        StageDefinition updatedStageDefinition = (StageDefinition) this.stageDefinitionService.update(stageDefinition).getObject();

        JobDefinition result = this.extractJobDefinitionsFromStageDefinition(updatedStageDefinition, jobDefinition.getId());
        if (result == null) {
            return super.createServiceResult(result, true, "not added successfully");
        }

        return super.createServiceResult(result, false, "added successfully");
    }

    @Override
    public ServiceResult update(JobDefinition jobDefinition) {
        JobDefinition result = new JobDefinition();
        StageDefinition stageDefinition = (StageDefinition) this.stageDefinitionService.getById(jobDefinition.getStageDefinitionId()).getObject();
        List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();
        boolean hasNameCollision = this.checkForNameCollision(jobDefinitions, jobDefinition);
        if (hasNameCollision) {
            return super.createServiceResult(jobDefinition, true, "with the same name exists");
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
            return super.createServiceResult(result, true, "not updated successfully");
        }

        return super.createServiceResult(result, false, "updated successfully");
    }

    @Override
    public ServiceResult delete(String jobDefinitionId) {
        boolean isRemoved = false;
        JobDefinition jobDefinitionToDelete = (JobDefinition) this.getById(jobDefinitionId).getObject();
        if (jobDefinitionToDelete == null) {
            return super.createServiceResult(jobDefinitionToDelete, true, "does not exists");
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
            return super.createServiceResult(jobDefinitionToDelete, true, "cannot delete the last job definition");
        }

        if (!isRemoved) {
            return super.createServiceResult(jobDefinitionToDelete, true, "not deleted");
        }

        stageDefinition.setJobDefinitions(jobDefinitions);
        StageDefinition updatedStageDefinition = (StageDefinition) this.stageDefinitionService.update(stageDefinition).getObject();
        JobDefinition result = this.extractJobDefinitionsFromStageDefinition(updatedStageDefinition, jobDefinitionId);
        if (result != null) {
            return super.createServiceResult(result, true, "not deleted successfully");
        }

        return super.createServiceResult(result, false, "deleted successfully");
    }

    @Override
    public ServiceResult getAllInStage(String stageDefinitionId) {
        StageDefinition currentStage = (StageDefinition) this.stageDefinitionService.getById(stageDefinitionId).getObject();
        List<JobDefinition> allJobDefinitionsInStage = currentStage.getJobDefinitions();
        return super.createServiceResultArray(allJobDefinitionsInStage, false, this.successMessage);
    }

    @Override
    public ServiceResult getAllInPipeline(String pipelineDefinitionId) {
        List<StageDefinition> stageDefinitions = (List<StageDefinition>) this.stageDefinitionService.getAllInPipeline(pipelineDefinitionId).getObject();
        List<JobDefinition> allJobsInPipeline = new ArrayList<>();
        this.extractJobDefinitionsFromStageDefinitions(stageDefinitions, allJobsInPipeline);
        return super.createServiceResultArray(allJobsInPipeline, false, this.successMessage);
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
            if(jobDefinition.getId().equals(jobDefinitionToAdd.getId())){
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