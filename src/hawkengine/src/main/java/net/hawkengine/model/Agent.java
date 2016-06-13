package net.hawkengine.model;

import java.util.ArrayList;
import java.util.Date;

public class Agent extends DbEntry {
	private String name;
	private String hostName;
	private String ipAddress;
	private String sandbox;
	private ArrayList<String> resources;
	private ArrayList<String> environments;
	private AgentExecutionState executionState = AgentExecutionState.Idle;
	private ConfigState configState = ConfigState.Enabled;
	private String executingPipelineId;
	private String executingStageId;
	private String executingJobId;
	private boolean isConnected;
	private Date lastReported;
	private Object os;

	public String getName() {
		return this.name;
	}

	public void setName(String value) {
		this.name = value;
	}

	public String getHostName() {
		return this.hostName;
	}

	public void setHostName(String value) {
		this.hostName = value;
	}

	public String getIPAddress() {
		return this.ipAddress;
	}

	public void setIPAddress(String value) {
		this.ipAddress = value;
	}

	public String getSandbox() {
		return this.sandbox;
	}

	public void setSandbox(String value) {
		this.sandbox = value;
	}

	public ArrayList<String> getResources() {
		return this.resources;
	}

	public void setResources(ArrayList<String> value) {
		this.resources = value;
	}

	public ArrayList<String> getEnvironments() {
		return this.environments;
	}

	public void setEnvironments(ArrayList<String> value) {
		this.environments = value;
	}

	public AgentExecutionState getExecutionState() {
		return this.executionState;
	}

	public void setExecutionState(AgentExecutionState value) {
		this.executionState = value;
	}

	public ConfigState getConfigState() {
		return this.configState;
	}

	public void setConfigState(ConfigState value) {
		this.configState = value;
	}

	public String getExecutingPipelineID() {
		return this.executingPipelineId;
	}

	public void setExecutingPipelineID(String value) {
		this.executingPipelineId = value;
	}

	public String getExecutingStageId() {
		return this.executingStageId;
	}

	public void setExecutingStageId(String value) {
		this.executingStageId = value;
	}

	public String getExecutingJobId() {
		return this.executingJobId;
	}

	public void setExecutingJobId(String value) {
		this.executingJobId = value;
	}

	public boolean getIsConnected() {
		return this.isConnected;
	}

	public void setIsConnected(boolean value) {
		this.isConnected = value;
	}

	public Date getLastReported() {
		return this.lastReported;
	}

	public void setLastReported(Date value) {
		this.lastReported = value;
	}

	public Object getOS() {
		return this.os;
	}

	public void setOS(Object value) {
		this.os = value;
	}
}
