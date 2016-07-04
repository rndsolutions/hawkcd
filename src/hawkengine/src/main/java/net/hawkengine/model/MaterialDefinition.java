package net.hawkengine.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import net.hawkengine.model.enums.MaterialType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = GitMaterial.class, name = "GIT"), @JsonSubTypes.Type(value = NugetMaterial.class, name = "NUGET") })
public abstract class MaterialDefinition extends DbEntry{
    private String pipelineDefinitionId;
    private String pipelineDefinitionName;
    private String name;
    private MaterialType type;
    private boolean isPollingForChanges;

    public String getPipelineDefinitionId() {
        return this.pipelineDefinitionId;
    }

    public void setPipelineDefinitionId(String value) {
        this.pipelineDefinitionId = value;
    }

    public String getPipelineDefinitionName() {
        return this.pipelineDefinitionName;
    }

    public void setPipelineDefinitionName(String pipelineDefinitionName) {
        this.pipelineDefinitionName = pipelineDefinitionName;
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

    public boolean isPollingForChanges() {
        return this.isPollingForChanges;
    }

    public void setPollingForChanges(boolean pollingForChanges) {
        this.isPollingForChanges = pollingForChanges;
    }
}
