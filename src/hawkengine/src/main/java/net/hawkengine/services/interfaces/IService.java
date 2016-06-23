package net.hawkengine.services.interfaces;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.ServiceResult;

import java.util.List;

public interface IService<T extends DbEntry> {
    ServiceResult createServiceResult(T object, boolean hasErrors, String messsage);

    ServiceResult createServiceResultArray(List<?> object, boolean hasErrors, String messsage);
}
