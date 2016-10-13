package net.hawkengine.core;

import net.hawkengine.core.materialhandler.MaterialTracker;
import net.hawkengine.core.pipelinescheduler.JobAssigner;
import net.hawkengine.core.pipelinescheduler.PipelinePreparer;
import net.hawkengine.core.utilities.DataImporter;
import net.hawkengine.core.utilities.EndpointFinder;
import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.ws.ConnectionWorker;
import net.hawkengine.ws.WsServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.servlet.ServletContainer;

public class HawkServer {
    private Server server;
    private Thread pipelinePreparer;
    private Thread jobAssigner;
    private Thread materialTracker;
    private DataImporter dataImporter;
    private Thread connectionWorker;

    public HawkServer() {
        RedisManager.connect();

        //setting server thread pool
        final QueuedThreadPool threadPool=new QueuedThreadPool(10);
        //threadPool.setStopTimeout();
        //threadPool.setMinThreads(3);
        //threadPool.setMaxThreads(10);
        threadPool.setName("http-worker");
        this.server = new Server(threadPool);

        this.pipelinePreparer = new Thread(new PipelinePreparer(), "PipelineScheduler");
        this.jobAssigner = new Thread(new JobAssigner(),"JobAssigner");
        this.materialTracker = new Thread(new MaterialTracker(), "MaterialTracker");
        this.dataImporter = new DataImporter();
        this.connectionWorker =  new Thread(new ConnectionWorker(), "ConnectionWorker");
    }

    public void configureJetty() {



        //server.setThreadPool(threadPool);

        // HTTP connector
        ServerConnector connector = new ServerConnector(this.server);
        int port = ServerConfiguration.getConfiguration().getServerPort();
        connector.setPort(port);
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
//        String classes = this.endpointFinder.getClasses("net.hawkengine.http");
        restServlet.setInitParameter("jersey.config.server.provider.packages", "net.hawkengine.http");
//        restServlet.setInitParameter(ServerProperties.PROVIDER_PACKAGES,
//                "com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;" + "net.hawkengine.http");


        // localhost:8080/ws/v1

        // WebSockets

        context.addServlet(WsServlet.class, "/ws/v1");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, context, new DefaultHandler()});

        this.server.setHandler(handlers);
    }

    public void start() throws Exception {
        this.server.start();
        this.dataImporter.importDefaultEntities();
        this.pipelinePreparer.start();
        this.jobAssigner.start();
        this.materialTracker.start();
        this.connectionWorker.start();
        this.server.join();
    }

    public void stop() {
        RedisManager.disconnect();
    }
}