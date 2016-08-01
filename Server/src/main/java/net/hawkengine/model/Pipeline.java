package net.hawkengine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.hawkengine.model.enums.Status;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pipeline extends DbEntry {
    private String pipelineDefinitionId;
    private String pipelineDefinitionName;
    private int executionId;
    private List<Material> materials;
    private List<EnvironmentVariable> environmentVariables;
    private List<Environment> environments;
    private List<Stage> stages;
    private Status status;
    private Date startTime;
    private Date endTime;
    private Duration duration;
    private String triggerReason;
    private boolean areMaterialsUpdated;
    private boolean isPrepared;

    public Pipeline() {
        this.startTime = new Date();
        this.setEnvironmentVariables(new ArrayList<>());
        this.setMaterials(new ArrayList<>());
        this.setEnvironments(new ArrayList<>());
        this.setStages(new ArrayList<>());
        this.status = Status.IN_PROGRESS;
    }

    public String getPipelineDefinitionId() {
        return this.pipelineDefinitionId;
    }

    public void setPipelineDefinitionId(String pipelineDefinitionId) {
        this.pipelineDefinitionId = pipelineDefinitionId;
    }

    public String getPipelineDefinitionName() {
        return pipelineDefinitionName;
    }

    public void setPipelineDefinitionName(String pipelineDefinitionName) {
        this.pipelineDefinitionName = pipelineDefinitionName;
    }

    public int getExecutionId() {
        return this.executionId;
    }

    public void setExecutionId(int executionId) {
        this.executionId = executionId;
    }

    public List<Material> getMaterials() {
        return this.materials;
    }

    public void setMaterials(List<Material> materials) {
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

    public List<Stage> getStages() {
        return this.stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getTriggerReason() {
        return this.triggerReason;
    }

    public void setTriggerReason(String triggerReason) {
        this.triggerReason = triggerReason;
    }

    @JsonProperty("areMaterialsUpdated")
    public boolean areMaterialsUpdated() {
        return this.areMaterialsUpdated;
    }

    @JsonProperty("areMaterialsUpdated")
    public void setMaterialsUpdated(boolean areMaterialsUpdated) {
        this.areMaterialsUpdated = areMaterialsUpdated;
    }

    @JsonProperty("isPrepared")
    public boolean isPrepared() {
        return this.isPrepared;
    }

    @JsonProperty("isPrepared")
    public void setPrepared(boolean prepared) {
        this.isPrepared = prepared;
    }
}