package net.hawkengine.model.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.hawkengine.model.DbEntry;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;

public class Permission extends DbEntry{
    //TODO: Rename Scope
    private PermissionScope permissionScope;
    private String permittedEntityId;
    //TODO: permission type
    private PermissionType permissionType;

    public PermissionScope getPermissionScope() {
        return this.permissionScope;
    }

    public void setPermissionScope(PermissionScope permissionScope) {
        this.permissionScope = permissionScope;
    }

    public String getPermittedEntityId() {
        return this.permittedEntityId;
    }

    public void setPermittedEntityId(String permittedEntityId) {
        this.permittedEntityId = permittedEntityId;
    }

    public PermissionType getPermissionType() {
        return this.permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }
}
