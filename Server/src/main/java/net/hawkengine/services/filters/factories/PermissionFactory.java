package net.hawkengine.services.filters.factories;

import net.hawkengine.model.User;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.PermissionService;
import net.hawkengine.services.filters.interfaces.IPermissionService;

import java.util.List;

public class PermissionFactory {
    private IPermissionService permissionService;

    public PermissionFactory() {
        this.permissionService = new PermissionService();
    }

    public List<Permission> distributePermissions(Permission permission, User user) {
        switch (permission.getPermissionScope()) {
            case SERVER:
                return this.permissionService.distributeServerPermissions(permission, user);
            case PIPELINE_GROUP:
                return this.permissionService.distributePipelineGroupPermissions(permission, user);
            case PIPELINE:
                return this.permissionService.distributePipelinePermissions(permission, user);
            case ENVIRONMENT:
                return this.permissionService.distributeEnvironmentPermissions(permission, user);
            default:
                return null;
        }
    }
}
