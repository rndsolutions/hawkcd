package net.hawkengine.model.payload;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.enums.PermissionScope;

public class Permission extends DbEntry{
    private PermissionScope permissionScope;
    private String permittedEntityId;

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
}
