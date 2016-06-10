package net.hawkengine.model;

import java.util.ArrayList;

public class Environment extends DbEntry {
    private String environmentName;
    private ArrayList<EnvironmentVariable> environmentVariables;
    private ArrayList<String> pipelineNames;
	private ArrayList<Agent> agents;

    public Environment() {
        this.setEnvironmentVariables(new ArrayList<EnvironmentVariable>());
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String value) {
        environmentName = value;
    }

    public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
        environmentVariables = value;
    }

    public ArrayList<String> getPipelineNames() {
        return pipelineNames;
    }

    public void setPipelineNames(ArrayList<String> value) {
        pipelineNames = value;
    }

    public ArrayList<Agent> getAgents() {
        return agents;
    }

    public void setAgents(ArrayList<Agent> value) {
        agents = value;
    }
}
