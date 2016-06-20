package net.hawkengine.services;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.IService;

import java.util.List;

public class Service<T extends DbEntry> implements IService<T> {
    private String objectType;

    @Override
    public ServiceResult createServiceResult(T object, boolean hasErrors, String messsage) {
        ServiceResult result = new ServiceResult();
        result.setError(hasErrors);
        result.setObject(object);
        if (object != null) {
            result.setMessage(this.getObjectType() + " " + object.getId() + " " + messsage + ".");
        }else {
            result.setMessage(this.getObjectType() + " " + messsage + ".");
        }
        return result;
    }

    @Override
    public ServiceResult createServiceResultArray(List<?> object, boolean hasErrors, String messsage) {
        ServiceResult result = new ServiceResult();
        result.setError(hasErrors);
        result.setObject(object);
        result.setMessage(this.getObjectType() + "s " + messsage + ".");

        return result;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
}
