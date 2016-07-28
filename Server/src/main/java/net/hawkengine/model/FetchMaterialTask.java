package net.hawkengine.model;

import net.hawkengine.model.enums.MaterialType;
import net.hawkengine.model.enums.TaskType;

public class FetchMaterialTask extends TaskDefinition {
    private String materialName;
    private String pipelineName;
    private MaterialType materialType;
    private String source;
    private String destination;
    private MaterialDefinition materialDefinition;

    public FetchMaterialTask() {
        this.setType(TaskType.FETCH_MATERIAL);
    }

    public String getMaterialName() {
        return this.materialName;
    }

    public void setMaterialName(String value) {
        this.materialName = value;
    }

    public String getPipelineName() {
        return this.pipelineName;
    }

    public void setPipelineName(String value) {
        this.pipelineName = value;
    }

    public MaterialType getMaterialType() {
        return this.materialType;
    }

    public void setMaterialType(MaterialType value) {
        this.materialType = value;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String value) {
        this.source = value;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String value) {
        this.destination = value;
    }

    public MaterialDefinition getMaterialDefinition() {
        return this.materialDefinition;
    }

    public void setMaterialDefinition(MaterialDefinition materialDefinition) {
        this.materialDefinition = materialDefinition;
    }
}
