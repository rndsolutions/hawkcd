package net.hawkengine.model.dto;

import net.hawkengine.model.enums.StageStatus;

import java.time.LocalDateTime;

public class StageDto {
    private String stageDefinitionName;
    private StageStatus status;
    private LocalDateTime endTime;

    public String getStageDefinitionName() {
        return stageDefinitionName;
    }

    public void setStageDefinitionName(String stageDefinitionName) {
        this.stageDefinitionName = stageDefinitionName;
    }

    public StageStatus getStatus() {
        return status;
    }

    public void setStatus(StageStatus status) {
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
