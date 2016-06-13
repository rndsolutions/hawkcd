package net.hawkengine;

import net.hawkengine.core.HawkServer;
import net.hawkengine.db.redis.RedisManager;

import javax.servlet.ServletException;
import javax.websocket.DeploymentException;

public class Main {

    public static void main(String[] args) {
        HawkServer hawk_server = new HawkServer();
        try {
            RedisManager.connect();
            hawk_server.configureJetty();
            hawk_server.start();

        } catch (ServletException | DeploymentException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
