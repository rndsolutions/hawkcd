package net.hawkengine.services.interfaces;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.ServiceResult;

import java.util.List;

public interface ICrudService<T extends DbEntry> {
    ServiceResult getById(String id);

    List<ServiceResult> getAll();

    ServiceResult add(T object);

    ServiceResult update(T object);

    ServiceResult delete(String id);
}
