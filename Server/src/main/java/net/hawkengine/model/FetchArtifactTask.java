package net.hawkengine.model;

import net.hawkengine.model.enums.TaskType;

public class FetchArtifactTask extends TaskDefinition {


    private String pipelineDefinitionName;
    private String stageDefinitionName;
    private String jobDefinitionName;
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

    public String getStageDefinitionName() {
        return this.stageDefinitionName;
    }

    public void setStageDefinitionName(String value) {
        this.stageDefinitionName = value;
    }

    public String getJobDefinitionName() {
        return this.jobDefinitionName;
    }

    public void setJobDefinitionName(String value) {
        this.jobDefinitionName = value;
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
