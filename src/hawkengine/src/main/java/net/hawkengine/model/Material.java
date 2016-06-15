package net.hawkengine.model;

import net.hawkengine.model.enums.MaterialType;

import java.time.LocalDateTime;
import java.util.HashMap;

public class Material extends DbEntry {
    private String pipelineName;
    private String materialName;
    private String url;
    private MaterialType type;
    private LocalDateTime changeDate;
    private HashMap<String, Object> materialSpecificDetails;

    public String getPipelineName() {
        return this.pipelineName;
    }

    public void setPipelineName(String value) {
        this.pipelineName = value;
    }

    public String getMaterialName() {
        return this.materialName;
    }

    public void setMaterialName(String value) {
        this.materialName = value;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String value) {
        this.url = value;
    }

    public MaterialType getType() {
        return this.type;
    }

    public void setType(MaterialType value) {
        this.type = value;
    }

    public LocalDateTime getChangeDate() {
        return this.changeDate;
    }

    public void setChangeDate(LocalDateTime value) {
        this.changeDate = value;
    }

    public HashMap<String, Object> getMaterialSpecificDetails() {
        return this.materialSpecificDetails;
    }

    public void setMaterialSpecificDetails(HashMap<String, Object> value) {
        this.materialSpecificDetails = value;
    }
}
