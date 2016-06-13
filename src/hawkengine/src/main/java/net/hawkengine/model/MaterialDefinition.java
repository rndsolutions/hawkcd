package net.hawkengine.model;

import java.util.HashMap;

public class MaterialDefinition {
    private String pipelineDefinitionId;
    private String name;
    private MaterialType type;
    private String url;
    private boolean autoTriggerOnChange;
    private HashMap<String, Object> materialSpecificDetails;

    public MaterialDefinition() {
    }

    public String getPipelineDefinitionId() {
        return pipelineDefinitionId;
    }

    public void setPipelineDefinitionId(String value) {
        pipelineDefinitionId = value;
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

    public HashMap<String, Object> getMaterialSpecificDetails() {
        return materialSpecificDetails;
    }

    public void setMaterialSpecificDetails(HashMap<String, Object> value) {
        materialSpecificDetails = value;
    }

}
