package net.hawkengine.model;

import net.hawkengine.model.configuration.filetree.Directory;
import net.hawkengine.model.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;
    private String triggerReason;
    private boolean areMaterialsUpdated;
    private boolean isPrepared;
    private boolean shouldBeCanceled;
    private List<Directory> artifactsFileStructure;

    public Pipeline() {
        this.setStartTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        this.setEnvironmentVariables(new ArrayList<>());
        this.setMaterials(new ArrayList<>());
        this.setEnvironments(new ArrayList<>());
        this.setStages(new ArrayList<>());
        this.setArtifactsFileStructure(new ArrayList<>());
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
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

    public void setMaterialsUpdated(boolean areMaterialsUpdated) {
        this.areMaterialsUpdated = areMaterialsUpdated;
    }

    public boolean isPrepared() {
        return this.isPrepared;
    }

    public void setPrepared(boolean prepared) {
        this.isPrepared = prepared;
    }

    public boolean shouldBeCanceled() {
        return this.shouldBeCanceled;
    }

    public void setShouldBeCanceled(boolean shouldBeCanceled) {
        this.shouldBeCanceled = shouldBeCanceled;
    }

    public List<Directory> getArtifactsFileStructure() {
        return this.artifactsFileStructure;
    }

    public void setArtifactsFileStructure(List<Directory> artifactsFileStructure) {
        this.artifactsFileStructure = artifactsFileStructure;
    }
}