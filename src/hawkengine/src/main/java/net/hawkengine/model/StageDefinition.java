package net.hawkengine.model;

import java.util.ArrayList;
import java.util.List;

public class StageDefinition extends DbEntry{
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
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPipelineDefinitionId() {
        return pipelineDefinitionId;
    }

    public void setPipelineDefinitionId(String pipelineDefinitionId) {
        this.pipelineDefinitionId = pipelineDefinitionId;
    }

    public List<EnvironmentVariable> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(List<EnvironmentVariable> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<JobDefinition> getJobDefinitions() {
        return jobDefinitions;
    }

    public void setJobDefinitions(List<JobDefinition> jobDefinitions) {
        this.jobDefinitions = jobDefinitions;
    }

    public boolean isTriggeredManually() {
        return isTriggeredManually;
    }

    public void setTriggeredManually(boolean triggeredManually) {
        this.isTriggeredManually = triggeredManually;
    }
}
