//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model.payload;

import java.util.ArrayList;

import net.hawkengine.model.ArtifactInfoType;

public class ArtifactInfo {
	private String __Name;

	public String getName() {
		return __Name;
	}

	public void setName(String value) {
		__Name = value;
	}

	private String __Url;

	public String getUrl() {
		return __Url;
	}

	public void setUrl(String value) {
		__Url = value;
	}

	private ArtifactInfoType __Type = ArtifactInfoType.File;

	public ArtifactInfoType getType() {
		return __Type;
	}

	public void setType(ArtifactInfoType value) {
		__Type = value;
	}

	private ArrayList<ArtifactInfo> __Files;

	public ArrayList<ArtifactInfo> getFiles() {
		return __Files;
	}

	public void setFiles(ArrayList<ArtifactInfo> value) {
		__Files = value;
	}

}
