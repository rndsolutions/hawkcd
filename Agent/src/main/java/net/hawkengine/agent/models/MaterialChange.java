package net.hawkengine.agent.models;

import net.hawkengine.agent.enums.MaterialType;

import java.util.Date;
import java.util.Dictionary;

public class MaterialChange {

    private String id;
    private String pipelineName;
    private String materialName;
    private String url;
    private MaterialType type;
    private Date changeDate;
    private Dictionary<String, Object> materialSpecificDetails;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MaterialType getType() {
        return type;
    }

    public void setType(MaterialType type) {
        this.type = type;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public Dictionary<String, Object> getMaterialSpecificDetails() {
        return materialSpecificDetails;
    }

    public void setMaterialSpecificDetails(Dictionary<String, Object> materialSpecificDetails) {
        this.materialSpecificDetails = materialSpecificDetails;
    }

    //    @Override
//    public String toString() {
//        return String.format("ID={0} PipelineName={1} MaterialName={2} ChangeDate={3}", ID, PipelineName, MaterialName, ChangeDate);
//    }
}

