package net.hawkengine.model;

import java.util.Date;
import java.util.HashMap;

public class MaterialChange extends DbEntry {
    private String pipelineName;
    private String materialName;
    private String url;
    private MaterialType type = MaterialType.GIT;
    private Date changeDate;
    private HashMap<String, Object> materialSpecificDetails;

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String value) {
        pipelineName = value;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String value) {
        materialName = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        url = value;
    }

    public MaterialType getType() {
        return type;
    }

    public void setType(MaterialType value) {
        type = value;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date value) {
        changeDate = value;
    }

    public HashMap<String, Object> getMaterialSpecificDetails() {
        return materialSpecificDetails;
    }

    public void setMaterialSpecificDetails(HashMap<String, Object> value) {
        materialSpecificDetails = value;
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
