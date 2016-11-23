package io.hawkcd.core.security;

import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;

public class Grant {
    PermissionScope permissionScope;
    PermissionType permissionType;
    String permittedEntityId;

    public String getPermittedEntityId() {
        return permittedEntityId;
    }

    public void setPermittedEntityId(String permittedEntityId) {
        this.permittedEntityId = permittedEntityId;
    }

    public Grant(PermissionScope scope, PermissionType type) {
        this.permissionScope = scope;
        this.permissionType = type;
    }

    public Grant(Authorization authorization) {
        this.permissionScope = authorization.scope();
        this.permissionType = authorization.type();
    }

    public PermissionScope getScope() {
        return permissionScope;
    }

    public void setScope(PermissionScope scope) {
        this.permissionScope = scope;
    }

    public PermissionType getType() {
        return permissionType;
    }

    public void setType(PermissionType type) {
        this.permissionType = type;
    }

    public boolean isGreaterThan(Grant grant) {
        boolean result = (this.getScope().getPriorityLevel() <= grant.getScope().getPriorityLevel())
                && (this.getType().getPriorityLevel() <= grant.getType().getPriorityLevel());
        return result;
    }
}
