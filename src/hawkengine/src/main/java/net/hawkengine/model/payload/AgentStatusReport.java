//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model.payload;

public class AgentStatusReport {
	private AgentInfo __AgentInfo;

	public AgentInfo getAgentInfo() {
		return __AgentInfo;
	}

	public void setAgentInfo(AgentInfo value) {
		__AgentInfo = value;
	}

	private JobExecutionInfo __JobExecutionInfo;

	public JobExecutionInfo getJobExecutionInfo() {
		return __JobExecutionInfo;
	}

	public void setJobExecutionInfo(JobExecutionInfo value) {
		__JobExecutionInfo = value;
	}

	private EnvironmentInfo __EnvironmentInfo;

	public EnvironmentInfo getEnvironmentInfo() {
		return __EnvironmentInfo;
	}

	public void setEnvironmentInfo(EnvironmentInfo value) {
		__EnvironmentInfo = value;
	}

}
