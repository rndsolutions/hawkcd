//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

import javax.naming.OperationNotSupportedException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PipelineDefinition extends DbEntry {
    private String name;
    private String originalName;
    private int __ExecutionID;
    private String __GroupName;
    private String __LabelTemplate;
    private ArrayList<MaterialDefinition> __Materials;
    private ArrayList<EnvironmentVariable> __EnvironmentVariables;
    private ArrayList<Parameter> __Parameters;
    private List<Environment> environments;
    private ArrayList<Stage> __Stages;
    private Status __Status = Status.PASSED;
    // [SD] Execution contexts props
    private Date __Start;
    private Date __End;
    private Duration __Duration;
    private ArrayList<MaterialChange> __ExecutionMaterials;
    private String __TriggerReason;
    private boolean __AutoScheduling;
    private boolean __IsLocked;

    public PipelineDefinition() throws Exception {
        this.setLabelTemplate("%COUNT%");
        this.setEnvironmentVariables(new ArrayList<EnvironmentVariable>());
    }

    public int getExecutionID() {
        return __ExecutionID;
    }

    public void setExecutionID(int value) {
        __ExecutionID = value;
    }

    public String getOriginalName() throws Exception {
        return this.originalName;
    }

    public String getName() throws Exception {
        return this.name;
    }

    public void setName(String value) throws Exception {
        if (this.name != null) {
            this.originalName = value;
        } else {
            this.originalName = this.name;
        }
        this.name = value;
    }

    public List<Environment> getEnvironments() {
        return this.environments;
    }

    public void setEnvironments(List<Environment> environments) {
        this.environments = environments;
    }

    public String getGroupName() {
        return __GroupName;
    }

    public void setGroupName(String value) {
        __GroupName = value;
    }

    public String getLabelTemplate() {
        return __LabelTemplate;
    }

    public void setLabelTemplate(String value) {
        __LabelTemplate = value;
    }

    public ArrayList<MaterialDefinition> getMaterials() {
        return __Materials;
    }

    public void setMaterials(ArrayList<MaterialDefinition> value) {
        __Materials = value;
    }

    public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
        return __EnvironmentVariables;
    }

    public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
        __EnvironmentVariables = value;
    }

    public ArrayList<Parameter> getParameters() {
        return __Parameters;
    }

    public void setParameters(ArrayList<Parameter> value) {
        __Parameters = value;
    }

    public ArrayList<Stage> getStages() {
        return __Stages;
    }

    public void setStages(ArrayList<Stage> value) {
        __Stages = value;
    }

    public Status getStatus() {
        return __Status;
    }

    public void setStatus(Status value) {
        __Status = value;
    }

    public Date getStart() {
        return __Start;
    }

    public void setStart(Date value) {
        __Start = value;
    }

    public Date getEnd() {
        return __End;
    }

    public void setEnd(Date value) {
        __End = value;
    }

    public Duration getDuration() {
        return __Duration;
    }

    public void setDuration(Duration value) {
        __Duration = value;
    }

    public ArrayList<MaterialChange> getExecutionMaterials() {
        return __ExecutionMaterials;
    }

    public void setExecutionMaterials(ArrayList<MaterialChange> value) {
        __ExecutionMaterials = value;
    }

    public String getTriggerReason() {
        return __TriggerReason;
    }

    public void setTriggerReason(String value) {
        __TriggerReason = value;
    }

    public boolean getAutoScheduling() {
        return __AutoScheduling;
    }

    public void setAutoScheduling(boolean value) {
        __AutoScheduling = value;
    }

    public boolean getIsLocked() {
        return __IsLocked;
    }

    public void setIsLocked(boolean value) {
        __IsLocked = value;
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

    public Job getJobById(UUID stageID, UUID jobID) throws Exception {

        throw new OperationNotSupportedException("not implemented");
		/*
		 * Stage stage = getStageById(stageID); if (stage.getJobs() != null) {
		 * Job job = stage.getJobs().Where( [UNSUPPORTED] to translate lambda
		 * expressions we need an explicit delegate type, try adding a cast "(j)
		 * => { return j.Id == jobID; }" ).FirstOrDefault(); if (job == null)
		 * throw new
		 * ArgumentException(String.format(StringSupport.CSFmtStrToJFmtStr(
		 * "Job wiht ID {0} was not found in Stage with ID {1}"
		 * ),jobID,stageID));
		 * 
		 * return job; }
		 * 
		 * throw new
		 * NullReferenceException(String.format(StringSupport.CSFmtStrToJFmtStr(
		 * "Jobs collection of Stage {0} is null"),stageID));
		 */
    }

    public TaskBase getTaskById(UUID stageID, UUID jobID, UUID taskID) throws Exception {

        throw new OperationNotSupportedException("not implemented");
		/*
		 * Job job = getJobById(stageID,jobID); if (job.getTasks() != null) {
		 * TaskBase task = job.getTasks().Where( [UNSUPPORTED] to translate
		 * lambda expressions we need an explicit delegate type, try adding a
		 * cast "(j) => { return j.Id == taskID; }" ).FirstOrDefault(); if (task
		 * == null) throw new
		 * ArgumentException(String.format(StringSupport.CSFmtStrToJFmtStr(
		 * "Task with ID {0} was not found in Job with ID {1}"),taskID,jobID));
		 * 
		 * return task; } throw new
		 * NullReferenceException(String.format(StringSupport.CSFmtStrToJFmtStr(
		 * "Tasks collection of Job {0} is null"),stageID));
		 */
    }

    public void resetIdentifier() throws Exception {
        this.originalName = this.name;
    }

}
