//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

import java.util.ArrayList;

public class PipelineGroup extends DbEntry {
	private String __Name;

	public String getName() {
		return __Name;
	}

	public void setName(String value) {
		__Name = value;
	}

	private ArrayList<PipelineDefinition> __PipelineDefinitions;

	public ArrayList<PipelineDefinition> getPipelines() {
		return __PipelineDefinitions;
	}

	public void setPipelines(ArrayList<PipelineDefinition> value) {
		__PipelineDefinitions = value;
	}

}
