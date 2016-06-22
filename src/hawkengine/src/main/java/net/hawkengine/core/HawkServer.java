package net.hawkengine.core;

import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.Account;
import net.hawkengine.http.AgentController;
import net.hawkengine.http.Config;
import net.hawkengine.http.Exec;
import net.hawkengine.http.Stats;
import net.hawkengine.ws.WsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class HawkServer {

    private Server server;
    private static final int PORT = 8080;

    public HawkServer() {
        this.server = new Server();
    }

    protected void configure() {

        //bind(IConfigService.class).to(ConfigService.class);
    }

    public void configureJetty() {

        // HTTP connector
        ServerConnector connector = new ServerConnector(this.server);
        connector.setPort(PORT);
        this.server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // REST
        ServletHolder restServlet = context.addServlet(ServletContainer.class, "/*");
        restServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        String classes = AgentController.class.getCanonicalName();

        restServlet.setInitParameter("jersey.config.server.provider.classnames", classes);

        // localhost:8080/ws/v1

        // WebSockets
        context.addServlet(WsServlet.class, "/ws/v1");

        this.server.setHandler(context);
    }

    public void start() throws Exception {

        this.server.start();
        this.server.join();
    }

    public void stop() {
        RedisManager.release();
    }
}