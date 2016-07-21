package net.hawkengine.services.filters;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationService<T extends DbEntry> implements IAuthorizationService{

    @Override
    public List<?> getAll(List<Permission> permissions, List<?> entitiesToFilter) {
        List<Object> result = new ArrayList<>();
        for (Object entityToFilter: entitiesToFilter) {
            T object = (T) entityToFilter;
            String entityToFilterId = object.getId();
            for (Permission permission: permissions) {
                if (permission.getPermittedEntityId().equals(entityToFilterId)){
                    if (permission.isAbleToGet()){
                        result.add(entityToFilter);
                    }
                }
            }
        }
        return result;
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
