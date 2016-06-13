package net.hawkengine.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

public class PipelineDefinition extends DbEntry {
    private String name;
    private String pipelineGroupId;
    private String labelTemplate;
    private List<MaterialDefinition> materials;
    private List<EnvironmentVariable> environmentVariables;
    private List<Environment> environments;
    private List<StageDefinition> stages;
    private boolean isAutoSchedulingEnabled;
    private boolean isLocked;

    public PipelineDefinition() {
        this.setLabelTemplate("%COUNT%");
        this.setEnvironmentVariables(new ArrayList<>());
        this.setMaterials(new ArrayList<>());
        this.setEnvironments(new ArrayList<>());
        this.setStages(new ArrayList<>());
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

    public List<StageDefinition> getStages() {
        return this.stages;
    }

    public void setStages(List<StageDefinition> stages) {
        this.stages = stages;
    }

    public boolean isAutoSchedulingEnabled() {
        return this.isAutoSchedulingEnabled;
    }

    public void setAutoSchedulingEnabled(boolean autoSchedulingEnabled) {
        this.isAutoSchedulingEnabled = autoSchedulingEnabled;
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }
}

