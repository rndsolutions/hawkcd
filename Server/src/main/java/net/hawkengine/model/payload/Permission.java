package net.hawkengine.model.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.hawkengine.model.DbEntry;
import net.hawkengine.model.enums.OperatingArea;
import net.hawkengine.model.enums.Role;

public class Permission extends DbEntry{
    private OperatingArea operatingArea;
    private String permittedEntityId;
    private Role role;
    private boolean isAbleToAdd;
    private boolean isAbleToGet;
    private boolean isAbleToUpdate;
    private boolean isAbleToDelete;

    public OperatingArea getOperatingArea() {
        return this.operatingArea;
    }

    public void setOperatingArea(OperatingArea operatingArea) {
        this.operatingArea = operatingArea;
    }

    public String getPermittedEntityId() {
        return this.permittedEntityId;
    }

    public void setPermittedEntityId(String permittedEntityId) {
        this.permittedEntityId = permittedEntityId;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAbleToAdd() {
        return this.isAbleToAdd;
    }
    @JsonProperty("isAbleToAdd")
    public void setAbleToAdd(boolean ableToAdd) {
        this.isAbleToAdd = ableToAdd;
    }

    public boolean isAbleToGet() {
        return this.isAbleToGet;
    }
    @JsonProperty("isAbleToGet")
    public void setAbleToGet(boolean ableToGet) {
        this.isAbleToGet = ableToGet;
    }

    public boolean isAbleToUpdate() {
        return this.isAbleToUpdate;
    }
    @JsonProperty("isAbleToUpdate")
    public void setAbleToUpdate(boolean ableToUpdate) {
        this.isAbleToUpdate = ableToUpdate;
    }

    public boolean isAbleToDelete() {
        return this.isAbleToDelete;
    }
    @JsonProperty("isAbleToDelete")
    public void setAbleToDelete(boolean ableToDelete) {
        this.isAbleToDelete = ableToDelete;
    }
}
