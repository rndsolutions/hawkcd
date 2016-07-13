package net.hawkengine;

import net.hawkengine.core.HawkServer;
import net.hawkengine.db.redis.RedisManager;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        HawkServer hawkServer;
        hawkServer = new HawkServer();
        try {
            hawkServer.configureJetty();
            hawkServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}