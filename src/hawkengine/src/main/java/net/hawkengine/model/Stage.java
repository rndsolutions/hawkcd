package net.hawkengine.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Stage {
    private StageType stageType = StageType.ON_SUCCESS;
    private UUID id;
    private int executionID;
    private String name;
    private ArrayList<JobDefinition> jobs;
    private ArrayList<EnvironmentVariable> environmentVariables;
    private String security;
    private boolean fetchMaterials;
    private boolean neverCleanArtifacts;
    private boolean cleanWorkingDirectory;
    private Status status = Status.PASSED;
    private Date start;
    private Date end;
    private Duration duration;
    private boolean canBeExecuted;

    public Stage() throws Exception {
        this.setID(UUID.randomUUID());
        this.setFetchMaterials(true);
        this.setEnvironmentVariables(new ArrayList<EnvironmentVariable>());
    }

    public UUID getID() {
        return id;
    }

    public void setID(UUID value) {
        id = value;
    }

    public int getExecutionID() {
        return executionID;
    }

    public void setExecutionID(int value) {
        executionID = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public ArrayList<JobDefinition> getJobs() {
        return jobs;
    }

    public void setJobs(ArrayList<JobDefinition> value) {
        jobs = value;
    }

    public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
        environmentVariables = value;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String value) {
        security = value;
    }

    public boolean getFetchMaterials() {
        return fetchMaterials;
    }

    public void setFetchMaterials(boolean value) {
        fetchMaterials = value;
    }

    public boolean getNeverCleanArtifacts() {
        return neverCleanArtifacts;
    }

    public void setNeverCleanArtifacts(boolean value) {
        neverCleanArtifacts = value;
    }

    public boolean getCleanWorkingDirectory() {
        return cleanWorkingDirectory;
    }

    public void setCleanWorkingDirectory(boolean value) {
        cleanWorkingDirectory = value;
    }

    public StageType getStageType() throws Exception {
        return this.stageType;
    }

    public void setStageType(StageType value) throws Exception {
        if (value == getStageType().MANUAL) {
            this.setCanBeExecuted(false);
        } else {
            this.setCanBeExecuted(true);
        }
        this.stageType = value;
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
