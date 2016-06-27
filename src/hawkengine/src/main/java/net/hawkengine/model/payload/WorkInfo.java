package net.hawkengine.model.payload;

import net.hawkengine.model.EnvironmentVariable;
import net.hawkengine.model.Job;
import net.hawkengine.model.Material;
import java.util.List;
import java.util.UUID;

public class WorkInfo {
    private UUID pipelineId;
    private int pipelineExecutionID;
    private String pipelineName;
    private String pipelineEnvironmentName;
    private String pipelineTriggerReason;
    private String labelTemplate;
    private UUID stageId;
    private int stageExecutionID;
    private String stageName;
    private String stageTriggerReason;
    private boolean shouldFetchMaterials;
    private Job job;
    private List<Material> materials;
    private List<EnvironmentVariable> environmentVariables;

    public UUID getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(UUID pipelineId) {
        this.pipelineId = pipelineId;
    }

    public int getPipelineExecutionID() {
        return pipelineExecutionID;
    }

    public void setPipelineExecutionID(int pipelineExecutionID) {
        this.pipelineExecutionID = pipelineExecutionID;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public String getPipelineEnvironmentName() {
        return pipelineEnvironmentName;
    }

    public void setPipelineEnvironmentName(String pipelineEnvironmentName) {
        this.pipelineEnvironmentName = pipelineEnvironmentName;
    }

    public String getPipelineTriggerReason() {
        return pipelineTriggerReason;
    }

    public void setPipelineTriggerReason(String pipelineTriggerReason) {
        this.pipelineTriggerReason = pipelineTriggerReason;
    }

    public String getLabelTemplate() {
        return labelTemplate;
    }

    public void setLabelTemplate(String labelTemplate) {
        this.labelTemplate = labelTemplate;
    }

    public UUID getStageId() {
        return stageId;
    }

    public void setStageId(UUID stageId) {
        this.stageId = stageId;
    }

    public int getStageExecutionID() {
        return stageExecutionID;
    }

    public void setStageExecutionID(int stageExecutionID) {
        this.stageExecutionID = stageExecutionID;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getStageTriggerReason() {
        return stageTriggerReason;
    }

    public void setStageTriggerReason(String stageTriggerReason) {
        this.stageTriggerReason = stageTriggerReason;
    }

    public boolean isShouldFetchMaterials() {
        return shouldFetchMaterials;
    }

    public void setShouldFetchMaterials(boolean shouldFetchMaterials) {
        this.shouldFetchMaterials = shouldFetchMaterials;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public List<EnvironmentVariable> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(List<EnvironmentVariable> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }
}
