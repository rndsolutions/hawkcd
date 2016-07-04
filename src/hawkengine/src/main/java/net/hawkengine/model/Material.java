package net.hawkengine.model;

import java.time.LocalDateTime;

public class Material extends DbEntry {
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
