package net.hawkengine.services.interfaces;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.ServiceResult;

public interface ICrudService<T extends DbEntry> {
    ServiceResult getById(String id);

    ServiceResult getAll();

    ServiceResult add(T object);

    ServiceResult update(T object);

    ServiceResult delete(String id);

    void  enqueue(String name, String methodType, T entry);
}
