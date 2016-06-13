package net.hawkengine.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobDefinition extends DbEntry{
    private String name;
    private String output;
    private List<TaskDefinition> tasks;
    private List<EnvironmentVariable> environmentVariables;
    private List<String> resources;
    private int cancelAfter;

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

    public String getOutput() {
        return this.output;
    }

    public void setOutput(String output) {
        this.output = output;
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

    public int getCancelAfter() {
        return this.cancelAfter;
    }

    public void setCancelAfter(int cancelAfter) {
        this.cancelAfter = cancelAfter;
    }
}
