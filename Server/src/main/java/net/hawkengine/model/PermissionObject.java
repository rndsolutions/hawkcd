package net.hawkengine.model;

import net.hawkengine.model.enums.PermissionType;

public class PermissionObject {
    private PermissionType permissionType;

    public PermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }
}
