package net.hawkengine.services.interfaces;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.enums.NotificationType;

import java.util.List;

public interface IService<T extends DbEntry> {
    ServiceResult createServiceResult(T object, NotificationType notificationType, String messsage);

    ServiceResult createServiceResultArray(List<?> object, NotificationType notificationType, String messsage);
}
