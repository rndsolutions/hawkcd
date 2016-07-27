package net.hawkengine.db.redis;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.model.configuration.DatabaseConfig;
import net.hawkengine.model.enums.DatabaseType;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.embedded.RedisServer;

public class RedisManager {
    private static RedisServer redisEmbeddedDb;
    private static JedisPool jedisPool;

    static JedisPool getJedisPool() {
        return jedisPool;
    }

    public static void connect() {
        DatabaseConfig config = ServerConfiguration.getConfiguration().getDatabaseConfigs().get(DatabaseType.REDIS);
        String host = config.getHost();
        int port = config.getPort();
        String password = config.getPassword();

        redisEmbeddedDb = RedisServer.builder().port(port).build();

        boolean shouldUseEmbeddedDb = ServerConfiguration.getConfiguration().shouldUseEmbeddedDb();
        if (shouldUseEmbeddedDb) {
            redisEmbeddedDb.start();
        }

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setTestOnBorrow(true);

        jedisPool = new JedisPool(poolConfig, host, port, 0, password);
    }

    public static void disconnect() {
        redisEmbeddedDb.stop();
        jedisPool.destroy();
    }
}
