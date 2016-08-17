package net.hawkengine.services.filters;

import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;

import java.util.List;

public class UserGroupAuthorizationService implements IAuthorizationService {
    @Override
    public List getAll(List permissions, List entriesToFilter) {
        if (this.hasPermission(permissions)){
            return entriesToFilter;
        }
        return null;
    }

    @Override
    public boolean getById(String entityId, List permissions) {
        return this.hasPermission(permissions);
    }

    @Override
    public boolean add(String entity, List permissions) {
        return this.hasPermission(permissions);
    }

    @Override
    public boolean update(String entity, List permissions) {
        return this.hasPermission(permissions);
    }

    @Override
    public boolean delete(String entityId, List permissions) {
        return this.hasPermission(permissions);
    }

    private boolean hasPermission(List<Permission> permissions) {
        for (Permission permission : permissions) {
            if (permission.getPermissionScope() == PermissionScope.SERVER && permission.getPermissionType() == PermissionType.ADMIN) {
                return true;
            }
        }
        return false;
    }
}
