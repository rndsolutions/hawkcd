package net.hawkengine.model;

import java.util.ArrayList;

public class Environment extends DbEntry {
    private String environmentName;
    private ArrayList<EnvironmentVariable> environmentVariables;
    private ArrayList<String> pipelineNames;
    private ArrayList<Agent> agents;

    public Environment() {
        this.setEnvironmentVariables(new ArrayList<>());
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

    public ArrayList<Agent> getAgents() {
        return this.agents;
    }

    public void setAgents(ArrayList<Agent> value) {
        this.agents = value;
    }
}
