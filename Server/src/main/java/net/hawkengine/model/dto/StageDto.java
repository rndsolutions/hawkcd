package net.hawkengine.model.dto;

import net.hawkengine.model.Stage;
import java.util.List;

public class StageDto {
    private Stage stage;
    private List<String> jobDefinitionIds;

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public List<String> getJobDefinitionIds() {
        return this.jobDefinitionIds;
    }

    public void setJobDefinitionIds(List<String> jobDefinitionIds) {
        this.jobDefinitionIds = jobDefinitionIds;
    }
}
