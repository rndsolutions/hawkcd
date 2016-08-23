package net.hawkengine.model;

import java.time.LocalDateTime;

public class Material extends DbEntry {
    private LocalDateTime changeDate;
    private MaterialDefinition materialDefinition;
    private String pipelineDefinitionId;
    private boolean isUpdated;

    public LocalDateTime getChangeDate() {
        return this.changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public MaterialDefinition getMaterialDefinition() {
        return this.materialDefinition;
    }

    public void setMaterialDefinition(MaterialDefinition materialDefinition) {
        this.materialDefinition = materialDefinition;
    }

    public String getPipelineDefinitionId() {
        return pipelineDefinitionId;
    }

    public void setPipelineDefinitionId(String pipelineDefinitionId) {
        this.pipelineDefinitionId = pipelineDefinitionId;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }
}
