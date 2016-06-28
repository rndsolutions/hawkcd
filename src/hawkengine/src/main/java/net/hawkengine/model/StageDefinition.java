package net.hawkengine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class StageDefinition extends DbEntry {
    private String name;
    private String pipelineDefinitionId;
    private List<EnvironmentVariable> environmentVariables;
    private List<JobDefinition> jobDefinitions;
    private boolean isTriggeredManually;

    public StageDefinition() {
        this.setEnvironmentVariables(new ArrayList<>());
        this.setJobDefinitions(new ArrayList<>());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPipelineDefinitionId() {
        return this.pipelineDefinitionId;
    }

    public void setPipelineDefinitionId(String pipelineDefinitionId) {
        this.pipelineDefinitionId = pipelineDefinitionId;
    }

    public List<EnvironmentVariable> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public void setEnvironmentVariables(List<EnvironmentVariable> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<JobDefinition> getJobDefinitions() {
        return this.jobDefinitions;
    }

    public void setJobDefinitions(List<JobDefinition> jobDefinitions) {
        this.jobDefinitions = jobDefinitions;
    }

    public boolean isTriggeredManually() {
        return this.isTriggeredManually;
    }

    @JsonProperty("isTriggeredManually")
    public void setTriggeredManually(boolean triggeredManually) {
        this.isTriggeredManually = triggeredManually;
    }
}
