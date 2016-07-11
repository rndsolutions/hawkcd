package net.hawkengine.agent.models;

import net.hawkengine.agent.enums.MaterialType;

import java.util.Map;

public class FetchMaterialTask extends TaskDefinition {
    private String materialName;
    private String pipelineName;
    private MaterialType materialType;
    private String source;
    private String destination;
    private Map<String, Object> materialSpecificDetails;

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Map<String, Object> getMaterialSpecificDetails() {
        return materialSpecificDetails;
    }

    public void setMaterialSpecificDetails(Map<String, Object> materialSpecificDetails) {
        this.materialSpecificDetails = materialSpecificDetails;
    }
}
