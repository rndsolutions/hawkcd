package net.hawkengine.model;

import net.hawkengine.model.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pipeline extends DbEntry {
    private String pipelineDefinitionId;
    private int executionId;
    private List<Material> materials;
    private List<EnvironmentVariable> environmentVariables;
    private List<Environment> environments;
    private List<Stage> stages;
    private List<JobDefinition> jobsForExecution;
    private Status status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;
    private String triggerReason;
    private boolean areMaterialsUpdated;
    private boolean isPrepared;

    public Pipeline() {
        this.startTime = LocalDateTime.now();
        this.setEnvironmentVariables(new ArrayList<>());
        this.setMaterials(new ArrayList<>());
        this.setEnvironments(new ArrayList<>());
        this.setStages(new ArrayList<>());
    }

    public String getPipelineDefinitionId() {
        return this.pipelineDefinitionId;
    }

    public void setPipelineDefinitionId(String pipelineDefinitionId) {
        this.pipelineDefinitionId = pipelineDefinitionId;
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

    public List<JobDefinition> getJobsForExecution() {
        return this.jobsForExecution;
    }

    public void setJobsForExecution(List<JobDefinition> jobsForExecution) {
        this.jobsForExecution = jobsForExecution;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
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

    public boolean areMaterialsUpdated() {
        return this.areMaterialsUpdated;
    }

    public void setAreMaterialsUpdated(boolean areMaterialsUpdated) {
        this.areMaterialsUpdated = areMaterialsUpdated;
    }

    public boolean isPrepared() {
        return this.isPrepared;
    }

    public void setPrepared(boolean prepared) {
        this.isPrepared = prepared;
    }
}