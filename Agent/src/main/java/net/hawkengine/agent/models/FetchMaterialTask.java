package net.hawkengine.agent.models;

import net.hawkengine.agent.enums.MaterialType;
import net.hawkengine.model.MaterialDefinition;

import java.util.Map;

public class FetchMaterialTask extends TaskDefinition {
    private String materialName;
    private String pipelineName;
    private MaterialType materialType;
    private String source;
    private String destination;
    private MaterialDefinition materialDefinition;

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

    public MaterialDefinition getMaterialDefinition() {
        return this.materialDefinition;
    }

    public void setMaterialDefinition(MaterialDefinition materialDefinition) {
        this.materialDefinition = materialDefinition;
    }
}
