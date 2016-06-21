package net.hawkengine.services;

import com.sun.istack.internal.Nullable;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
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
        List<JobDefinition> jobDefinitions = new ArrayList<>();
        for (PipelineDefinition definition : pipelines) {
            this.ExtractJobDefinitions(definition, jobDefinitions);
        }

        JobDefinition job = jobDefinitions
                .stream()
                .filter(jd -> jd.getId().equals(id))
                .findFirst()
                .orElse(null);

        return this.GetProperResult(job);
    }

    @Override
    public ServiceResult getAll() {
        ArrayList<PipelineDefinition> allPipelines = (ArrayList<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
        ArrayList<JobDefinition> jobDefinitions = new ArrayList<>();
        for(PipelineDefinition definition : allPipelines){
            this.ExtractJobDefinitions(definition,jobDefinitions);
        }

        return this.GetProperResult(jobDefinitions);
    }

    @Override
    public ServiceResult add(JobDefinition jobDefinition) {
        JobDefinition jobDefinitionFlag = null;
        StageDefinition currentStage = (StageDefinition) this.stageDefinitionService.getById(jobDefinition.getStageDefinitionId()).getObject();
        List<JobDefinition> jobDefinitions = currentStage.getJobDefinitions();
        boolean hasNameCollision = this.CheckForNameCollision(jobDefinitions,jobDefinition);
        if(hasNameCollision){
            return this.GetProperResult(jobDefinitionFlag);
        }

        jobDefinitions.add(jobDefinition);
        currentStage.setJobDefinitions(jobDefinitions);
        StageDefinition updatedStageDefinitions = (StageDefinition)this.stageDefinitionService.update(currentStage).getObject();
        JobDefinition result = updatedStageDefinitions
                .getJobDefinitions()
                .stream()
                .filter(jd -> jd.getId().equals(jobDefinition.getId()))
                .findFirst()
                .orElse(null);

        return this.GetProperResult(result);
    }

    @Override
    public ServiceResult update(JobDefinition jobDefinition) {
        return super.update(jobDefinition);
    }

    @Override
    public ServiceResult delete(String jobDefinitionId) {
        return super.delete(jobDefinitionId);
    }

    @Override
    public ServiceResult getAllInStage(String stageDefinitionId) {
        StageDefinition currentStage = (StageDefinition) this.stageDefinitionService.getById(stageDefinitionId).getObject();
        List<JobDefinition> allJobDefinitionsInStage = currentStage.getJobDefinitions();
        return this.GetProperResult(allJobDefinitionsInStage);
    }

    @Override
    public ServiceResult getAllInPipeline(String pipelineDefinitionId) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        List<JobDefinition> allJobsInPipeline = new ArrayList<>();
        this.ExtractJobDefinitions(pipelineDefinition, allJobsInPipeline);
        return this.GetProperResult(allJobsInPipeline);
    }

    /**
     * Method void for extracting JobDefinitions from PipelineDefinition provided. Fills in a
     * provided List.
     */
    private void ExtractJobDefinitions(PipelineDefinition pipelineDefinition, List<JobDefinition> jobDefinitions) {
        List<StageDefinition> stages = pipelineDefinition.getStageDefinitions();
        for (StageDefinition stage : stages) {
            List<JobDefinition> currentStageJobs = stage.getJobDefinitions();
            jobDefinitions.addAll(currentStageJobs);
        }
    }

    /**
     * Method accepts JobDefinition, performs null check and decides wheather to return a success
     * object or a failure object.
     */
    private ServiceResult GetProperResult(@Nullable JobDefinition jobDefinition) {
        if (jobDefinition == null) {
            return super.createServiceResult(jobDefinition, true, this.failureMessage);
        }

        return super.createServiceResult(jobDefinition, false, this.successMessage);
    }

    /**
     * Method accepts a list of JobDefinitions, performs null check and size check and decides wheather to return a
     * success or a failure object.
     */
    private ServiceResult GetProperResult(List<JobDefinition> jobDefinitions) {
        if ((jobDefinitions == null) || jobDefinitions.isEmpty()) {
            return super.createServiceResultArray(jobDefinitions, true, this.failureMessage);
        }

        return super.createServiceResultArray(jobDefinitions, false, this.successMessage);
    }

    private boolean CheckForNameCollision(List<JobDefinition> jobDefinitions,JobDefinition definitionToAdd){
        boolean hasCollision = false;
        for(JobDefinition definition : jobDefinitions){
            if(definition.getName().equals(definitionToAdd.getName())){
                hasCollision = true;
                return hasCollision;
            }
        }
        return hasCollision;
    }
}