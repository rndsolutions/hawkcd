package net.hawkengine.services.filters.interfaces;

import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.payload.Permission;

import java.util.List;

public interface IAuthorizationService {
    List<?> getAll(List<Permission> permissions, List<?> entritiesToFilter);

    boolean getById(String entityId, List<Permission> permissions);

    boolean add(String entityId, List<Permission> permissions);

    boolean update(String entityId, List<Permission> permissions);

    boolean delete(String entrityId, List<Permission> permissions);

    List<PipelineDefinition> getAllPipelineDefinitions (List<Permission> permissions, List<PipelineDefinition> entitiesToFilter);
}
