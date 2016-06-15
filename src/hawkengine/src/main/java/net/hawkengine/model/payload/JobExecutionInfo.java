package net.hawkengine.model.payload;

import net.hawkengine.model.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class JobExecutionInfo {
    private String pipelineDefinitionId;
    private int pipelineExecutionId;
    private String pipelineLabel;
    private String pipelineName;
    private String executedBy;
    private UUID stageId;
    private int stageExecutionId;
    private String stageName;
    private String jobId;
    private String jobName;
    private Status status = Status.PASSED;
    private String jobStateString;
    private LocalDateTime start;
    private LocalDateTime end;
    private Duration duration;
    private String result;

    public String getPipelineId() {
        return this.pipelineDefinitionId;
    }

    public void setPipelineId(String value) {
        this.pipelineDefinitionId = value;
    }

    public int getPipelineExecutionId() {
        return this.pipelineExecutionId;
    }

    public void setPipelineExecutionId(int value) {
        this.pipelineExecutionId = value;
    }

    public String getPipelineLabel() {
        return this.pipelineLabel;
    }

    public void setPipelineLabel(String value) {
        this.pipelineLabel = value;
    }

    public String getPipelineName() {
        return this.pipelineName;
    }

    public void setPipelineName(String value) {
        this.pipelineName = value;
    }

    public String getExecutedBy() {
        return this.executedBy;
    }

    public void setExecutedBy(String value) {
        this.executedBy = value;
    }

    public UUID getStageId() {
        return this.stageId;
    }

    public void setStageId(UUID value) {
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

    public String getJobId() {
        return this.jobId;
    }

    public void setJobId(String value) {
        this.jobId = value;
    }

    public String getJobName() {
        return this.jobName;
    }

    public void setJobName(String value) {
        this.jobName = value;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status value) {
        this.status = value;
    }

    public String getJobStateString() {
        return this.jobStateString;
    }

    public void setJobStateString(String value) {
        this.jobStateString = value;
    }

    public LocalDateTime getStart() {
        return this.start;
    }

    public void setStart(LocalDateTime value) {
        this.start = value;
    }

    public LocalDateTime getEnd() {
        return this.end;
    }

    public void setEnd(LocalDateTime value) {
        this.end = value;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setDuration(Duration value) {
        this.duration = value;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String value) {
        this.result = value;
    }
}
