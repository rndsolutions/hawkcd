package net.hawkengine.services;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.services.interfaces.IService;

import java.util.List;

public abstract class Service<T extends DbEntry> implements IService<T> {
    private String objectType;

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    @Override
    public ServiceResult createServiceResult(T object, NotificationType notificationType, String messsage) {
        String resultMessage;
        if (notificationType == NotificationType.SUCCESS) {
            if (object == null) {
                resultMessage = this.getObjectType() + " " + messsage + ".";
            } else {
                resultMessage = this.getObjectType() + " " + object.getId() + " " + messsage + ".";
            }
        } else {
            resultMessage = this.getObjectType() + " " + messsage + ".";
        }
        ServiceResult result = new ServiceResult(object, notificationType, resultMessage);

        return result;
    }

    @Override
    public ServiceResult createServiceResultArray(List<?> object, NotificationType notificationType, String messsage) {
        String resultMessage = this.getObjectType() + "s " + messsage + ".";
        ServiceResult result = new ServiceResult(object, notificationType, resultMessage);

        return result;
    }
}
