package net.hawkengine.model;

import java.util.ArrayList;
import java.util.UUID;

public class WorkInfo {
    private UUID pipelineId;
    private int pipelineExecutionID;
    private String pipelineName;
    private String pipelineEnvironmentName;
    private String pipelineTriggerReason;
    private String labelTemplate;
    private String stageId;
    private int stageExecutionId;
    private String stageName;
    private String stageTriggerReason;
    private boolean shouldFetchMaterials;
    private JobDefinition job;
    private ArrayList<MaterialDefinition> materials;
    private ArrayList<EnvironmentVariable> environmentVariables;

    public WorkInfo() throws Exception {
        this.setEnvironmentVariables(new ArrayList<>());
    }

    public UUID getPipelineId() {
        return this.pipelineId;
    }

    public void setPipelineId(UUID value) {
        this.pipelineId = value;
    }

    public int getPipelineExecutionID() {
        return this.pipelineExecutionID;
    }

    public void setPipelineExecutionID(int value) {
        this.pipelineExecutionID = value;
    }

    public String getPipelineName() {
        return this.pipelineName;
    }

    public void setPipelineName(String value) {
        this.pipelineName = value;
    }

    public String getPipelineEnvironmentName() {
        return this.pipelineEnvironmentName;
    }

    public void setPipelineEnvironmentName(String value) {
        this.pipelineEnvironmentName = value;
    }

    public String getPipelineTriggerReason() {
        return this.pipelineTriggerReason;
    }

    public void setPipelineTriggerReason(String value) {
        this.pipelineTriggerReason = value;
    }

    public String getLabelTemplate() {
        return this.labelTemplate;
    }

    public void setLabelTemplate(String value) {
        this.labelTemplate = value;
    }

    public String getStageId() {
        return this.stageId;
    }

    public void setStageId(String value) {
        this.stageId = value;
    }

    public int getStageExecutionId() {
        return this.stageExecutionId;
    }

    public void setStageExecutionId(int value) {
        this.stageExecutionId = value;
    }

    public String getStageName() {
        return this.stageName;
    }

    public void setStageName(String value) {
        this.stageName = value;
    }

    public String getStageTriggerReason() {
        return this.stageTriggerReason;
    }

    public void setStageTriggerReason(String value) {
        this.stageTriggerReason = value;
    }

    public boolean getShouldFetchMaterials() {
        return this.shouldFetchMaterials;
    }

    public void setShouldFetchMaterials(boolean value) {
        this.shouldFetchMaterials = value;
    }

    public JobDefinition getJob() {
        return this.job;
    }

    public void setJob(JobDefinition value) {
        this.job = value;
    }

    public ArrayList<MaterialDefinition> getMaterials() {
        return this.materials;
    }

    public void setMaterials(ArrayList<MaterialDefinition> value) {
        this.materials = value;
    }

    public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
        this.environmentVariables = value;
    }

}
