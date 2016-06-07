//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

import java.util.ArrayList;

public class Environment extends DbEntry {
	public Environment() throws Exception {
		this.setEnvironmentVariables(new ArrayList<EnvironmentVariable>());
	}

	private ArrayList<Agent> __Agents;

	public ArrayList<Agent> getAgents() {
		return __Agents;
	}

	public void setAgents(ArrayList<Agent> value) {
		__Agents = value;
	}

	private ArrayList<String> __PipelineNames;

	public ArrayList<String> getPipelineNames() {
		return __PipelineNames;
	}

	public void setPipelineNames(ArrayList<String> value) {
		__PipelineNames = value;
	}

	private ArrayList<EnvironmentVariable> __EnvironmentVariables;

	public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
		return __EnvironmentVariables;
	}

	public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
		__EnvironmentVariables = value;
	}

	private String __EnvironmentName;

	public String getEnvironmentName() {
		return __EnvironmentName;
	}

	public void setEnvironmentName(String value) {
		__EnvironmentName = value;
	}

}
