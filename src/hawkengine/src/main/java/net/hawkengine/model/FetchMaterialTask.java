package net.hawkengine.model;

import java.util.HashMap;

public class FetchMaterialTask extends Task {
    private String materialName;
    private String pipelineName;
    private MaterialType materialType = MaterialType.GIT;
    private String source;
    private String destination;
    private HashMap<String, Object> materialSpecificDetails;

    public FetchMaterialTask() throws Exception {
        this.setType(TaskType.FETCH_MATERIAL);
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String value) {
        materialName = value;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String value) {
        pipelineName = value;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public void setMaterialType(MaterialType value) {
        materialType = value;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String value) {
        source = value;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String value) {
        destination = value;
    }

    public HashMap<String, Object> getMaterialSpecificDetails() {
        return materialSpecificDetails;
    }

    public void setMaterialSpecificDetails(HashMap<String, Object> value) {
        materialSpecificDetails = value;
    }
}
