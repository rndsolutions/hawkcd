package net.hawkengine.services;

import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IStageDefinitionService;

import java.util.ArrayList;
import java.util.List;

public class StageDefinitionService extends Service<StageDefinition> implements IStageDefinitionService {
    private IPipelineDefinitionService pipelineDefinitionService;

    public StageDefinitionService() {
        this.pipelineDefinitionService = new PipelineDefinitionService();
        super.setObjectType("StageDefinition");
    }

    public StageDefinitionService(IPipelineDefinitionService pipelineDefinitionService) {
        this.pipelineDefinitionService = pipelineDefinitionService;
    }

    @Override
    public ServiceResult getById(String stageDefinitionId, String pipelineDefinitionId) {
        ServiceResult serviceResult = new ServiceResult();
        PipelineDefinition pipelineFromDatabase = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        List<StageDefinition> stageDefinitions = pipelineFromDatabase.getStageDefinitions();

        for (StageDefinition stageDefinition : stageDefinitions) {
            if (stageDefinition.getId().equals(stageDefinitionId)) {
                serviceResult = super.createServiceResult(stageDefinition, false, "retrieved successfully");
                return serviceResult;
            } else {
                serviceResult = super.createServiceResult((StageDefinition) serviceResult.getObject(), true, "not found");
            }
        }

        return serviceResult;
    }

    @Override
    public ServiceResult getAll(String pipelineDefinitionId) {
        PipelineDefinition pipelineFromDatabase = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        List<StageDefinition> stageDefinitions = pipelineFromDatabase.getStageDefinitions();

        ServiceResult result = super.createServiceResultArray(stageDefinitions, false, "retrieved successfully");

        return result;
    }

    @Override
    public ServiceResult add(StageDefinition stageDefinition) {
        String pipelineDefinitionId = stageDefinition.getPipelineDefinitionId();
        PipelineDefinition pipelineFromDatabase = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        List<StageDefinition> stageDefinitions = pipelineFromDatabase.getStageDefinitions();
        stageDefinitions.add(stageDefinition);
        pipelineFromDatabase.setStageDefinitions(stageDefinitions);

        ServiceResult serviceResult = this.pipelineDefinitionService.update(pipelineFromDatabase);

        ServiceResult result = new ServiceResult();
        if (serviceResult.hasError()) {
            result = super.createServiceResult((StageDefinition) result.getObject(), true, "already exists");
        } else {
            result = super.createServiceResult(stageDefinition, false, "created successfully");
        }
        return result;
    }

    @Override
    public ServiceResult update(StageDefinition stageDefinition) {
        String pipelineDefinitionId = stageDefinition.getPipelineDefinitionId();
        PipelineDefinition pipelineFromDatabase = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();

        ServiceResult serviceResult = this.getById(stageDefinition.getId(), pipelineDefinitionId);

        if (serviceResult.hasError()) {
            return super.createServiceResult(stageDefinition, true, "not found");
        } else {
            List<StageDefinition> stageDefinitions = pipelineFromDatabase.getStageDefinitions();
            List<StageDefinition> stageDefinitionsToBeAdded = new ArrayList<>();

            for (StageDefinition stageDefinitionFromDatabase : stageDefinitions) {
                if (stageDefinitionFromDatabase.getId().equals(stageDefinition.getId())) {
                    stageDefinitionFromDatabase = stageDefinition;
                    stageDefinitionsToBeAdded.add(stageDefinition);
                } else {
                    stageDefinitionsToBeAdded.add(stageDefinitionFromDatabase);
                }
            }

                pipelineFromDatabase.setStageDefinitions(stageDefinitionsToBeAdded);

                this.pipelineDefinitionService.update(pipelineFromDatabase);
                serviceResult = super.createServiceResult(stageDefinition, false, "updated successfully");
                return serviceResult;
        }
    }

    @Override
    public ServiceResult delete(String stageDefinitionId, String pipelineDefinitionId) {
        PipelineDefinition pipelineFromDatabase = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();

        ServiceResult serviceResult = this.getById(stageDefinitionId, pipelineDefinitionId);

        StageDefinition stageDefinition = (StageDefinition) serviceResult.getObject();

        if (serviceResult.hasError()) {
            return super.createServiceResult(stageDefinition, true, "not found");
        } else {
            List<StageDefinition> stageDefinitions = pipelineFromDatabase.getStageDefinitions();
            List<StageDefinition> stageDefinitionsToBeAdded = new ArrayList<>();

            for (StageDefinition stageDefinitionFromDatabase : stageDefinitions) {
                if (stageDefinitionFromDatabase.getId().equals(stageDefinition.getId())) {
                    stageDefinitionFromDatabase = stageDefinition;
                } else {
                    stageDefinitionsToBeAdded.add(stageDefinitionFromDatabase);
                }
            }

            pipelineFromDatabase.setStageDefinitions(stageDefinitionsToBeAdded);

            this.pipelineDefinitionService.update(pipelineFromDatabase);
            serviceResult = super.createServiceResult(stageDefinition, false, "deleted successfully");
            return serviceResult;
        }
    }
}
