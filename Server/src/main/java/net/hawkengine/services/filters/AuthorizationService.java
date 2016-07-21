package net.hawkengine.services.filters;

import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;

import java.util.List;

public class AuthorizationService implements IAuthorizationService{
    @Override
    public List<?> getAll(List<Permission> permissions) {
        return null;
    }

    @Override
    public boolean getById(String entityId, List<Permission> permissions) {
        return false;
    }

    @Override
    public boolean add(String entityId, List<Permission> permissions) {
        return false;
    }

    @Override
    public boolean update(String entityId, List<Permission> permissions) {
        return false;
    }

    @Override
    public boolean delete(String entrityId, List<Permission> permissions) {
        return false;
    }
}
