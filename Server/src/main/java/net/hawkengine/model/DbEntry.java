package net.hawkengine.model;

import net.hawkengine.model.enums.PermissionType;

import java.util.UUID;

public class DbEntry {
    private final String id;
    private PermissionType permissionType;

    public DbEntry() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return this.id;
    }

    public PermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }
}
