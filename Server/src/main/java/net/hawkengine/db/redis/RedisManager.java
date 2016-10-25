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

package net.hawkengine.db.redis;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.model.configuration.DatabaseConfig;
import net.hawkengine.model.enums.DatabaseType;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {
    private static JedisPool jedisPool;

    static JedisPool getJedisPool() {
        return jedisPool;
    }

    public static void connect() {
        DatabaseConfig config = ServerConfiguration.getConfiguration().getDatabaseConfigs().get(DatabaseType.REDIS);
        String host = config.getHost();
        int port = config.getPort();
        String password = config.getPassword();

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100);
        poolConfig.setBlockWhenExhausted(false);
        poolConfig.setTestOnBorrow(true);

        jedisPool = new JedisPool(poolConfig, host, port, 0, password);
    }

    public static void disconnect() {
        jedisPool.destroy();
    }
}