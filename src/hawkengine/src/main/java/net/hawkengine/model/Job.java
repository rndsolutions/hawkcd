//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Job {
	public Job() throws Exception {
		this.setId(UUID.randomUUID());
		this.setCanBeExecuted(true);
		this.setResources(new ArrayList<String>());
		this.setEnvironmentVariables(new ArrayList<EnvironmentVariable>());
	}

	private UUID __Id;

	public UUID getId() {
		return __Id;
	}

	public void setId(UUID value) {
		__Id = value;
	}

	private String __Name;

	public String getName() {
		return __Name;
	}

	public void setName(String value) {
		__Name = value;
	}

	private String __Result;

	public String getResult() {
		return __Result;
	}

	public void setResult(String value) {
		__Result = value;
	}

	private String __ExecutedBy;

	public String getExecutedBy() {
		return __ExecutedBy;
	}

	public void setExecutedBy(String value) {
		__ExecutedBy = value;
	}

	private ArrayList<TaskBase> __Tasks;

	public ArrayList<TaskBase> getTasks() {
		return __Tasks;
	}

	public void setTasks(ArrayList<TaskBase> value) {
		__Tasks = value;
	}

	private ArrayList<EnvironmentVariable> __EnvironmentVariables;

	public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
		return __EnvironmentVariables;
	}

	public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
		__EnvironmentVariables = value;
	}

	private ArrayList<Tab> __Tabs;

	public ArrayList<Tab> getTabs() {
		return __Tabs;
	}

	public void setTabs(ArrayList<Tab> value) {
		__Tabs = value;
	}

	private int __RunInstaceCount;

	public int getRunInstaceCount() {
		return __RunInstaceCount;
	}

	public void setRunInstaceCount(int value) {
		__RunInstaceCount = value;
	}

	private RunType __RunType = RunType.One;

	public RunType getRunType() {
		return __RunType;
	}

	public void setRunType(RunType value) {
		__RunType = value;
	}

	/**
	 * Comma separated values in the UI;
	 */
	private ArrayList<String> __Resources;

	public ArrayList<String> getResources() {
		return __Resources;
	}

	public void setResources(ArrayList<String> value) {
		__Resources = value;
	}

	private JobTimeOutType __JobTimeOut = JobTimeOutType.Never;

	public JobTimeOutType getJobTimeOut() {
		return __JobTimeOut;
	}

	public void setJobTimeOut(JobTimeOutType value) {
		__JobTimeOut = value;
	}

	/**
	 * The amount of time in minutes.
	 */
	private int __CancelAfter;

	public int getCancelAfter() {
		return __CancelAfter;
	}

	public void setCancelAfter(int value) {
		__CancelAfter = value;
	}

	// [SD] Execution contexts props
	private ExecutionState __State = ExecutionState.None;

	public ExecutionState getState() {
		return __State;
	}

	public void setState(ExecutionState value) {
		__State = value;
	}

	private ExecutionStatus __Status = ExecutionStatus.Passed;

	public ExecutionStatus getStatus() {
		return __Status;
	}

	public void setStatus(ExecutionStatus value) {
		__Status = value;
	}

	private Date __Start;

	public Date getStart() {
		return __Start;
	}

	public void setStart(Date value) {
		__Start = value;
	}

	private Date __End;

	public Date getEnd() {
		return __End;
	}

	public void setEnd(Date value) {
		__End = value;
	}

	private Duration __Duration;

	public Duration getDuration() {
		return __Duration;
	}

	public void setDuration(Duration value) {
		__Duration = value;
	}

	private boolean __CanBeExecuted;

	public boolean getCanBeExecuted() {
		return __CanBeExecuted;
	}

	public void setCanBeExecuted(boolean value) {
		__CanBeExecuted = value;
	}

}
