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
        return pipelineName;
    }

    public void setPipelineName(String value) {
        pipelineName = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public MaterialType getType() {
        return type;
    }

    public void setType(MaterialType value) {
        type = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        url = value;
    }

    public boolean getAutoTriggerOnChange() {
        return autoTriggerOnChange;
    }

    public void setAutoTriggerOnChange(boolean value) {
        autoTriggerOnChange = value;
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
