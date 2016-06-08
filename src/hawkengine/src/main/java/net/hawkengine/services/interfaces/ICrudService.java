package net.hawkengine.services.interfaces;

import net.hawkengine.model.ServiceResult;

import java.util.List;

public interface ICrudService {
    ServiceResult getById(String id);

    List<ServiceResult> getAll();

    ServiceResult add(Object object);

    ServiceResult updateAgent(Object object);

    ServiceResult deleteAgent(String id);
}
