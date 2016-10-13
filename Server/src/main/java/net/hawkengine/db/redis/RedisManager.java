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