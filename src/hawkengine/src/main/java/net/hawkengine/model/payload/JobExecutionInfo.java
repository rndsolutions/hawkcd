//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model.payload;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import net.hawkengine.model.Status;

public class JobExecutionInfo {
	private UUID __PipelineId;

	public UUID getPipelineId() {
		return this.__PipelineId;
	}

	public void setPipelineId(UUID value) {
		this.__PipelineId = value;
	}

	private int __PipelineExecutionId;

	public int getPipelineExecutionId() {
		return this.__PipelineExecutionId;
	}

	public void setPipelineExecutionId(int value) {
		this.__PipelineExecutionId = value;
	}

	private String __PipelineLabel;

	public String getPipelineLabel() {
		return this.__PipelineLabel;
	}

	public void setPipelineLabel(String value) {
		this.__PipelineLabel = value;
	}

	private String __PipelineName;

	public String getPipelineName() {
		return this.__PipelineName;
	}

	public void setPipelineName(String value) {
		this.__PipelineName = value;
	}

	private String __ExecutedBy;

	public String getExecutedBy() {
		return this.__ExecutedBy;
	}

	public void setExecutedBy(String value) {
		this.__ExecutedBy = value;
	}

	private UUID __StageId;

	public UUID getStageId() {
		return this.__StageId;
	}

	public void setStageId(UUID value) {
		this.__StageId = value;
	}

	private int __StageExecutionId;

	public int getStageExecutionId() {
		return this.__StageExecutionId;
	}

	public void setStageExecutionId(int value) {
		this.__StageExecutionId = value;
	}

	private String __StageName;

	public String getStageName() {
		return this.__StageName;
	}

	public void setStageName(String value) {
		this.__StageName = value;
	}

	private UUID __JobId;

	public UUID getJobId() {
		return this.__JobId;
	}

	public void setJobId(UUID value) {
		this.__JobId = value;
	}

	private String __JobName;

	public String getJobName() {
		return this.__JobName;
	}

	public void setJobName(String value) {
		this.__JobName = value;
	}

	private Status __Status = Status.PASSED;

	public Status getStatus() {
		return this.__Status;
	}

	public void setStatus(Status value) {
		this.__Status = value;
	}

	private String __JobStateString;

	public String getJobStateString() {
		return this.__JobStateString;
	}

	public void setJobStateString(String value) {
		this.__JobStateString = value;
	}

	private Date __Start;

	public Date getStart() {
		return this.__Start;
	}

	public void setStart(Date value) {
		this.__Start = value;
	}

	private Date __End;

	public Date getEnd() {
		return this.__End;
	}

	public void setEnd(Date value) {
		this.__End = value;
	}

	private Duration __Duration;

	public Duration getDuration() {
		return this.__Duration;
	}

	public void setDuration(Duration value) {
		this.__Duration = value;
	}

	private String __Result;

	public String getResult() {
		return this.__Result;
	}

	public void setResult(String value) {
		this.__Result = value;
	}
}
