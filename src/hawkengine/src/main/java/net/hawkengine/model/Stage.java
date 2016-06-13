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
        return this.id;
    }

    public void setID(UUID value) {
        this.id = value;
    }

    public int getExecutionID() {
        return this.executionID;
    }

    public void setExecutionID(int value) {
        this.executionID = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public ArrayList<JobDefinition> getJobs() {
        return this.jobs;
    }

    public void setJobs(ArrayList<JobDefinition> value) {
        this.jobs = value;
    }

    public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
        this.environmentVariables = value;
    }

    public String getSecurity() {
        return this.security;
    }

    public void setSecurity(String value) {
        this.security = value;
    }

    public boolean getFetchMaterials() {
        return this.fetchMaterials;
    }

    public void setFetchMaterials(boolean value) {
        this.fetchMaterials = value;
    }

    public boolean getNeverCleanArtifacts() {
        return this.neverCleanArtifacts;
    }

    public void setNeverCleanArtifacts(boolean value) {
        this.neverCleanArtifacts = value;
    }

    public boolean getCleanWorkingDirectory() {
        return this.cleanWorkingDirectory;
    }

    public void setCleanWorkingDirectory(boolean value) {
        this.cleanWorkingDirectory = value;
    }

    public StageType getStageType() throws Exception {
        return this.stageType;
    }

    public void setStageType(StageType value) throws Exception {
        if (value == this.getStageType().MANUAL) {
            this.setCanBeExecuted(false);
        } else {
            this.setCanBeExecuted(true);
        }
        this.stageType = value;
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
