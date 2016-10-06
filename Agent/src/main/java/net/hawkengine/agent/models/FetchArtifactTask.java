package net.hawkengine.agent.models;

import net.hawkengine.agent.enums.TaskType;

public class FetchArtifactTask extends TaskDefinition {
    private String designatedPipelineDefinitionId;
    private String designatedPipelineDefinitionName;
    private String designatedPipelineExecutionId;
    private String source;
    private String destination;
    private boolean shouldUseLatestRun;

    public FetchArtifactTask() {
        this.setType(TaskType.FETCH_ARTIFACT);
    }

    public String getDesignatedPipelineDefinitionId() {
        return designatedPipelineDefinitionId;
    }

    public void setDesignatedPipelineDefinitionId(String designatedPipelineDefinitionId) {
        this.designatedPipelineDefinitionId = designatedPipelineDefinitionId;
    }

    public String getDesignatedPipelineDefinitionName() {
        return designatedPipelineDefinitionName;
    }

    public void setDesignatedPipelineDefinitionName(String designatedPipelineDefinitionName) {
        this.designatedPipelineDefinitionName = designatedPipelineDefinitionName;
    }

    public String getDesignatedPipelineExecutionId() {
        return designatedPipelineExecutionId;
    }

    public void setDesignatedPipelineExecutionId(String designatedPipelineExecutionId) {
        this.designatedPipelineExecutionId = designatedPipelineExecutionId;
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

    public boolean shouldUseLatestRun() {
        return shouldUseLatestRun;
    }

    public void setShouldUseLatestRun(boolean shouldUseLatestRun) {
        this.shouldUseLatestRun = shouldUseLatestRun;
    }
}
