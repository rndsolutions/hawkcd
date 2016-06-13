package net.hawkengine.model;

import java.util.ArrayList;
import java.util.List;

public class JobDefinition extends DbEntry {
    private String name;
    private List<TaskDefinition> tasks;
    private List<EnvironmentVariable> environmentVariables;
    private List<String> resources;

    public JobDefinition() {
        this.setResources(new ArrayList<>());
        this.setEnvironmentVariables(new ArrayList<>());
        this.setTasks(new ArrayList<>());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TaskDefinition> getTasks() {
        return this.tasks;
    }

    public void setTasks(List<TaskDefinition> tasks) {
        this.tasks = tasks;
    }

    public List<EnvironmentVariable> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public void setEnvironmentVariables(List<EnvironmentVariable> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<String> getResources() {
        return this.resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }
}
