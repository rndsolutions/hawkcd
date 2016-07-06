package net.hawkengine.db.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.DbEntry;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.TaskDefinition;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RedisRepository<T extends DbEntry> implements IDbRepository<T> {
    private Type type;
    private String entryNamespace;
    private String idNamespace;
    private Gson jsonConverter;
    private JedisPool jedisPool;

    public RedisRepository(Class<T> type) {
        this.type = type;
        this.entryNamespace = String.format("%s:%s", "Entries", type.getSimpleName());
        this.idNamespace = String.format("%s:%s", "Ids", type.getSimpleName());
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        this.jedisPool = RedisManager.getJedisPool();

    }

    public RedisRepository(Class<T> entry, JedisPool pool) {
        this(entry);
        this.jedisPool = pool;
    }

    @Override
    public T getById(String id) {
        T result;
        try (Jedis jedis = this.jedisPool.getResource()) {
            String entryKey = String.format("%s:%s", this.entryNamespace, id);
            String entryValue = jedis.get(entryKey);

            result = this.jsonConverter.fromJson(entryValue, this.type);
        }

        return result;
    }

    @Override
    public List<T> getAll() {
        List<T> result;
        try (Jedis jedis = this.jedisPool.getResource()) {
            Set<String> entitiesIds = jedis.smembers(this.idNamespace);

            result = new ArrayList<>();
            for (String id : entitiesIds) {
                T entry = this.getById(id);
                result.add(entry);
            }
        }

        return result;
    }

    @Override
    public T add(T entry) {
        T result = null;
        try (Jedis jedis = this.jedisPool.getResource()) {
            T existingObject = this.getById(entry.getId());
            if (existingObject == null) {
                String entryKey = String.format("%s:%s", this.entryNamespace, entry.getId());
                String entryValue = jedis.set(entryKey, this.jsonConverter.toJson(entry));
                Long entryId = jedis.sadd(this.idNamespace, entry.getId());
                result = this.getById(entry.getId());
            }
        }

        return result;
    }

    @Override
    public T update(T entry) {
        T result = null;
        try (Jedis jedis = this.jedisPool.getResource()) {
            T existingObject = this.getById(entry.getId());
            if (existingObject != null) {
                String entryKey = String.format("%s:%s", this.entryNamespace, entry.getId());
                String entryValue = jedis.set(entryKey, this.jsonConverter.toJson(entry));
                Long entryId = jedis.sadd(this.idNamespace, entry.getId());
                result = this.getById(entry.getId());
            }
        }

        return result;
    }

    @Override
    public T delete(String id) {
        T result = null;
        try (Jedis jedis = this.jedisPool.getResource()) {
            result= this.getById(id);
            if (result != null) {
                String entryKey = String.format("%s:%s", this.entryNamespace, id);
                jedis.del(entryKey);
                jedis.srem(this.idNamespace, id);
            }
        }

        return result;
    }
}
