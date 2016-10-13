package net.hawkengine.ws;

import com.google.gson.Gson;

import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.services.AgentService;

import java.util.List;
import java.util.Objects;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * In charge of managing websockets connections
 */
public class ConnectionWorker implements  Runnable {

    private JedisPool jedisPool;

    public ConnectionWorker(){
        //RedisManager.connect();
        jedisPool = RedisManager.getJedisPool();
    }

    @Override
    public void run() {

        while (true){

            try {

                try(Jedis resource = jedisPool.getResource()){
                    List<String> messageList = resource.blpop(0,"queue");


                    EndpointConnector.passResultToEndpoint(AgentService.class.getSimpleName(), "update", result);

                    for (String s : messageList) {
                        System.out.println(s);
                    }
                }

                Thread.sleep(600);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
