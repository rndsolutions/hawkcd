//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model.payload;

public class AgentReport {
    private AgentInfo agentInfo;
    private EnvironmentInfo environmentInfo;
    private JobExecutionInfo jobExecutionInfo;

    public AgentInfo getAgentInfo() {
        return this.agentInfo;
    }

    public void setAgentInfo(AgentInfo value) {
        this.agentInfo = value;
    }

    public EnvironmentInfo getEnvironmentInfo() {
        return this.environmentInfo;
    }

    public void setEnvironmentInfo(EnvironmentInfo value) {
        this.environmentInfo = value;
    }

    public JobExecutionInfo getJobExecutionInfo() {
        return this.jobExecutionInfo;
    }

    public void setJobExecutionInfo(JobExecutionInfo value) {
        this.jobExecutionInfo = value;
    }

}
