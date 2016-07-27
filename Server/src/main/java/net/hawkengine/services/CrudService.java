package net.hawkengine.services;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.DbEntry;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.enums.DatabaseType;
import net.hawkengine.services.interfaces.ICrudService;

import java.util.List;

public abstract class CrudService<T extends DbEntry> extends Service<T> implements ICrudService<T> {
    static final DatabaseType DATABASE_TYPE = ServerConfiguration.getConfiguration().getDatabaseType();

    private IDbRepository<T> repository;

    public IDbRepository<T> getRepository() {
        return this.repository;
    }

    public void setRepository(IDbRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public ServiceResult getById(String id) {
        T dbObject = this.getRepository().getById(id);

        ServiceResult result = new ServiceResult();
        if (dbObject != null) {
            result = super.createServiceResult(dbObject, false, "retrieved successfully");
        } else {
            result = super.createServiceResult(dbObject, true, "not found");
        }

        return result;
    }

    @Override
    public ServiceResult getAll() {
        List<T> dbObjects = this.getRepository().getAll();

        ServiceResult result = super.createServiceResultArray(dbObjects, false, "retrieved successfully");

        return result;
    }

    @Override
    public ServiceResult add(T object) {
        T dbObject = this.getRepository().add(object);

        ServiceResult result = new ServiceResult();
        if (dbObject != null) {
            result = super.createServiceResult(dbObject, false, "created successfully");
        } else {
            result = super.createServiceResult(dbObject, true, "already exists");
        }

        return result;
    }

    @Override
    public ServiceResult update(T object) {
        if (object == null){
            return super.createServiceResult(object, true, "not found");
        }
        T dbObject = this.getRepository().update(object);

        ServiceResult result = new ServiceResult();
        if (dbObject != null) {
            result = super.createServiceResult(dbObject, false, "updated successfully");
        } else {
            result = super.createServiceResult(dbObject, true, "not found");
        }

        return result;
    }

    @Override
    public ServiceResult delete(String id) {
        T dbObject = this.getRepository().delete(id);

        ServiceResult result = new ServiceResult();
        if (dbObject == null) {
            result = super.createServiceResult((T) result.getObject(), true, "not found" );
        } else {
            result = super.createServiceResult((T) result.getObject(), false, "deleted successfully");
        }

        result.setObject(dbObject);

        return result;
    }
}
