//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Stage {
	private StageType stageType = StageType.OnSuccess;

	public Stage() throws Exception {
		this.setID(UUID.randomUUID());
		this.setFetchMaterials(true);
		this.setEnvironmentVariables(new ArrayList<EnvironmentVariable>());
	}

	private UUID __ID;

	public UUID getID() {
		return __ID;
	}

	public void setID(UUID value) {
		__ID = value;
	}

	private int __ExecutionID;

	public int getExecutionID() {
		return __ExecutionID;
	}

	public void setExecutionID(int value) {
		__ExecutionID = value;
	}

	private String __Name;

	public String getName() {
		return __Name;
	}

	public void setName(String value) {
		__Name = value;
	}

	private ArrayList<Job> __Jobs;

	public ArrayList<Job> getJobs() {
		return __Jobs;
	}

	public void setJobs(ArrayList<Job> value) {
		__Jobs = value;
	}

	private ArrayList<EnvironmentVariable> __EnvironmentVariables;

	public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
		return __EnvironmentVariables;
	}

	public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
		__EnvironmentVariables = value;
	}

	private String __Security;

	public String getSecurity() {
		return __Security;
	}

	public void setSecurity(String value) {
		__Security = value;
	}

	private boolean __FetchMaterials;

	public boolean getFetchMaterials() {
		return __FetchMaterials;
	}

	public void setFetchMaterials(boolean value) {
		__FetchMaterials = value;
	}

	private boolean __NeverCleanArtifacts;

	public boolean getNeverCleanArtifacts() {
		return __NeverCleanArtifacts;
	}

	public void setNeverCleanArtifacts(boolean value) {
		__NeverCleanArtifacts = value;
	}

	private boolean __CleanWorkingDirectory;

	public boolean getCleanWorkingDirectory() {
		return __CleanWorkingDirectory;
	}

	public void setCleanWorkingDirectory(boolean value) {
		__CleanWorkingDirectory = value;
	}

	public StageType getStageType() throws Exception {
		return this.stageType;
	}

	public void setStageType(StageType value) throws Exception {
		if (value == getStageType().Manual) {
			this.setCanBeExecuted(false);
		} else {
			this.setCanBeExecuted(true);
		}
		this.stageType = value;
	}

	private Status __Status = Status.PASSED;

	public Status getStatus() {
		return __Status;
	}

	public void setStatus(Status value) {
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
