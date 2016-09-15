package net.hawkengine.services.filters;

import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;

import java.util.List;

public class UserAuthorizationService implements IAuthorizationService {
    @Override
    public List getAll(List permissions, List entriesToFilter) {

        if (this.hasPermissionToGet(permissions)) {
            return entriesToFilter;
        }

        return null;
    }

    @Override
    public boolean getById(String entityId, List permissions) {

        if (this.hasPermissionToGet(permissions)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean add(String entity, List permissions) {
        return this.hasAdminPermission(permissions);
    }

    @Override
    public boolean update(String entity, List permissions) {
        return this.hasAdminPermission(permissions);
    }

    @Override
    public boolean delete(String entityId, List permissions) {
        return this.hasAdminPermission(permissions);
    }

    private boolean hasAdminPermission(List<Permission> permissions) {
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasPermissionToGet(List<Permission> permissions) {
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                return true;
            }
        }

        return false;
    }
}
