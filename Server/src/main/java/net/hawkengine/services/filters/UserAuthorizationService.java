package net.hawkengine.services.filters;

import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;

import java.util.List;

public class UserAuthorizationService implements IAuthorizationService {
    @Override
    public List getAll(List permissions, List entriesToFilter) {
        List <Permission> permisssions = permissions;
        for (Permission permission : permisssions) {
            if (permission.getPermissionScope() == PermissionScope.SERVER) {
                if (permission.getPermissionType() == PermissionType.VIEWER || permission.getPermissionType() == PermissionType.OPERATOR) {

                    return entriesToFilter;
                }
            }
        }

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
