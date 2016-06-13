package net.hawkengine.model;

import java.util.HashMap;

public class MaterialDefinition {
    private String pipelineName;
    private String name;
    private MaterialType type = MaterialType.GIT;
    private String url;
    private boolean autoTriggerOnChange;
    private String destination;
    private HashMap<String, Object> materialSpecificDetails;

    public MaterialDefinition() throws Exception {
    }

    public String getPipelineName() {
        return this.pipelineName;
    }

    public void setPipelineName(String value) {
        this.pipelineName = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public MaterialType getType() {
        return this.type;
    }

    public void setType(MaterialType value) {
        this.type = value;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String value) {
        this.url = value;
    }

    public boolean getAutoTriggerOnChange() {
        return this.autoTriggerOnChange;
    }

    public void setAutoTriggerOnChange(boolean value) {
        this.autoTriggerOnChange = value;
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
