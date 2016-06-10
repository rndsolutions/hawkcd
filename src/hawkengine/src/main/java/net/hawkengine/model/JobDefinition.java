package net.hawkengine.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class JobDefinition {
    private UUID id;
    private String name;
    private String result;
    private String executedBy;
    private ArrayList<Task> tasks;
    private ArrayList<EnvironmentVariable> environmentVariables;
    private int runInstaceCount;
    /**
     * Comma separated values in the UI;
     */
    private ArrayList<String> resources;
    /**
     * The amount of time in minutes.
     */
    private int cancelAfter;
    private Status status = Status.PASSED;
    private Date start;
    private Date end;
    private Duration duration;
    private boolean canBeExecuted;

    public JobDefinition() throws Exception {
        this.setId(UUID.randomUUID());
        this.setCanBeExecuted(true);
        this.setResources(new ArrayList<String>());
        this.setEnvironmentVariables(new ArrayList<EnvironmentVariable>());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID value) {
        id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String value) {
        result = value;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(String value) {
        executedBy = value;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> value) {
        tasks = value;
    }

    public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
        environmentVariables = value;
    }

    public int getRunInstaceCount() {
        return runInstaceCount;
    }

    public void setRunInstaceCount(int value) {
        runInstaceCount = value;
    }

    public ArrayList<String> getResources() {
        return resources;
    }

    public void setResources(ArrayList<String> value) {
        resources = value;
    }

    public int getCancelAfter() {
        return cancelAfter;
    }

    public void setCancelAfter(int value) {
        cancelAfter = value;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status value) {
        status = value;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date value) {
        start = value;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date value) {
        end = value;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration value) {
        duration = value;
    }

    public boolean getCanBeExecuted() {
        return canBeExecuted;
    }

    public void setCanBeExecuted(boolean value) {
        canBeExecuted = value;
    }
}
