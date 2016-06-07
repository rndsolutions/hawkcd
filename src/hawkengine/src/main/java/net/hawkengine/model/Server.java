//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

public class Server extends DbEntry {
	private String __ArtifactsDir;

	public String getArtifactsDir() {
		return __ArtifactsDir;
	}

	public void setArtifactsDir(String value) {
		__ArtifactsDir = value;
	}

	private Double __PurgeStart;

	public Double getPurgeStart() {
		return __PurgeStart;
	}

	public void setPurgeStart(Double value) {
		__PurgeStart = value;
	}

	private Double __PurgeUpTo;

	public Double getPurgeUpTo() {
		return __PurgeUpTo;
	}

	public void setPurgeUpTo(Double value) {
		__PurgeUpTo = value;
	}

}
