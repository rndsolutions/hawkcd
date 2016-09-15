package net.hawkengine.services.filters;

import net.hawkengine.services.filters.interfaces.IAuthorizationService;

import java.util.List;

/**
 * Created by Margarita Ivancheva on 9/15/2016.
 */
public class StageAutorizationService implements IAuthorizationService {
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
        return true;
    }

    @Override
    public boolean update(String entity, List permissions) {
        return true;
    }

    @Override
    public boolean delete(String entityId, List permissions) {
        return true;
    }
}
