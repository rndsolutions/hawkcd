package net.hawkengine.services.filters.interfaces;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.payload.Permission;

import java.util.List;

public interface IAuthorizationService<T extends DbEntry> {
    List<T> getAll(List<Permission> permissions, List<?> entriesToFilter);

    boolean getById(String entityId, List<Permission> permissions);

    boolean add(String entity, List<Permission> permissions);

    boolean update(String entity, List<Permission> permissions);

    boolean delete(String entity, List<Permission> permissions);
}
