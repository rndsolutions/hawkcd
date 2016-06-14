package net.hawkengine.model;

import java.util.HashMap;

@SuppressWarnings("UnqualifiedFieldAccess")
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
        return this.pipelineDefinitionId;
    }

    public void setPipelineDefinitionId(String value) {
        this.pipelineDefinitionId = value;
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

    public HashMap<String, Object> getMaterialSpecificDetails() {
        return this.materialSpecificDetails;
    }

    public void setMaterialSpecificDetails(HashMap<String, Object> value) {
        this.materialSpecificDetails = value;
    }

}
