package net.hawkengine.model;

import net.hawkengine.model.enums.MaterialType;
import net.hawkengine.model.enums.TaskType;

import java.util.HashMap;

public class FetchMaterialTask extends Task {
    private String materialName;
    private String pipelineName;
    private MaterialType materialType = MaterialType.GIT;
    private String source;
    private String destination;
    private HashMap<String, Object> materialSpecificDetails;

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

    public HashMap<String, Object> getMaterialSpecificDetails() {
        return this.materialSpecificDetails;
    }

    public void setMaterialSpecificDetails(HashMap<String, Object> value) {
        this.materialSpecificDetails = value;
    }
}
