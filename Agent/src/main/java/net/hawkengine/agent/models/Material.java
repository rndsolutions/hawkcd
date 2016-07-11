package net.hawkengine.agent.models;

import net.hawkengine.model.MaterialDefinition;

import java.time.LocalDateTime;

public class Material {
    private LocalDateTime changeDate;
    private MaterialDefinition materialDefinition;

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
}