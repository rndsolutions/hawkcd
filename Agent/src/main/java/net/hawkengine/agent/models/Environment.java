package net.hawkengine.agent.models;

import net.hawkengine.agent.models.payload.AgentInfo;

import java.util.ArrayList;

public class Environment {
    private String id;
    private String environmentName;
    private ArrayList<EnvironmentVariable> environmentVariables;
    private ArrayList<String> pipelineNames;
    private ArrayList<AgentInfo> agents;

    public Environment() {
        this.setEnvironmentVariables(new ArrayList<>());
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnvironmentName() {
        return this.environmentName;
    }

    public void setEnvironmentName(String value) {
        this.environmentName = value;
    }

    public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
        this.environmentVariables = value;
    }

    public ArrayList<String> getPipelineNames() {
        return this.pipelineNames;
    }

    public void setPipelineNames(ArrayList<String> value) {
        this.pipelineNames = value;
    }

    public ArrayList<AgentInfo> getAgents() {
        return this.agents;
    }

    public void setAgents(ArrayList<AgentInfo> value) {
        this.agents = value;
    }
}
