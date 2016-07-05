package net.hawkengine.services;

import net.hawkengine.model.Pipeline;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.Stage;
import net.hawkengine.services.interfaces.IStageService;
import java.util.ArrayList;
import java.util.List;


public class StageService  extends CrudService<Stage> implements IStageService {
    private PipelineService pipelineService;
    private String failureMessage = "not found.";
    private String successMessage = "retrieved successfully.";


    public StageService(){
        super.setObjectType("Stage");
        this.pipelineService = new PipelineService();
    }

    @Override
    public ServiceResult getById(String stageId) {
        List<Pipeline> allPipelines = (List<Pipeline>) this.pipelineService.getAll().getObject();
        Stage result = null;
        for (Pipeline pipeline : allPipelines){
            List<Stage> stages = pipeline.getStages();
            for (Stage stage : stages){
                if (stage.getStageDefinitionId().equals(stageId)){
                    result = stage;
                    return super.createServiceResult(result,false,this.successMessage);
                }
            }
        }
        return super.createServiceResult(result,true,this.failureMessage);
    }

    @Override
    public ServiceResult getAll() {
        List<Pipeline> allPipelines = (List<Pipeline>) this.pipelineService.getAll().getObject();
        List<Stage> allStages = new ArrayList<>();

        for (Pipeline pipeline : allPipelines){
            List<Stage> stages = pipeline.getStages();
            allStages.addAll(stages);
        }
        return super.createServiceResultArray(allStages,false,this.successMessage);
    }

    @Override
    public ServiceResult add(Stage stage) {
        Pipeline pipeline = (Pipeline) this.pipelineService.getById(stage.getPipelineId()).getObject();
        Stage result = null;
        List<Stage> stages = pipeline.getStages();
        stages.add(stage);
        pipeline.setStages(stages);
        Pipeline updatedPipeline = (Pipeline) this.pipelineService.update(pipeline).getObject();
        List<Stage> updatedStages = updatedPipeline.getStages();
        for (Stage currentStage : updatedStages){
            if (currentStage.getStageDefinitionId().equals(stage.getId())){
                result = currentStage;
            }
        }
        if (result == null){
            return super.createServiceResult(result,true, "not added successfully.");
        }
        return super.createServiceResult(result,false,this.successMessage);
    }

    @Override
    public ServiceResult update(Stage stage) {
        Stage result = null;
        Pipeline pipeline = (Pipeline)this.pipelineService.getById(stage.getPipelineId()).getObject();
        List <Stage> stageList = pipeline.getStages();
        int stageListSize = stageList.size();
        for (int i = 0; i < stageListSize; i++){
            result = stageList.get(i);
            if (result.getStageDefinitionId().equals(stage.getPipelineId())){
                stageList.set(i,stage);
                pipeline.setStages(stageList);
                Pipeline updatedPipeline = (Pipeline)this.pipelineService.update(pipeline).getObject();
                List<Stage> updatedStages = updatedPipeline.getStages();
                for (Stage currentStage : updatedStages){
                    if (currentStage.getStageDefinitionId().equals(stage.getId()))
                        result = currentStage;
                        break;
                }
            }
        }

        if (result == null){
            return super.createServiceResult(result, true, "not updated successfully.");
        }
        return super.createServiceResult(result,false, "updated successfully");
    }


    //Sorry for this fucked up shit, iztrii me sled kato si me prochel.
    @Override
    public ServiceResult delete(String stageId) {
        boolean isRemoved = false;
        Pipeline  pipelineToBeDeletedFrom = null;
        Stage stageToDelete = null;
        List<Pipeline> pipelines = (List<Pipeline>) this.pipelineService.getAll().getObject();
        for (Pipeline pipeline : pipelines){
            List<Stage> currentStage = pipeline.getStages();
            for (Stage stage : currentStage){
                if (stage.getStageDefinitionId().equals(stageId)){
                    stageToDelete = stage;
                    pipelineToBeDeletedFrom = (Pipeline)
                            this.pipelineService.getById(stage.getPipelineId()).getObject();

                }
            }
        }

        if (stageToDelete == null) {
            return super.createServiceResult(stageToDelete,true,"does not exist.");
        }

        List <Stage> pipelineStageList = pipelineToBeDeletedFrom.getStages();
        int stageSize = pipelineStageList.size();
        if (stageSize > 1){
            for (int i = 0; i < stageSize; i++){
                stageToDelete = pipelineStageList.get(i);
                if (stageToDelete.getStageDefinitionId().equals(stageId)){
                    pipelineStageList.remove(stageToDelete);
                    isRemoved = true;
                    break;
                }
            }
        }

        pipelineToBeDeletedFrom.setStages(pipelineStageList);
        this.pipelineService.update(pipelineToBeDeletedFrom).getObject();

        if (!isRemoved){
            return super.createServiceResult(null,true,"not deleted.");
        }

        return super.createServiceResult(stageToDelete,false,this.successMessage);
    }
}