package net.hawkengine.model.payload;

public class AgentStatusReport {
    private AgentInfo agentInfo;
    private JobExecutionInfo jobExecutionInfo;
    private EnvironmentInfo anvironmentInfo;

    public AgentInfo getAgentInfo() {
        return this.agentInfo;
    }

    public void setAgentInfo(AgentInfo value) {
        this.agentInfo = value;
    }

    public JobExecutionInfo getJobExecutionInfo() {
        return this.jobExecutionInfo;
    }

    public void setJobExecutionInfo(JobExecutionInfo value) {
        this.jobExecutionInfo = value;
    }

    public EnvironmentInfo getEnvironmentInfo() {
        return this.anvironmentInfo;
    }

    public void setEnvironmentInfo(EnvironmentInfo value) {
        this.anvironmentInfo = value;
    }

}
