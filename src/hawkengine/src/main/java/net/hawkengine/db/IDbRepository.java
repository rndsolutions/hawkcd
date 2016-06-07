package net.hawkengine.db;

import net.hawkengine.model.DbEntry;

import java.util.List;

public interface IDbRepository<T extends DbEntry> {
    T getById(String id) throws Exception;

    List<T> getAll() throws Exception;

	T add(T entry) throws Exception;

    T update(T entry) throws Exception;

	boolean delete(String id) throws Exception;
}
