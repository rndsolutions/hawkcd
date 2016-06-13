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
        return this.id;
    }

    public void setId(UUID value) {
        this.id = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String value) {
        this.result = value;
    }

    public String getExecutedBy() {
        return this.executedBy;
    }

    public void setExecutedBy(String value) {
        this.executedBy = value;
    }

    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(ArrayList<Task> value) {
        this.tasks = value;
    }

    public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
        this.environmentVariables = value;
    }

    public int getRunInstaceCount() {
        return this.runInstaceCount;
    }

    public void setRunInstaceCount(int value) {
        this.runInstaceCount = value;
    }

    public ArrayList<String> getResources() {
        return this.resources;
    }

    public void setResources(ArrayList<String> value) {
        this.resources = value;
    }

    public int getCancelAfter() {
        return this.cancelAfter;
    }

    public void setCancelAfter(int value) {
        this.cancelAfter = value;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status value) {
        this.status = value;
    }

    public Date getStart() {
        return this.start;
    }

    public void setStart(Date value) {
        this.start = value;
    }

    public Date getEnd() {
        return this.end;
    }

    public void setEnd(Date value) {
        this.end = value;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setDuration(Duration value) {
        this.duration = value;
    }

    public boolean getCanBeExecuted() {
        return this.canBeExecuted;
    }

    public void setCanBeExecuted(boolean value) {
        this.canBeExecuted = value;
    }
}
