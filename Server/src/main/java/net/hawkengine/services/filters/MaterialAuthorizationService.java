package net.hawkengine.services.filters;

import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;

import java.util.List;

public class MaterialAuthorizationService implements IAuthorizationService {
    @Override
    public List getAll(List permissions, List entriesToFilter) {
        return entriesToFilter;
    }

    @Override
    public boolean getById(String entityId, List permissions) {
        return true;
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
    public boolean delete(String entity, List permissions) {
        return this.hasPermission(permissions);
    }

    private boolean hasPermission(List<Permission> permissions) {
        return true;

//        for (Permission permission : permissions) {
//            if (permission.getPermissionScope() == PermissionScope.SERVER && permission.getPermissionType() == PermissionType.ADMIN) {
//                return true;
//            }
//        }
//        return false;
    }
}
