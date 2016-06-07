//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

import java.util.HashMap;

public class FetchMaterialTask extends TaskBase {
	public FetchMaterialTask() throws Exception {
		this.setType(TaskType.FetchMaterial);
	}

	private String __MaterialName;

	public String getMaterialName() {
		return __MaterialName;
	}

	public void setMaterialName(String value) {
		__MaterialName = value;
	}

	private String __PipelineName;

	public String getPipelineName() {
		return __PipelineName;
	}

	public void setPipelineName(String value) {
		__PipelineName = value;
	}

	private MaterialType __MaterialType = MaterialType.Git;

	public MaterialType getMaterialType() {
		return __MaterialType;
	}

	public void setMaterialType(MaterialType value) {
		__MaterialType = value;
	}

	private String __Source;

	public String getSource() {
		return __Source;
	}

	public void setSource(String value) {
		__Source = value;
	}

	private String __Destination;

	public String getDestination() {
		return __Destination;
	}

	public void setDestination(String value) {
		__Destination = value;
	}

	private HashMap<String, Object> __MaterialSpecificDetails;

	public HashMap<String, Object> getMaterialSpecificDetails() {
		return __MaterialSpecificDetails;
	}

	public void setMaterialSpecificDetails(HashMap<String, Object> value) {
		__MaterialSpecificDetails = value;
	}

}
