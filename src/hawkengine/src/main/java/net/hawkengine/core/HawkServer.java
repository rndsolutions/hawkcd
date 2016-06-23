package net.hawkengine.core;

import net.hawkengine.core.components.pipelinescheduler.JobAssigner;
import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.AgentController;
import net.hawkengine.ws.WsServlet;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class HawkServer {
    private static final int PORT = 8080;

    private Server server;
    private Thread jobAssigner;

    public HawkServer() {
        RedisManager.connect();
        this.server = new Server();
        this.jobAssigner = new Thread(new JobAssigner());
    }

    public void configureJetty() {
        // HTTP connector
        ServerConnector connector = new ServerConnector(this.server);
        connector.setPort(PORT);
        this.server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
      //  resourceHandler.setResourceBase(this.getClass().getResource("/dist").toExternalForm());

        // REST

        ServletHolder restServlet = context.addServlet(ServletContainer.class, "/*");
        restServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        String classes = AgentController.class.getCanonicalName();
        restServlet.setInitParameter("jersey.config.server.provider.classnames", classes);

        // localhost:8080/ws/v1


        // WebSockets

        context.addServlet(WsServlet.class, "/ws/v1");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, context, new DefaultHandler() });

        this.server.setHandler(handlers);
    }

    public void start() throws Exception {
        jobAssigner.start();
        this.server.start();
        this.server.join();
    }

    public void stop() {
        RedisManager.release();
    }
}