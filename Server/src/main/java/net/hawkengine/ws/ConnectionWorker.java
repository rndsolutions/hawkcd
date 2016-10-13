package net.hawkengine.ws;

import net.hawkengine.db.redis.RedisManager;

import java.util.Objects;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * In charge of managing websockets connections
 */
public class ConnectionWorker {

    private JedisPool jedisPool;

    public ConnectionWorker(){
        //RedisManager.connect();
        jedisPool = RedisManager.getJedisPool();
    }

    public void update(){
        Jedis connection = jedisPool.getResource();
    }

    public void add(String obj){
        Jedis connection = jedisPool.getResource();
        connection.rpush("queue", obj);
    }

}
