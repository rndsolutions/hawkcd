package io.hawkcd.model;

import java.util.List;

public class PipelineFamily extends Entity {
    protected String pipelineDefinitionId;
    protected String pipelineGroupId;
    protected List<EnvironmentVariable> environmentVariables;

    public void setPipelineGroupId(String pipelineGroupId) {
        this.pipelineGroupId = pipelineGroupId;
    }

    public String getPipelineDefinitionId() {
        return this.pipelineDefinitionId;
    }

    public void setPipelineDefinitionId(String pipelineDefinitionId){
        this.pipelineDefinitionId = pipelineDefinitionId;
    }

    public String getPipelineGroupId() {
        return this.pipelineGroupId;
    }

    public List<EnvironmentVariable> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public void setEnvironmentVariables(List<EnvironmentVariable> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }
}
