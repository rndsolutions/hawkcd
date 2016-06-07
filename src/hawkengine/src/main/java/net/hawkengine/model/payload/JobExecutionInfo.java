//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model.payload;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import net.hawkengine.model.ExecutionState;
import net.hawkengine.model.ExecutionStatus;

public class JobExecutionInfo {
	private UUID __PipelineId;

	public UUID getPipelineId() {
		return __PipelineId;
	}

	public void setPipelineId(UUID value) {
		__PipelineId = value;
	}

	private int __PipelineExecutionId;

	public int getPipelineExecutionId() {
		return __PipelineExecutionId;
	}

	public void setPipelineExecutionId(int value) {
		__PipelineExecutionId = value;
	}

	private String __PipelineLabel;

	public String getPipelineLabel() {
		return __PipelineLabel;
	}

	public void setPipelineLabel(String value) {
		__PipelineLabel = value;
	}

	private String __PipelineName;

	public String getPipelineName() {
		return __PipelineName;
	}

	public void setPipelineName(String value) {
		__PipelineName = value;
	}

	private String __ExecutedBy;

	public String getExecutedBy() {
		return __ExecutedBy;
	}

	public void setExecutedBy(String value) {
		__ExecutedBy = value;
	}

	private UUID __StageId;

	public UUID getStageId() {
		return __StageId;
	}

	public void setStageId(UUID value) {
		__StageId = value;
	}

	private int __StageExecutionId;

	public int getStageExecutionId() {
		return __StageExecutionId;
	}

	public void setStageExecutionId(int value) {
		__StageExecutionId = value;
	}

	private String __StageName;

	public String getStageName() {
		return __StageName;
	}

	public void setStageName(String value) {
		__StageName = value;
	}

	private UUID __JobId;

	public UUID getJobId() {
		return __JobId;
	}

	public void setJobId(UUID value) {
		__JobId = value;
	}

	private String __JobName;

	public String getJobName() {
		return __JobName;
	}

	public void setJobName(String value) {
		__JobName = value;
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

	private String __JobStateString;

	public String getJobStateString() {
		return __JobStateString;
	}

	public void setJobStateString(String value) {
		__JobStateString = value;
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

	private ArrayList<JobTaskExecutionInfo> __TaskExecutionInfo;

	public ArrayList<JobTaskExecutionInfo> getTaskExecutionInfo() {
		return __TaskExecutionInfo;
	}

	public void setTaskExecutionInfo(ArrayList<JobTaskExecutionInfo> value) {
		__TaskExecutionInfo = value;
	}

	private String __Result;

	public String getResult() {
		return __Result;
	}

	public void setResult(String value) {
		__Result = value;
	}

	public JobExecutionInfo() throws Exception {
		setTaskExecutionInfo(new ArrayList<JobTaskExecutionInfo>());
	}

}
