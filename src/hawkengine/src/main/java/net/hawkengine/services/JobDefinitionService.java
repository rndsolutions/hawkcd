package net.hawkengine.services;

import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.interfaces.IJobDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IStageDefinitionService;

import java.util.ArrayList;
import java.util.List;

public class JobDefinitionService extends CrudService<JobDefinition> implements IJobDefinitionService {
    private IPipelineDefinitionService pipelineDefinitionService;
    private IStageDefinitionService stageDefinitionService;
    private String successMessage = "retrieved successfully";
    private String failureMessage = "not found";


    public JobDefinitionService() {
        super.setObjectType("JobDefinition");
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.stageDefinitionService = new StageDefinitionService();
    }

    public JobDefinitionService(IPipelineDefinitionService pipelineDefinitionService, IStageDefinitionService stageDefinitionService) {
        this.pipelineDefinitionService = pipelineDefinitionService;
        this.stageDefinitionService = stageDefinitionService;
    }

    @Override
    public ServiceResult getById(String jobDefinitionId) {
        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
        JobDefinition result = new JobDefinition();

        for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
            List<StageDefinition> stageDefinitions = pipelineDefinition.getStageDefinitions();
            for (StageDefinition stageDefinition : stageDefinitions) {
                List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();
                for (JobDefinition jobDefinition : jobDefinitions) {
                    if (jobDefinition.getId().equals(jobDefinitionId)) {
                        result = jobDefinition;
                        return super.createServiceResult(result, false, this.successMessage);
                    }
                }
            }
        }

        return super.createServiceResult(result, true, this.failureMessage);
    }

    @Override
    public ServiceResult getAll() {
        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
        List<JobDefinition> jobDefinitions = new ArrayList<>();

        for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
            this.extractJobDefinitionsFromPipelineDefinition(pipelineDefinition, jobDefinitions);
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
        boolean isUpdated = false;
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
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        List<JobDefinition> allJobsInPipeline = new ArrayList<>();
        this.extractJobDefinitionsFromPipelineDefinition(pipelineDefinition, allJobsInPipeline);
        return super.createServiceResultArray(allJobsInPipeline, false, this.successMessage);
    }

    /**
     * Method void for extracting JobDefinitions from PipelineDefinition provided. Fills in a
     * provided List.
     */
    private void extractJobDefinitionsFromPipelineDefinition(PipelineDefinition pipelineDefinition, List<JobDefinition> jobDefinitions) {
        List<StageDefinition> stages = pipelineDefinition.getStageDefinitions();
        for (StageDefinition stage : stages) {
            List<JobDefinition> currentStageJobs = stage.getJobDefinitions();
            jobDefinitions.addAll(currentStageJobs);
        }
    }

    /**
     * Method boolean accepts a list of JobDefinitions, performs name check and decides wheather it
     * has name collision or not.
     */
    private boolean checkForNameCollision(List<JobDefinition> jobDefinitions, JobDefinition jobDefinitionToAdd) {
        for (JobDefinition jobDefinition : jobDefinitions) {
            if (jobDefinition.getName().equals(jobDefinitionToAdd.getName())) {
                return true;
            }
        }

        return false;
    }

    // TODO: document this method
    private JobDefinition extractJobDefinitionsFromStageDefinition(StageDefinition stageDefinition, String jobDefinitionId) {
        StageDefinition updatedStageDefinition = (StageDefinition) this.stageDefinitionService.update(stageDefinition).getObject();
        JobDefinition result = updatedStageDefinition
                .getJobDefinitions()
                .stream()
                .filter(jd -> jd.getId().equals(jobDefinitionId))
                .findFirst()
                .orElse(null);

        return result;
    }
}