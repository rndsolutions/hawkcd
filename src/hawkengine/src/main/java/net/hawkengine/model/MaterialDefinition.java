//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

import java.util.HashMap;

public class MaterialDefinition {
	public MaterialDefinition() throws Exception {
	}

	private String __PipelineName;

	public String getPipelineName() {
		return __PipelineName;
	}

	public void setPipelineName(String value) {
		__PipelineName = value;
	}

	private String __Name;

	public String getName() {
		return __Name;
	}

	public void setName(String value) {
		__Name = value;
	}

	private MaterialType __Type = MaterialType.Git;

	public MaterialType getType() {
		return __Type;
	}

	public void setType(MaterialType value) {
		__Type = value;
	}

	private String __Url;

	public String getUrl() {
		return __Url;
	}

	public void setUrl(String value) {
		__Url = value;
	}

	private boolean __AutoTriggerOnChange;

	public boolean getAutoTriggerOnChange() {
		return __AutoTriggerOnChange;
	}

	public void setAutoTriggerOnChange(boolean value) {
		__AutoTriggerOnChange = value;
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
