package net.hawkengine.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JobDefinition extends DbEntry {
    private String name;
    private String stageDefinitionId;
    private String pipelineDefinitionId;
    private List<EnvironmentVariable> environmentVariables;
    private List<TaskDefinition> taskDefinitions;
    private Set<String> resources;

    public JobDefinition() {
        this.setEnvironmentVariables(new ArrayList<>());
        this.setTaskDefinitions(new ArrayList<>());
        this.setResources(new HashSet<>());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStageDefinitionId() {
        return this.stageDefinitionId;
    }

    public void setStageDefinitionId(String stageDefinitionId) {
        this.stageDefinitionId = stageDefinitionId;
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

    public List<TaskDefinition> getTaskDefinitions() {
        return this.taskDefinitions;
    }

    public void setTaskDefinitions(List<TaskDefinition> taskDefinitions) {
        this.taskDefinitions = taskDefinitions;
    }

    public Set<String> getResources() {
        return this.resources;
    }

    public void setResources(Set<String> resources) {
        this.resources = resources;
    }
}
