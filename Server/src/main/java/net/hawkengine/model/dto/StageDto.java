package net.hawkengine.model.dto;

import java.util.List;

public class StageDto {
    private String stageDefinitionId;
    private String pipelineId;
    private int executionId;
    private List<String> jobDefinitionIds;

    public String getStageDefinitionId() {
        return stageDefinitionId;
    }

    public void setStageDefinitionId(String stageDefinitionId) {
        this.stageDefinitionId = stageDefinitionId;
    }

    public String getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public int getExecutionId() {
        return executionId;
    }

    public void setExecutionId(int executionId) {
        this.executionId = executionId;
    }

    public List<String> getJobDefinitionIds() {
        return this.jobDefinitionIds;
    }

    public void setJobDefinitionIds(List<String> jobDefinitionIds) {
        this.jobDefinitionIds = jobDefinitionIds;
    }
}
