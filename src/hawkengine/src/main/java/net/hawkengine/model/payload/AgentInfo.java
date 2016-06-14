package net.hawkengine.model.payload;

import net.hawkengine.model.AgentExecutionState;

import java.util.UUID;

public class AgentInfo {
    private UUID agentId;
    private String name;
    private String ipAddress;
    private String rootPath;
    private AgentExecutionState state = AgentExecutionState.IDLE;

    public UUID getAgentId() {
        return this.agentId;
    }

    public void setAgentId(UUID value) {
        this.agentId = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getIPAddress() {
        return this.ipAddress;
    }

    public void setIPAddress(String value) {
        this.ipAddress = value;
    }

    public String getSandbox() {
        return this.rootPath;
    }

    public void setSandbox(String value) {
        this.rootPath = value;
    }

    public AgentExecutionState getState() {
        return this.state;
    }

    public void setState(AgentExecutionState value) {
        this.state = value;
    }

}
