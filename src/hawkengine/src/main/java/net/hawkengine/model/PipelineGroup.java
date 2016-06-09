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

	private ArrayList<Pipeline> __Pipelines;

	public ArrayList<Pipeline> getPipelines() {
		return __Pipelines;
	}

	public void setPipelines(ArrayList<Pipeline> value) {
		__Pipelines = value;
	}

}
