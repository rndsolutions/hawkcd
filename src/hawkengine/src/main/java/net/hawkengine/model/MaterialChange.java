//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

import java.util.Date;
import java.util.HashMap;

public class MaterialChange extends DbEntry {
	private String __PipelineName;

	public String getPipelineName() {
		return __PipelineName;
	}

	public void setPipelineName(String value) {
		__PipelineName = value;
	}

	private String __MaterialName;

	public String getMaterialName() {
		return __MaterialName;
	}

	public void setMaterialName(String value) {
		__MaterialName = value;
	}

	private String __Url;

	public String getUrl() {
		return __Url;
	}

	public void setUrl(String value) {
		__Url = value;
	}

	private MaterialType __Type = MaterialType.Git;

	public MaterialType getType() {
		return __Type;
	}

	public void setType(MaterialType value) {
		__Type = value;
	}

	private Date __ChangeDate;

	public Date getChangeDate() {
		return __ChangeDate;
	}

	public void setChangeDate(Date value) {
		__ChangeDate = value;
	}

	private HashMap<String, Object> __MaterialSpecificDetails;

	public HashMap<String, Object> getMaterialSpecificDetails() {
		return __MaterialSpecificDetails;
	}

	public void setMaterialSpecificDetails(HashMap<String, Object> value) {
		__MaterialSpecificDetails = value;
	}

	public String toString() {
		try {
			return String.format(" ID={0} PipelineName={1} MaterialName={2} ChangeDate={3}", this.getId(),
					getPipelineName(), getMaterialName(), getChangeDate());
		} catch (RuntimeException __dummyCatchVar0) {
			throw __dummyCatchVar0;
		} catch (Exception __dummyCatchVar0) {
			throw new RuntimeException(__dummyCatchVar0);
		}

	}

}
