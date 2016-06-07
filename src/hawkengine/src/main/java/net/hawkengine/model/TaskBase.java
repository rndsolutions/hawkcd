//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

import java.util.Date;
import java.util.UUID;

public abstract class TaskBase {
	private UUID __Id;

	public UUID getId() {
		return __Id;
	}

	public void setId(UUID value) {
		__Id = value;
	}

	private RunIf __RunIfCondition = RunIf.Passed;

	public RunIf getRunIfCondition() {
		return __RunIfCondition;
	}

	public void setRunIfCondition(RunIf value) {
		__RunIfCondition = value;
	}

	private TaskType __Type = TaskType.Exec;

	public TaskType getType() {
		return __Type;
	}

	public void setType(TaskType value) {
		__Type = value;
	}

	public TaskBase() throws Exception {
		this.setId(UUID.randomUUID());
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

	private String __Result;

	public String getResult() {
		return __Result;
	}

	public void setResult(String value) {
		__Result = value;
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

}
