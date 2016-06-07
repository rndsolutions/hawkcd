//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model.payload;

import java.util.Date;
import java.util.UUID;

import net.hawkengine.model.ExecutionState;
import net.hawkengine.model.ExecutionStatus;

public class JobTaskExecutionInfo {
	private UUID __TaskId;

	public UUID getTaskId() {
		return __TaskId;
	}

	public void setTaskId(UUID value) {
		__TaskId = value;
	}

	private String __Result;

	public String getResult() {
		return __Result;
	}

	public void setResult(String value) {
		__Result = value;
	}

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

	public JobTaskExecutionInfo() throws Exception {
		setResult("");
	}

	public JobTaskExecutionInfo(UUID taskId, Date start, ExecutionState state) throws Exception {
		setState(state);
		setTaskId(taskId);
		setStart(start);
		setResult("");
	}

	public String toString() {
		try {
			return String.format("TaskId: {0}, Result: {1}, State: {2}, Start: {3}, End:{3} ", getTaskId(), getResult(),
					getState(), getStart(), getEnd());
		} catch (RuntimeException __dummyCatchVar0) {
			throw __dummyCatchVar0;
		} catch (Exception __dummyCatchVar0) {
			throw new RuntimeException(__dummyCatchVar0);
		}

	}

}
