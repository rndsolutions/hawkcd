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
        this.setEnvironmentVariables(new ArrayList<EnvironmentVariable>());
    }

    public UUID getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(UUID value) {
        pipelineId = value;
    }

    public int getPipelineExecutionID() {
        return pipelineExecutionID;
    }

    public void setPipelineExecutionID(int value) {
        pipelineExecutionID = value;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String value) {
        pipelineName = value;
    }

    public String getPipelineEnvironmentName() {
        return pipelineEnvironmentName;
    }

    public void setPipelineEnvironmentName(String value) {
        pipelineEnvironmentName = value;
    }

    public String getPipelineTriggerReason() {
        return pipelineTriggerReason;
    }

    public void setPipelineTriggerReason(String value) {
        pipelineTriggerReason = value;
    }

    public String getLabelTemplate() {
        return labelTemplate;
    }

    public void setLabelTemplate(String value) {
        labelTemplate = value;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String value) {
        stageId = value;
    }

    public int getStageExecutionId() {
        return stageExecutionId;
    }

    public void setStageExecutionId(int value) {
        stageExecutionId = value;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String value) {
        stageName = value;
    }

    public String getStageTriggerReason() {
        return stageTriggerReason;
    }

    public void setStageTriggerReason(String value) {
        stageTriggerReason = value;
    }

    public boolean getShouldFetchMaterials() {
        return shouldFetchMaterials;
    }

    public void setShouldFetchMaterials(boolean value) {
        shouldFetchMaterials = value;
    }

    public JobDefinition getJob() {
        return job;
    }

    public void setJob(JobDefinition value) {
        job = value;
    }

    public ArrayList<MaterialDefinition> getMaterials() {
        return materials;
    }

    public void setMaterials(ArrayList<MaterialDefinition> value) {
        materials = value;
    }

    public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
        environmentVariables = value;
    }

}
