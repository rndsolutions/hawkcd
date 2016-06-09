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
        T dbCall = null;
        try {
            dbCall = this.repository.getById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServiceResult result = new ServiceResult();
        if(dbCall != null){
            result.setError(false);
            result.setMessage(dbCall.getClass().getSimpleName() + " with id " + dbCall.getId() + " retrieved successfully.");
            result.setObject(dbCall);
        }
        else{
            result.setError(true);
            result.setMessage(dbCall.getClass().getSimpleName() + " with id " + dbCall.getId() + " already exists.");
            result.setObject(null);
        }

        return result;
    }

    @Override
    public ServiceResult getAll() {
        List<T> dbCall = null;
        try {
            dbCall = this.repository.getAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServiceResult result = new ServiceResult();
        result.setError(false);
        if(dbCall != null){
            result.setMessage(dbCall.get(0).getClass().getSimpleName() + "s retrieved successfully.");
        }
        result.setObject(dbCall);

        return result;
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
        if(dbCall != null){
            result.setError(false);
            result.setMessage(dbCall.getClass().getSimpleName() + " with id " + dbCall.getId() + " retrieved successfully.");
            result.setObject(dbCall);
        }
        else{
            result.setError(true);
            result.setMessage(dbCall.getClass().getSimpleName() + " with id " + dbCall.getId() + " already exists.");
            result.setObject(null);
        }

        return result;
    }

    @Override
    public ServiceResult update(T object) {
        T dbCall = null;
        try {
            dbCall = this.repository.update(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServiceResult result = new ServiceResult();
        if(dbCall != null){
            result.setError(false);
            result.setMessage(dbCall.getClass().getSimpleName() + " with id " + dbCall.getId() + " updated successfully.");
            result.setObject(dbCall);
        }
        else{
            result.setError(true);
            result.setMessage(dbCall.getClass().getSimpleName() + " with id " + dbCall.getId() + " already exists.");
            result.setObject(null);
        }

        return result;
    }

    @Override
    public ServiceResult delete(String id) {
        boolean dbCall = false;
        try {
            dbCall = this.repository.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServiceResult result = new ServiceResult();
        if(dbCall){
            result.setError(false);
            result.setMessage("Object with id " + id + " deleted successfully.");
            result.setObject(id);
        }
        else{
            result.setError(true);
            result.setMessage("Object with id " + id + " not found.");
            result.setObject(null);
        }

        return result;
    }
}
