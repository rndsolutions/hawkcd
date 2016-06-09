//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model.payload;

import java.util.ArrayList;

import net.hawkengine.model.*;

public class ServerResponseToAgent {
	public ServerResponseToAgent() throws Exception {
		this.setEnvironmentVariables(new ArrayList<EnvironmentVariable>());
	}

	private Pipeline __Pipeline;

	public Pipeline getPipeline() {
		return __Pipeline;
	}

	public void setPipeline(Pipeline value) {
		__Pipeline = value;
	}

	private Job __Job;

	public Job getJob() {
		return __Job;
	}

	public void setJob(Job value) {
		__Job = value;
	}

	private Stage __Stage;

	public Stage getStage() {
		return __Stage;
	}

	public void setStage(Stage value) {
		__Stage = value;
	}

	private EnvironmentInfo __EnvironmentInfo;

	public EnvironmentInfo getEnvironmentInfo() {
		return __EnvironmentInfo;
	}

	public void setEnvironmentInfo(EnvironmentInfo value) {
		__EnvironmentInfo = value;
	}

	private ArrayList<Material> __Materials;

	public ArrayList<Material> getMaterials() {
		return __Materials;
	}

	public void setMaterials(ArrayList<Material> value) {
		__Materials = value;
	}

	private ArrayList<MaterialChange> __ExecutionMaterials;

	public ArrayList<MaterialChange> getExecutionMaterials() {
		return __ExecutionMaterials;
	}

	public void setExecutionMaterials(ArrayList<MaterialChange> value) {
		__ExecutionMaterials = value;
	}

	private ArrayList<EnvironmentVariable> __EnvironmentVariables;

	public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
		return __EnvironmentVariables;
	}

	public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
		__EnvironmentVariables = value;
	}

}
