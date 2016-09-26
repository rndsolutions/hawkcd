package net.hawkengine.model.dto;

import net.hawkengine.model.Stage;
import net.hawkengine.model.enums.StageStatus;

import java.time.LocalDateTime;

public class StageDto {
    private String stageDefinitionName;
    private StageStatus status;
    private LocalDateTime endTime;

    public void constructDto(Stage stage) {
        this.stageDefinitionName = stage.getStageDefinitionName();
        this.status = stage.getStatus();
        this.endTime = stage.getEndTime();
    }
}
