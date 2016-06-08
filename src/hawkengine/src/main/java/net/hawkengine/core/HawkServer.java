package net.hawkengine.core;

import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.Account;
import net.hawkengine.http.Config;
import net.hawkengine.http.Exec;
import net.hawkengine.http.Stats;
import net.hawkengine.ws.WsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.websocket.DeploymentException;

public class HawkServer {

    private Server server;

    public HawkServer() {
        server = new Server();
    }

    protected void configure() {

        //bind(IConfigService.class).to(ConfigService.class);
    }

    public void configureJetty() throws ServletException, DeploymentException {

        // HTTP connector
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // REST
        ServletHolder restServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        restServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        StringBuilder classes = new StringBuilder();
        classes.append(Account.class.getCanonicalName() + ", ");
        classes.append(Config.class.getCanonicalName() + ", ");
        classes.append(Stats.class.getCanonicalName() + ", ");
        classes.append(Exec.class.getCanonicalName() + ", ");

        restServlet.setInitParameter("jersey.config.server.provider.classnames", classes.toString());

        // localhost:8080/ws/v1

        // WebSockets
        context.addServlet(WsServlet.class, "/ws/v1");

        server.setHandler(context);
    }

    public void start() throws Exception {

        server.start();
        server.join();
    }

    public void stop() {
        RedisManager.release();
    }
}