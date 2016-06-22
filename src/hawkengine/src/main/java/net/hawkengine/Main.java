package net.hawkengine;

import net.hawkengine.core.HawkServer;
import net.hawkengine.db.redis.RedisManager;

public class Main {

    public static void main(String[] args) {
        HawkServer hawkServer = new HawkServer();
        try {
            hawkServer.configureJetty();
            hawkServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
