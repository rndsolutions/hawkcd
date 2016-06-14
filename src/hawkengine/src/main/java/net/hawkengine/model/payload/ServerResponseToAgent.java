//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model.payload;

import java.util.ArrayList;

import net.hawkengine.model.*;

public class ServerResponseToAgent {
	public ServerResponseToAgent() throws Exception {
		this.setEnvironmentVariables(new ArrayList<>());
	}

	private Pipeline __Pipeline;

	public Pipeline getPipeline() {
		return this.__Pipeline;
	}

	public void setPipeline(Pipeline value) {
		this.__Pipeline = value;
	}

	private JobDefinition __Job;

	public JobDefinition getJob() {
		return this.__Job;
	}

	public void setJob(JobDefinition value) {
		this.__Job = value;
	}

	private Stage __Stage;

	public Stage getStage() {
		return this.__Stage;
	}

	public void setStage(Stage value) {
		this.__Stage = value;
	}

	private EnvironmentInfo __EnvironmentInfo;

	public EnvironmentInfo getEnvironmentInfo() {
		return this.__EnvironmentInfo;
	}

	public void setEnvironmentInfo(EnvironmentInfo value) {
		this.__EnvironmentInfo = value;
	}

	private ArrayList<MaterialDefinition> __Materials;

	public ArrayList<MaterialDefinition> getMaterials() {
		return this.__Materials;
	}

	public void setMaterials(ArrayList<MaterialDefinition> value) {
		this.__Materials = value;
	}

	private ArrayList<MaterialChange> __ExecutionMaterials;

	public ArrayList<MaterialChange> getExecutionMaterials() {
		return this.__ExecutionMaterials;
	}

	public void setExecutionMaterials(ArrayList<MaterialChange> value) {
		this.__ExecutionMaterials = value;
	}

	private ArrayList<EnvironmentVariable> __EnvironmentVariables;

	public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
		return this.__EnvironmentVariables;
	}

	public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
		this.__EnvironmentVariables = value;
	}

}
