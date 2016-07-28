package net.hawkengine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Permission extends DbEntry{
    //TODO: Rename Scope
    //private PermissionScope permissionScope;
    private String permittedEntityId;
    //TODO: permission type
//    private PermissionType permissionType;
    private boolean isAbleToAdd;
    private boolean isAbleToGet;
    private boolean isAbleToUpdate;
    private boolean isAbleToDelete;
/*
    public PermissionScope getPermissionScope() {
        return this.permissionScope;
    }

    public void setPermissionScope(PermissionScope permissionScope) {
        this.permissionScope = permissionScope;
    }
*/
    public String getPermittedEntityId() {
        return this.permittedEntityId;
    }

    public void setPermittedEntityId(String permittedEntityId) {
        this.permittedEntityId = permittedEntityId;
    }
/*
    public PermissionType getPermissionType() {
        return this.permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }
*/
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