package net.hawkengine.agent.models;

import net.hawkengine.agent.enums.TaskType;

public class FetchArtifactTask extends TaskDefinition {
    private String pipelineDefinitionName;
    private String pipelineExecutionId;
    private String source;
    private String destination;

    public FetchArtifactTask() {
        this.setType(TaskType.FETCH_ARTIFACT);
    }

    public String getPipelineDefinitionName() {
        return pipelineDefinitionName;
    }

    public void setPipelineDefinitionName(String pipelineDefinitionName) {
        this.pipelineDefinitionName = pipelineDefinitionName;
    }

    public String getPipelineExecutionId() {
        return pipelineExecutionId;
    }

    public void setPipelineExecutionId(String pipelineExecutionId) {
        this.pipelineExecutionId = pipelineExecutionId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
