//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

public class UploadArtifactTask extends TaskBase {
	public UploadArtifactTask() throws Exception {
		this.setType(TaskType.UploadArtifact);
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

}
