package net.hawkengine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PipelineDefinition extends DbEntry {
    private String name;
    private String pipelineGroupId;
    private String groupName;
    private String labelTemplate;
    private List<MaterialDefinition> materials;
    private List<EnvironmentVariable> environmentVariables;
    private List<Environment> environments;
    private List<StageDefinition> stageDefinitions;
    private boolean isAutoSchedulingEnabled;
    private boolean isLocked;

    public PipelineDefinition() {
        this.setLabelTemplate("%COUNT%");
        this.setEnvironmentVariables(new ArrayList<>());
        this.setMaterials(new ArrayList<>());
        this.setEnvironments(new ArrayList<>());
        this.setStageDefinitions(new ArrayList<>());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPipelineGroupId() {
        return this.pipelineGroupId;
    }

    public void setPipelineGroupId(String pipelineGroupId) {
        this.pipelineGroupId = pipelineGroupId;
    }

    public String getLabelTemplate() {
        return this.labelTemplate;
    }

    public void setLabelTemplate(String labelTemplate) {
        this.labelTemplate = labelTemplate;
    }

    public List<MaterialDefinition> getMaterials() {
        return this.materials;
    }

    public void setMaterials(List<MaterialDefinition> materials) {
        this.materials = materials;
    }

    public List<EnvironmentVariable> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public void setEnvironmentVariables(List<EnvironmentVariable> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<Environment> getEnvironments() {
        return this.environments;
    }

    public void setEnvironments(List<Environment> environments) {
        this.environments = environments;
    }

    public List<StageDefinition> getStageDefinitions() {
        return this.stageDefinitions;
    }

    public void setStageDefinitions(List<StageDefinition> stageDefinitions) {
        this.stageDefinitions = stageDefinitions;
    }

    public boolean isAutoSchedulingEnabled() {
        return this.isAutoSchedulingEnabled;
    }

    @JsonProperty("isAutoSchedulingEnabled")
    public void setAutoSchedulingEnabled(boolean autoSchedulingEnabled) {
        this.isAutoSchedulingEnabled = autoSchedulingEnabled;
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    @JsonProperty("isLocked")
    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}

