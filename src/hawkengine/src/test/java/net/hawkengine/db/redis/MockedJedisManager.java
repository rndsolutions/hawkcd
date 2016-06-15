package net.hawkengine.db.redis;

import com.fiftyonred.mock_jedis.MockJedis;
import com.fiftyonred.mock_jedis.MockJedisPool;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MockedJedisManager {


    @SuppressWarnings("deprecation")
    public static MockJedis getResource() {
        JedisPool pool = new MockJedisPool(new JedisPoolConfig(), "test");
        //Jedis jedis = pool.getResource();
        return (MockJedis) pool.getResource();

    }


}
