package net.hawkengine.services;

import net.hawkengine.db.DbRepositoryFactory;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.*;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;

import java.util.List;
import java.util.stream.Collectors;

public class PipelineDefinitionService extends CrudService<PipelineDefinition> implements IPipelineDefinitionService {
    private static final Class CLASS_TYPE = PipelineDefinition.class;

    private MaterialDefinitionService materialDefinitionService;

    public PipelineDefinitionService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.materialDefinitionService = new MaterialDefinitionService();
    }

    public PipelineDefinitionService(IDbRepository repository) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    @Override
    public ServiceResult getById(String pipelineDefinitionId) {
        return super.getById(pipelineDefinitionId);
    }

    @Override
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    public ServiceResult add(PipelineDefinition pipelineDefinition) {
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
    public ServiceResult addWithMaterialDefinition(PipelineDefinition pipelineDefinition, MaterialDefinition materialDefinition) {
        ServiceResult serviceResult = this.materialDefinitionService.add(materialDefinition);
        // TODO: Return response to UI
        if (serviceResult.hasError()) {
            return super.createServiceResult(null, true, "could not be created");
        }

        pipelineDefinition.getMaterialDefinitionIds().add(materialDefinition.getId());

        return super.add(pipelineDefinition);
    }

    @Override
    public ServiceResult addWithMaterialDefinition(PipelineDefinition pipelineDefinition, String materialDefinitionId) {
        ServiceResult serviceResult = this.materialDefinitionService.getById(materialDefinitionId);
        if (serviceResult.hasError()) {
            return super.createServiceResult(null, true, "could not be created");
        }

        pipelineDefinition.getMaterialDefinitionIds().add(materialDefinitionId);

        return super.add(pipelineDefinition);
    }

    @Override
    public ServiceResult update(PipelineDefinition pipelineDefinition) {
        return super.update(pipelineDefinition);
    }

    @Override
    public ServiceResult delete(String pipelineDefinitionId) {
        return super.delete(pipelineDefinitionId);
    }

    @Override
    public ServiceResult getAllAutomaticallyScheduledPipelines() {
        ServiceResult result;
        List<PipelineDefinition> allPipelines = (List<PipelineDefinition>) this.getAll().getObject();
        List<PipelineDefinition> scheduledPipelines = allPipelines.stream().filter(p -> p.isAutoSchedulingEnabled()).collect(Collectors.toList());
        result = super.createServiceResultArray(scheduledPipelines, false, "retrieved successfully");
        return result;
    }

    @Override
    public ServiceResult unassignPipelineFromGroup(PipelineDefinition pipelineDefinition) {
        pipelineDefinition.setPipelineGroupId("");
        pipelineDefinition.setGroupName("");

        return this.update(pipelineDefinition);
    }

    @Override
    public ServiceResult assignPipelineToGroup(PipelineDefinition pipelineDefinition, PipelineGroup pipelineGroup) {
        pipelineDefinition.setPipelineGroupId(pipelineGroup.getId());
        pipelineDefinition.setGroupName(pipelineGroup.getName());

        return this.update(pipelineDefinition);
    }


}
