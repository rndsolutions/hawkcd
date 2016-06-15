package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.DbEntry;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.ICrudService;

import java.util.List;

public abstract class CrudService<T extends DbEntry> implements ICrudService<T> {
    private IDbRepository<T> repository;
    private String objectType;

    @Override
    public ServiceResult getById(String id) {
        T dbObject = this.getRepository().getById(id);

        ServiceResult result = new ServiceResult();
        if (dbObject != null) {
            result.setError(false);
            result.setMessage(this.getObjectType() + " " + id + " retrieved successfully.");
        } else {
            result.setError(true);
            result.setMessage(this.getObjectType() + " " + id + " not found.");
        }

        result.setObject(dbObject);

        return result;
    }

    @Override
    public ServiceResult getAll() {
        List<T> dbObjects = this.getRepository().getAll();

        ServiceResult result = new ServiceResult();
        result.setError(false);
        result.setMessage(this.getObjectType() + "s retrieved successfully.");
        result.setObject(dbObjects);

        return result;
    }

    @Override
    public ServiceResult add(T object) {
        T dbObject = this.getRepository().add(object);

        ServiceResult result = new ServiceResult();
        if (dbObject != null) {
            result.setError(false);
            result.setMessage(this.getObjectType() + " " + object.getId() + " created successfully.");
        } else {
            result.setError(true);
            result.setMessage(this.getObjectType() + " " + object.getId() + " already exists.");
        }

        result.setObject(dbObject);

        return result;
    }

    @Override
    public ServiceResult update(T object) {
        T dbObject = this.getRepository().update(object);

        ServiceResult result = new ServiceResult();
        if (dbObject != null) {
            result.setError(false);
            result.setMessage(this.getObjectType() + " " + object.getId() + " updated successfully.");
        } else {
            result.setError(true);
            result.setMessage(this.getObjectType() + " " + object.getId() + " not found.");
        }

        result.setObject(dbObject);

        return result;
    }

    @Override
    public ServiceResult delete(String id) {
        boolean isDeleted = this.getRepository().delete(id);

        ServiceResult result = new ServiceResult();
        if (isDeleted) {
            result.setObject(true);
            result.setError(false);
            result.setMessage(this.getObjectType() + " deleted successfully.");
        } else {
            result.setObject(false);
            result.setError(true);
            result.setMessage(this.getObjectType() + " not found.");
        }

        return result;
    }

    public IDbRepository<T> getRepository() {
        return this.repository;
    }

    public void setRepository(IDbRepository<T> repository) {
        this.repository = repository;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
}
