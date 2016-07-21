package net.hawkengine.core;

import net.hawkengine.core.materialhandler.MaterialTracker;
import net.hawkengine.core.pipelinescheduler.JobAssigner;
import net.hawkengine.core.pipelinescheduler.PipelinePreparer;
import net.hawkengine.core.utilities.EndpointFinder;
import net.hawkengine.db.redis.RedisManager;
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
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.URISyntaxException;

public class HawkServer {
    private static final int PORT = ServerConfiguration.getConfiguration().getServerPort();

    private Server server;
    private Thread pipelinePreparer;
    private Thread jobAssigner;
    private Thread materialTracker;
    private EndpointFinder endpointFinder;
    private RedisServer redisServer;

    public HawkServer() throws IOException, URISyntaxException {
        RedisManager.initializeEmbededDb(6379);
        RedisManager.startEmbededDb();
        RedisManager.connect(ServerConfiguration.getConfiguration().getServerHost());

        this.server = new Server();
        this.pipelinePreparer = new Thread(new PipelinePreparer());
        this.jobAssigner = new Thread(new JobAssigner());
        this.materialTracker = new Thread(new MaterialTracker());
        this.endpointFinder = new EndpointFinder();
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
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setResourceBase(this.getClass().getResource("/dist").toExternalForm());

        // REST

        ServletHolder restServlet = context.addServlet(ServletContainer.class, "/*");
        restServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        String classes = this.endpointFinder.getClasses("net.hawkengine.http");
        restServlet.setInitParameter("jersey.config.server.provider.classnames", classes);

        // localhost:8080/ws/v1

        // WebSockets

        context.addServlet(WsServlet.class, "/ws/v1");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, context, new DefaultHandler()});

        this.server.setHandler(handlers);
    }

    public void start() throws Exception {
        this.server.start();
        this.pipelinePreparer.start();
        this.jobAssigner.start();
        this.materialTracker.start();
        this.server.join();
    }

    public void stop() throws InterruptedException {
        RedisManager.release();
        RedisManager.stopEmbededDb();
    }
}