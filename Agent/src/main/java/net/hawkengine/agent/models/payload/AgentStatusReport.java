package net.hawkengine.agent.models.payload;

import net.hawkengine.agent.models.Job;

public class AgentStatusReport {
    private AgentInfo agentInfo;
    private EnvironmentInfo environmentInfo;
    private Job jobExecutionInfo;

    public AgentInfo getAgentInfo() {
        return agentInfo;
    }

    public void setAgentInfo(AgentInfo agentInfo) {
        this.agentInfo = agentInfo;
    }

    public EnvironmentInfo getEnvironmentInfo() {
        return environmentInfo;
    }

    public void setEnvironmentInfo(EnvironmentInfo environmentInfo) {
        this.environmentInfo = environmentInfo;
    }

    public Job getJobExecutionInfo() {
        return jobExecutionInfo;
    }

    public void setJobExecutionInfo(Job jobExecutionInfo) {
        this.jobExecutionInfo = jobExecutionInfo;
    }
}
