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
		return name;
	}

	public void setName(String value) {
		name = value;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String value) {
		hostName = value;
	}

	public String getIPAddress() {
		return ipAddress;
	}

	public void setIPAddress(String value) {
		ipAddress = value;
	}

	public String getSandbox() {
		return sandbox;
	}

	public void setSandbox(String value) {
		sandbox = value;
	}

	public ArrayList<String> getResources() {
		return resources;
	}

	public void setResources(ArrayList<String> value) {
		resources = value;
	}

	public ArrayList<String> getEnvironments() {
		return environments;
	}

	public void setEnvironments(ArrayList<String> value) {
		environments = value;
	}

	public AgentExecutionState getExecutionState() {
		return executionState;
	}

	public void setExecutionState(AgentExecutionState value) {
		executionState = value;
	}

	public ConfigState getConfigState() {
		return configState;
	}

	public void setConfigState(ConfigState value) {
		configState = value;
	}

	public String getExecutingPipelineID() {
		return executingPipelineId;
	}

	public void setExecutingPipelineID(String value) {
		executingPipelineId = value;
	}

	public String getExecutingStageId() {
		return executingStageId;
	}

	public void setExecutingStageId(String value) {
		executingStageId = value;
	}

	public String getExecutingJobId() {
		return executingJobId;
	}

	public void setExecutingJobId(String value) {
		executingJobId = value;
	}

	public boolean getIsConnected() {
		return isConnected;
	}

	public void setIsConnected(boolean value) {
		isConnected = value;
	}

	public Date getLastReported() {
		return lastReported;
	}

	public void setLastReported(Date value) {
		lastReported = value;
	}

	public Object getOS() {
		return os;
	}

	public void setOS(Object value) {
		os = value;
	}
}
