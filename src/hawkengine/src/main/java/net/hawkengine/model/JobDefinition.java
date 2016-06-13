package net.hawkengine.model;

import java.util.ArrayList;
import java.util.List;

public class JobDefinition extends DbEntry {
    private String name;
    private String stageDefinitionId;
    private List<EnvironmentVariable> environmentVariables;
    private List<TaskDefinition> taskDefinitions;
    private List<String> resources;

    public JobDefinition() {
        this.setEnvironmentVariables(new ArrayList<>());
        this.setTaskDefinitions(new ArrayList<>());
        this.setResources(new ArrayList<>());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStageDefinitionId() {
        return stageDefinitionId;
    }

    public void setStageDefinitionId(String stageDefinitionId) {
        this.stageDefinitionId = stageDefinitionId;
    }

    public List<EnvironmentVariable> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public void setEnvironmentVariables(List<EnvironmentVariable> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<TaskDefinition> getTaskDefinitions() {
        return taskDefinitions;
    }

    public void setTaskDefinitions(List<TaskDefinition> taskDefinitions) {
        this.taskDefinitions = taskDefinitions;
    }

    public List<String> getResources() {
        return this.resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }
}
