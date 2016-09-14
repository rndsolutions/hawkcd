package net.hawkengine.model;

import net.hawkengine.model.enums.TaskType;

public class FetchArtifactTask extends TaskDefinition {


    private String pipelineDefinitionName;
    private String pipelineExecutionId;
    private String source;
    private String destination;

    public FetchArtifactTask() {
        this.setType(TaskType.FETCH_ARTIFACT);
    }

    public String getPipelineDefinitionName() {
        return this.pipelineDefinitionName;
    }

    public void setPipelineDefinitionName(String value) {
        this.pipelineDefinitionName = value;
    }

    public String getPipelineExecutionId() {
        return this.pipelineExecutionId;
    }

    public void setPipelineExecutionId(String value) {
        this.pipelineExecutionId = value;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String value) {
        this.source = value;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String value) {
        this.destination = value;
    }
}
