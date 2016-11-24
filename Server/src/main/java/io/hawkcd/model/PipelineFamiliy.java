package io.hawkcd.model;

import java.util.List;

/**
 * Created by Rado @radoslavMinchev, rminchev@rnd-solutions.net on 24.11.16.
 */
public class PipelineFamiliy extends Entity {

    private String pipelineDefinitionId;
    private String pipelineGroupId;

    private List<EnvironmentVariable> environmentVariables;

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
