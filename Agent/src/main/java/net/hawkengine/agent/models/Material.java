package net.hawkengine.agent.models;

import net.hawkengine.agent.enums.MaterialType;

import java.util.Map;

public class Material {
    private String pipelineName;
    private String name;
    private MaterialType type;
    private String url;
    private Boolean autoTriggerOnChange;
    private String destination;
    private Map<String, Object> materialSpecificDetails;

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MaterialType getType() {
        return type;
    }

    public void setType(MaterialType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getAutoTriggerOnChange() {
        return autoTriggerOnChange;
    }

    public void setAutoTriggerOnChange(Boolean autoTriggerOnChange) {
        this.autoTriggerOnChange = autoTriggerOnChange;
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