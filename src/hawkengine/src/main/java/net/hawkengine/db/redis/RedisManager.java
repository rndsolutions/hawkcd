package net.hawkengine.db.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {
    private static JedisPool jedisPool;

    public static void connect() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        //poolConfig.setMaxTotal(20);
        poolConfig.setTestOnBorrow(true);
        jedisPool = new JedisPool(poolConfig, "192.168.99.100");
    }

    public static JedisPool getJedisPool() {
        return jedisPool;
    }

    public static void release() {
        jedisPool.destroy();
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }
}
