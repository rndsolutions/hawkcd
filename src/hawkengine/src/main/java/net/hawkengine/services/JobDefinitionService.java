package net.hawkengine.services;

import com.sun.istack.internal.Nullable;

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

    @Override
    public ServiceResult getById(String id) {
        List<PipelineDefinition> pipelines = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
        JobDefinition resultJbDefinition = new JobDefinition();

        for (PipelineDefinition pipelineDefinition : pipelines) {
            List<StageDefinition> stageDefinitions = pipelineDefinition.getStageDefinitions();
            for (StageDefinition stageDefinition : stageDefinitions) {
                List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();
                for (JobDefinition jobDefinition : jobDefinitions) {
                    if (jobDefinition.getId().equals(id)) {
                        resultJbDefinition = jobDefinition;
                        return super.createServiceResult(resultJbDefinition,false,this.successMessage);
                    }
                }
            }
        }

        return super.createServiceResult(resultJbDefinition,true,this.failureMessage);
    }

    @Override
    public ServiceResult getAll() {
        ArrayList<PipelineDefinition> allPipelines = (ArrayList<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
        ArrayList<JobDefinition> jobDefinitions = new ArrayList<>();
        for (PipelineDefinition definition : allPipelines) {
            this.extractJobDefinitions(definition, jobDefinitions);
        }

        return super.createServiceResultArray(jobDefinitions,false,this.successMessage);
    }

    @Override
    public ServiceResult add(JobDefinition jobDefinition) {
        StageDefinition currentStage = (StageDefinition) this.stageDefinitionService.getById(jobDefinition.getStageDefinitionId()).getObject();
        List<JobDefinition> jobDefinitions = currentStage.getJobDefinitions();
        boolean hasNameCollision = this.checkForNameCollision(jobDefinitions, jobDefinition);
        if (hasNameCollision) {
            return super.createServiceResult(jobDefinition, true, "with the same name exists");
        }

        jobDefinitions.add(jobDefinition);
        currentStage.setJobDefinitions(jobDefinitions);
        StageDefinition updatedStageDefinitions = (StageDefinition) this.stageDefinitionService.update(currentStage).getObject();
        JobDefinition result = updatedStageDefinitions
                .getJobDefinitions()
                .stream()
                .filter(jd -> jd.getId().equals(jobDefinition.getId()))
                .findFirst()
                .orElse(null);

        return super.createServiceResult(result,false,"added successfully");
    }

    @Override
    public ServiceResult update(JobDefinition jobDefinition) {
        boolean isUpdated = false;
        StageDefinition stageDefinition = (StageDefinition) this.stageDefinitionService.getById(jobDefinition.getStageDefinitionId()).getObject();
        List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();
        int lengthOfDefinitions = jobDefinitions.size();
        boolean hasNameCollision = this.checkForNameCollision(jobDefinitions, jobDefinition);
        if (hasNameCollision) {
            return super.createServiceResult(jobDefinition, true, "with the same name exists");
        }

        for (int i = 0; i < lengthOfDefinitions; i++) {
            JobDefinition definition = jobDefinitions.get(i);
            if (definition.getId().equals(jobDefinition.getId())) {
                jobDefinitions.set(i, jobDefinition);
                stageDefinition.setJobDefinitions(jobDefinitions);
                this.stageDefinitionService.update(stageDefinition);
                isUpdated = true;
            }
        }

        if (!isUpdated) {
            return super.createServiceResult(jobDefinition, true, "not updated successfully");
        }

        return super.createServiceResult(jobDefinition, false, "updated successfully");
    }

    @Override
    public ServiceResult delete(String jobDefinitionId) {
        boolean isRemoved = false;
        ServiceResult result;
        JobDefinition jobDefinitionToDelete = (JobDefinition) this.getById(jobDefinitionId).getObject();
        StageDefinition stageDefinition = (StageDefinition) this.stageDefinitionService
                .getById(jobDefinitionToDelete.getStageDefinitionId())
                .getObject();
        List<JobDefinition> jobDefinitions = stageDefinition.getJobDefinitions();
        int lengthOfDefinitions = jobDefinitions.size();

        if (jobDefinitions.size() > 1) {
            for (int i = 0; i < lengthOfDefinitions; i++) {
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
            result = super.createServiceResult(jobDefinitionToDelete, true, "not deleted");
            return result;

        }

        stageDefinition.setJobDefinitions(jobDefinitions);
        this.stageDefinitionService.update(stageDefinition);
        result = super.createServiceResult(jobDefinitionToDelete, false, "deleted successfully");
        return result;
    }

    @Override
    public ServiceResult getAllInStage(String stageDefinitionId) {
        StageDefinition currentStage = (StageDefinition) this.stageDefinitionService.getById(stageDefinitionId).getObject();
        List<JobDefinition> allJobDefinitionsInStage = currentStage.getJobDefinitions();
        return super.createServiceResultArray(allJobDefinitionsInStage,false,this.successMessage);
    }

    @Override
    public ServiceResult getAllInPipeline(String pipelineDefinitionId) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        List<JobDefinition> allJobsInPipeline = new ArrayList<>();
        this.extractJobDefinitions(pipelineDefinition, allJobsInPipeline);
        return super.createServiceResultArray(allJobsInPipeline,false,this.successMessage);
    }

    /**
     * Method void for extracting JobDefinitions from PipelineDefinition provided. Fills in a
     * provided List.
     */
    private void extractJobDefinitions(PipelineDefinition pipelineDefinition, List<JobDefinition> jobDefinitions) {
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
    private boolean checkForNameCollision(List<JobDefinition> jobDefinitions, JobDefinition definitionToAdd) {
        boolean hasCollision = false;
        for (JobDefinition definition : jobDefinitions) {
            if (definition.getName().equals(definitionToAdd.getName())) {
                hasCollision = true;
                return hasCollision;
            }
        }
        return hasCollision;
    }
}