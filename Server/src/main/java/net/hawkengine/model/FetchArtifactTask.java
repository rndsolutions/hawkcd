package net.hawkengine.model;

import net.hawkengine.model.enums.TaskType;

public class FetchArtifactTask extends TaskDefinition {
    private String designatedPipelineDefinitionId;
    private String designatedPipelineDefinitionName;
    private String designatedPipelineExecutionId;
    private String source;
    private String destination;
    private boolean shouldUseLatestRun;

    public FetchArtifactTask() {
        this.setType(TaskType.FETCH_ARTIFACT);
        this.source = "";
        this.destination = "";
    }

    public String getDesignatedPipelineDefinitionId() {
        return designatedPipelineDefinitionId;
    }

    public void setDesignatedPipelineDefinitionId(String designatedPipelineDefinitionId) {
        this.designatedPipelineDefinitionId = designatedPipelineDefinitionId;
    }

    public String getDesignatedPipelineDefinitionName() {
        return this.designatedPipelineDefinitionName;
    }

    public void setDesignatedPipelineDefinitionName(String value) {
        this.designatedPipelineDefinitionName = value;
    }

    public String getDesignatedPipelineExecutionId() {
        return this.designatedPipelineExecutionId;
    }

    public void setDesignatedPipelineExecutionId(String value) {
        this.designatedPipelineExecutionId = value;
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

    public boolean shouldUseLatestRun() {
        return shouldUseLatestRun;
    }

    public void setShouldUseLatestRun(boolean shouldUseLatestRun) {
        this.shouldUseLatestRun = shouldUseLatestRun;
    }
}
