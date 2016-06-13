package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.DbEntry;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.ICrudService;

import java.util.List;

public abstract class CrudService<T extends DbEntry> implements ICrudService<T> {
    protected IDbRepository<T> repository;
    protected String objectType;

    @Override
    public ServiceResult getById(String id) {
        T dbObject = this.repository.getById(id);

        ServiceResult result = new ServiceResult();
        if (dbObject != null) {
            result.setError(false);
            result.setMessage(this.objectType + " " + id + " retrieved successfully.");
        } else {
            result.setError(true);
            result.setMessage(this.objectType + " " + id + " not found.");
        }

        result.setObject(dbObject);

        return result;
    }

    @Override
    public ServiceResult getAll() {
        List<T> dbObjects = this.repository.getAll();

        ServiceResult result = new ServiceResult();
        result.setError(false);
        result.setMessage(this.objectType + "s retrieved successfully.");
        result.setObject(dbObjects);

        return result;
    }

    @Override
    public ServiceResult add(T object) {
        T dbObject = this.repository.add(object);

        ServiceResult result = new ServiceResult();
        if (dbObject != null) {
            result.setError(false);
            result.setMessage(this.objectType + " " + object.getId() + " created successfully.");
        } else {
            result.setError(true);
            result.setMessage(this.objectType + " " + object.getId() + " already exists.");
        }

        result.setObject(dbObject);

        return result;
    }

    @Override
    public ServiceResult update(T object) {
        T dbObject = this.repository.update(object);

        ServiceResult result = new ServiceResult();
        if (dbObject != null) {
            result.setError(false);
            result.setMessage(this.objectType + " " + object.getId() + " updated successfully.");
        } else {
            result.setError(true);
            result.setMessage(this.objectType + " " + object.getId() + " not found.");
        }

        result.setObject(dbObject);

        return result;
    }

    @Override
    public ServiceResult delete(String id) {
        boolean isDeleted = this.repository.delete(id);

        ServiceResult result = new ServiceResult();
        if (isDeleted) {
            result.setError(false);
            result.setMessage(this.objectType + " deleted successfully.");
        } else {
            result.setError(true);
            result.setMessage(this.objectType + " not found.");
        }

        return result;
    }
}
