package net.hawkengine.db.redis;

import java.io.IOException;
import java.net.URISyntaxException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.embedded.RedisServer;

public class RedisManager {
    private static JedisPool jedisPool;
    private static RedisServer redisEmbededDb;

    public static void connect(String serverName) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        //poolConfig.setMaxTotal(20);
        poolConfig.setTestOnBorrow(true);
        jedisPool = new JedisPool(poolConfig, serverName);

    }

    public static void  initializeEmbededDb(int port) throws IOException, URISyntaxException {
        redisEmbededDb = RedisServer.builder()
                .port(port)
             //   .setting("daemonize yes")
             //   .setting("appendonly yes")
             //   .setting("maxheap 128M")
                .build();
    }

    public static void startEmbededDb() throws IOException {
      redisEmbededDb.start();
    }

    public static void stopEmbededDb() throws InterruptedException {
        redisEmbededDb.stop();
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
