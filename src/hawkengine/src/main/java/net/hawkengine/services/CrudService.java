package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.DbEntry;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.ICrudService;

import java.util.List;

public abstract class CrudService<T extends DbEntry> implements ICrudService<T> {
    protected IDbRepository<T> repository;

    public void setRepository(IDbRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public ServiceResult getById(String id) {
        return null;
    }

    @Override
    public List<ServiceResult> getAll() {
        return null;
    }

    @Override
    public ServiceResult add(T object) {
        T dbCall = null;
        try {
            dbCall = this.repository.add(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ServiceResult result = new ServiceResult();
        result.setObject(dbCall);
        return result;
    }

    @Override
    public ServiceResult update(T object) {
        return null;
    }

    @Override
    public ServiceResult delete(String id) {
        return null;
    }
}
