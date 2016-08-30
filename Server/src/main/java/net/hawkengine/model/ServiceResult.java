package net.hawkengine.model;

import net.hawkengine.model.enums.NotificationType;

public class ServiceResult {
    private Object object;
    private NotificationType notificationType;
    private String message;

    public ServiceResult(){
        
    }

    public ServiceResult(Object object, NotificationType notificationType, String message) {
        this.object = object;
        this.notificationType = notificationType;
        this.message = message;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public NotificationType getNotificationType() {
        return this.notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
