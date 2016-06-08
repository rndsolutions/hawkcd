package net.hawkengine;

import javax.servlet.ServletException;
import javax.websocket.DeploymentException;

import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.core.HawkServer;

public class Main {

	public static void main(String[] args) {

		// Server server = new Server(8080);
		// server.setHandler(new HawkServer());

		HawkServer hawk_server = new HawkServer();

		try {

			RedisManager.connect();
			hawk_server.configureJetty();
			hawk_server.start();

		} catch (ServletException | DeploymentException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
