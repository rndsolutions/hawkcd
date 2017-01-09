/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hawkcd.db.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.hawkcd.utilities.deserializers.MaterialDefinitionAdapter;
import io.hawkcd.utilities.deserializers.TaskDefinitionAdapter;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.model.Entity;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.TaskDefinition;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RedisRepository<T extends Entity> implements IDbRepository<T> {
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
        if (id == null) {
            return null;
        }

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
        if (entry == null) {
            return null;
        }

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
        if (entry == null) {
            return null;
        }

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
        if (id == null) {
            return null;
        }

        T result;
        try (Jedis jedis = this.jedisPool.getResource()) {
            result = this.getById(id);
            if (result != null) {
                String entryKey = String.format("%s:%s", this.entryNamespace, id);
                jedis.del(entryKey);
                jedis.srem(this.idNamespace, id);
            }
        }

        return result;
    }
}
