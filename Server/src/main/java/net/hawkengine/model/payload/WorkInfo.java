package net.hawkengine.model.payload;

import net.hawkengine.model.Job;

public class WorkInfo {
    private String pipelineDefinitionName;
    private int pipelineExecutionID;
    private String stageDefinitionName;
    private int stageExecutionID;
    private String jobDefinitionName;
    private Job job;

    public String getPipelineDefinitionName() {
        return this.pipelineDefinitionName;
    }

    public void setPipelineDefinitionName(String pipelineDefinitionName) {
        this.pipelineDefinitionName = pipelineDefinitionName;
    }

    public int getPipelineExecutionID() {
        return this.pipelineExecutionID;
    }

    public void setPipelineExecutionID(int pipelineExecutionID) {
        this.pipelineExecutionID = pipelineExecutionID;
    }

    public String getStageDefinitionName() {
        return this.stageDefinitionName;
    }

    public void setStageDefinitionName(String stageDefinitionName) {
        this.stageDefinitionName = stageDefinitionName;
    }

    public int getStageExecutionID() {
        return this.stageExecutionID;
    }

    public void setStageExecutionID(int stageExecutionID) {
        this.stageExecutionID = stageExecutionID;
    }

    public String getJobDefinitionName() {
        return this.jobDefinitionName;
    }

    public void setJobDefinitionName(String jobDefinitionName) {
        this.jobDefinitionName = jobDefinitionName;
    }

    public Job getJob() {
        return this.job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
