//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model.payload;

public class AgentReport {
	private AgentInfo __AgentInfo;

	public AgentInfo getAgentInfo() {
		return this.__AgentInfo;
	}

	public void setAgentInfo(AgentInfo value) {
		this.__AgentInfo = value;
	}

	private EnvironmentInfo __EnvironmentInfo;

	public EnvironmentInfo getEnvironmentInfo() {
		return this.__EnvironmentInfo;
	}

	public void setEnvironmentInfo(EnvironmentInfo value) {
		this.__EnvironmentInfo = value;
	}

	private JobExecutionInfo __JobExecutionInfo;

	public JobExecutionInfo getJobExecutionInfo() {
		return this.__JobExecutionInfo;
	}

	public void setJobExecutionInfo(JobExecutionInfo value) {
		this.__JobExecutionInfo = value;
	}

}
