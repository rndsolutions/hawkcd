package io.hawkcd.core.security;

import io.hawkcd.model.enums.PermissionEntity;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;

public class AuthorizationGrant {
    private PermissionScope permissionScope;
    private PermissionType permissionType;
    private PermissionEntity permissionEntity;
    private String permittedEntityId;

    public AuthorizationGrant(PermissionScope scope, PermissionType type) {
        this.permissionScope = scope;
        this.permissionType = type;
    }

    public AuthorizationGrant(PermissionScope permissionScope, PermissionType permissionType, PermissionEntity permissionEntity) {
        this(permissionScope, permissionType);
        this.permissionEntity = permissionEntity;
    }

    public AuthorizationGrant(Authorization authorization) {
        this.permissionScope = authorization.scope();
        this.permissionType = authorization.type();
    }

    public PermissionScope getPermissionScope() {
        return this.permissionScope;
    }

    public PermissionType getPermissionType() {
        return this.permissionType;
    }

    public PermissionEntity getPermissionEntity() {
        return this.permissionEntity;
    }

    public String getPermittedEntityId() {
        return this.permittedEntityId;
    }

    public void setPermittedEntityId(String permittedEntityId) {
        this.permittedEntityId = permittedEntityId;
    }

    boolean isGreaterThan(AuthorizationGrant grant) {
        boolean result = this.getPermissionScope().getPriorityLevel() <= grant.getPermissionScope().getPriorityLevel() &&
                this.getPermissionType().getPriorityLevel() <= grant.getPermissionType().getPriorityLevel();

        return result;
    }

    boolean isDuplicateWith(AuthorizationGrant grant) {
        boolean result = this.getPermissionScope().getPriorityLevel() == grant.getPermissionScope().getPriorityLevel() &&
                this.getPermissionEntity().getPriorityLevel() == grant.getPermissionEntity().getPriorityLevel() &&
                this.getPermittedEntityId().equals(grant.getPermittedEntityId());

        return result;
    }
}
