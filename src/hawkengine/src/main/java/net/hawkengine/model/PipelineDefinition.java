package net.hawkengine.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

public class PipelineDefinition extends DbEntry {
    private String name;
    private String originalName;
    private int executionId;
    private String groupName;
    private String labelTemplate;
    private ArrayList<MaterialDefinition> materials;
    private ArrayList<EnvironmentVariable> environmentVariables;
    private Environment environment;
    private ArrayList<Stage> stages;
    private Status status = Status.PASSED;
    // [SD] Execution contexts props
    private Date start;
    private Date end;
    private Duration duration;
    private ArrayList<MaterialChange> executionMaterials;
    private String triggerReason;
    private boolean autoScheduling;
    private boolean isLocked;

    public PipelineDefinition() {
        this.setLabelTemplate("%COUNT%");
        this.setEnvironmentVariables(new ArrayList<EnvironmentVariable>());
    }

    public int getExecutionId() {
        return this.executionId;
    }

    public void setExecutionId(int value) {
        this.executionId = value;
    }

    public String getOriginalName() {
        return this.originalName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        if (this.name != null) {
            this.originalName = value;
        } else {
            this.originalName = this.name;
        }
        this.name = value;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String value) {
        this.groupName = value;
    }

    public String getLabelTemplate() {
        return this.labelTemplate;
    }

    public void setLabelTemplate(String value) {
        this.labelTemplate = value;
    }

    public ArrayList<MaterialDefinition> getMaterials() {
        return this.materials;
    }

    public void setMaterials(ArrayList<MaterialDefinition> value) {
        this.materials = value;
    }

    public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
        this.environmentVariables = value;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(Environment value) {
        this.environment = value;
    }

    public ArrayList<Stage> getStages() {
        return this.stages;
    }

    public void setStages(ArrayList<Stage> value) {
        this.stages = value;
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

    public ArrayList<MaterialChange> getExecutionMaterials() {
        return this.executionMaterials;
    }

    public void setExecutionMaterials(ArrayList<MaterialChange> value) {
        this.executionMaterials = value;
    }

    public String getTriggerReason() {
        return this.triggerReason;
    }

    public void setTriggerReason(String value) {
        this.triggerReason = value;
    }

    public boolean getAutoScheduling() {
        return this.autoScheduling;
    }

    public void setAutoScheduling(boolean value) {
        this.autoScheduling = value;
    }

    public boolean getIsLocked() {
        return this.isLocked;
    }

    public void setIsLocked(boolean value) {
        this.isLocked = value;
    }

    public Stage getStageById(UUID id) throws Exception {

        throw new OperationNotSupportedException("not implemented");
        /*
         * if (getStages() != null) { Stage stage = getStages().Where(
		 * [UNSUPPORTED] to translate lambda expressions we need an explicit
		 * delegate type, try adding a cast "(s) => { return s.ID == id; }"
		 * ).FirstOrDefault(); if (stage == null) throw new
		 * IllegalArgumentException(String.format(String.format(
		 * "Stage wiht ID {0} was not found in PipelineDefinition with ID {1}"
		 * ),id,base.ID.toString()));
		 * 
		 * return stage; } throw new NullReferenceException(String.Format(
		 * "Stages collection is null"));
		 */
    }

    public JobDefinition getJobById(UUID stageID, UUID jobID) throws Exception {

        throw new OperationNotSupportedException("not implemented");
		/*
		 * Stage stage = getStageById(stageID); if (stage.getJobs() != null) {
		 * JobDefinition job = stage.getJobs().Where( [UNSUPPORTED] to translate lambda
		 * expressions we need an explicit delegate type, try adding a cast "(j)
		 * => { return j.Id == jobID; }" ).FirstOrDefault(); if (job == null)
		 * throw new
		 * ArgumentException(String.format(StringSupport.CSFmtStrToJFmtStr(
		 * "JobDefinition wiht ID {0} was not found in Stage with ID {1}"
		 * ),jobID,stageID));
		 * 
		 * return job; }
		 * 
		 * throw new
		 * NullReferenceException(String.format(StringSupport.CSFmtStrToJFmtStr(
		 * "Jobs collection of Stage {0} is null"),stageID));
		 */
    }

    public Task getTaskById(UUID stageID, UUID jobID, UUID taskID) throws Exception {

        throw new OperationNotSupportedException("not implemented");
		/*
		 * JobDefinition job = getJobById(stageID,jobID); if (job.getTasks() != null) {
		 * Task task = job.getTasks().Where( [UNSUPPORTED] to translate
		 * lambda expressions we need an explicit delegate type, try adding a
		 * cast "(j) => { return j.Id == taskID; }" ).FirstOrDefault(); if (task
		 * == null) throw new
		 * ArgumentException(String.format(StringSupport.CSFmtStrToJFmtStr(
		 * "Task with ID {0} was not found in JobDefinition with ID {1}"),taskID,jobID));
		 * 
		 * return task; } throw new
		 * NullReferenceException(String.format(StringSupport.CSFmtStrToJFmtStr(
		 * "Tasks collection of JobDefinition {0} is null"),stageID));
		 */
    }

    public void resetIdentifier() throws Exception {
        this.originalName = this.name;
    }

}
