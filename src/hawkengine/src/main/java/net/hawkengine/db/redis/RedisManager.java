package net.hawkengine.db.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {
	private static JedisPool pool;

	public static void connect() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		//poolConfig.setMaxTotal(20);
		poolConfig.setTestOnBorrow(true);
		pool = new JedisPool(poolConfig, "192.168.99.100");
	}

	public static JedisPool getPool() {
		return pool;
	}

	public static void release() {
		pool.destroy();
	}

	public static Jedis getJedis() {
		return pool.getResource();
	}
}
