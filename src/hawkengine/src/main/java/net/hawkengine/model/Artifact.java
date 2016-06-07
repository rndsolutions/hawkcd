
package net.hawkengine.model;

public class Artifact {
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

	private ArtifactType __Type = ArtifactType.Build;

	public ArtifactType getType() {
		return __Type;
	}

	public void setType(ArtifactType value) {
		__Type = value;
	}

}
