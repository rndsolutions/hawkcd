package net.hawkengine.model;

import net.hawkengine.model.enums.StageStatus;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Stage extends DbEntry{
    private String stageDefinitionId;
    private String pipelineId;
    private String stageDefinitionName;
    private int executionId;
    private List<EnvironmentVariable> environmentVariables;
    private List<Job> jobs;
    private StageStatus status;
    private Date startTime;
    private Date endTime;
    private Duration duration;

    public Stage() {
        this.startTime = new Date();
        this.setEnvironmentVariables(new ArrayList<>());
        this.setJobs(new ArrayList<>());
        this.status = StageStatus.NOT_RUN;
    }

    public String getStageDefinitionId() {
        return this.stageDefinitionId;
    }

    public void setStageDefinitionId(String stageDefinitionId) {
        this.stageDefinitionId = stageDefinitionId;
    }

    public String getStageDefinitionName() {
        return this.stageDefinitionName;
    }

    public void setStageDefinitionName(String stageDefinitionName) {
        this.stageDefinitionName = stageDefinitionName;
    }

    public int getExecutionId() {
        return this.executionId;
    }

    public void setExecutionId(int executionId) {
        this.executionId = executionId;
    }

    public String getPipelineId() {
        return this.pipelineId;
    }

    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public List<EnvironmentVariable> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public void setEnvironmentVariables(List<EnvironmentVariable> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<Job> getJobs() {
        return this.jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public StageStatus getStatus() {
        return this.status;
    }

    public void setStatus(StageStatus status) {
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
}
