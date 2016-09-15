package net.hawkengine.model.dto;

import net.hawkengine.model.Stage;
import java.util.List;

public class StageDto {
    private String stageDefinitionId;
    private List<Stage> stages;

    public StageDto(String stageDefinitionId, List<Stage> stages) {
        this.stageDefinitionId = stageDefinitionId;
        this.stages = stages;
    }

    public String getStageDefinitionId() {
        return this.stageDefinitionId;
    }

    public void setStageDefinitionId(String stageDefinitionId) {
        this.stageDefinitionId = stageDefinitionId;
    }

    public List<Stage> getStages() {
        return this.stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }
}
