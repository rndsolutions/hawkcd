package net.hawkengine.db;

import net.hawkengine.model.DbEntry;

import java.util.List;

@SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
public interface IDbRepository<T extends DbEntry> {
    T getById(String id);

    List<T> getAll();

    T add(T entry);

    T update(T entry);

    boolean delete(String id);
}
