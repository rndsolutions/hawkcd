/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hawkcd;

import io.hawkcd.core.config.Config;
import io.hawkcd.http.PipelineController;
import io.hawkcd.materials.MaterialTracker;
import io.hawkcd.core.subscriber.SubscriberComponent;
import io.hawkcd.scheduler.JobAssigner;
import io.hawkcd.scheduler.PipelinePreparer;
import io.hawkcd.utilities.Initializer;
import io.hawkcd.db.redis.RedisManager;
import io.hawkcd.ws.WsServlet;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class HServer {
    private Server server;
    private Thread pipelinePreparer;
    private Thread jobAssigner;
    private Thread materialTracker;
    private Thread subsciber;
    private Initializer initializer;

    public HServer() {
        RedisManager.connect();

        //setting server thread pool
        final QueuedThreadPool threadPool = new QueuedThreadPool(100);
        threadPool.setName("http-worker");
        this.server = new Server(threadPool);

        this.pipelinePreparer = new Thread(new PipelinePreparer(), "PipelineScheduler");
        this.jobAssigner = new Thread(new JobAssigner(), "JobAssigner");
        this.materialTracker = new Thread(new MaterialTracker(), "MaterialTracker");
        this.subsciber = new Thread(new SubscriberComponent(), "SubscriberComponent");
        this.initializer = new Initializer();
    }

    public void configureJetty() throws Exception {
        // HTTP connector
        ServerConnector connector = new ServerConnector(this.server);
        int port = Config.getConfiguration().getServerPort();
        connector.setPort(port);
        this.server.addConnector(connector);
        HandlerList handlers = new HandlerList();

        //setup swagger
        buildSwagger();

        handlers.addHandler(swaggerUI());

        handlers.addHandler(hawkUI());

        // REST
        ContextHandler context = buildContext();//new ServletContextHandler(ServletContextHandler.SESSIONS);
//        context.setContextPath("/");
//
//        ServletHolder restServlet = context.addServlet(ServletContainer.class, "/*");
//        restServlet.setInitOrder(0);
//        restServlet.setInitParameter("jersey.config.server.provider.packages", "io.hawkcd.http");


        // Handler for Entity Browser and Swagger
        handlers.addHandler(context);

        // *:8080/ws/v1
        // WebSockets
        ((ServletContextHandler) context).addServlet(WsServlet.class, "/ws/v1");

        //handlers.setHandlers(new Handler[]{resourceHandler, context, new DefaultHandler()});
        //handlers.setHandlers(handlers);

        this.server.setHandler(handlers);
    }

    private ResourceHandler hawkUI() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setResourceBase(this.getClass().getResource("/dist").toExternalForm());
        return resourceHandler;
    }

    private static void buildSwagger() {
        // This configures Swagger
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setResourcePackage(PipelineController.class.getPackage().getName());
        beanConfig.setScan(true);
        beanConfig.setBasePath("/");
        beanConfig.setDescription("Entity Browser API to demonstrate Swagger with Jersey2 in an "
                + "embedded Jetty instance, with no web.xml or Spring MVC.");
        beanConfig.setTitle("HawkCD REST APIs");
    }

    private static ContextHandler buildContext() {
        ResourceConfig resourceConfig = new ResourceConfig();
        // io.swagger.jaxrs.listing loads up Swagger resources

        resourceConfig.packages(PipelineController.class.getPackage().getName(), ApiListingResource.class.getPackage().getName());
        ServletContainer servletContainer = new ServletContainer(resourceConfig);
        ServletHolder servletHolder = new ServletHolder(servletContainer);
        ServletContextHandler appRootHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        appRootHandler.setContextPath("/");
        appRootHandler.addServlet(servletHolder, "/*");

        return appRootHandler;
    }

    // This starts the Swagger UI at http://localhost:9999/docs
    private static ContextHandler swaggerUI() throws Exception {
        final ResourceHandler swaggerUIResourceHandler = new ResourceHandler();
        String swaggerui = HServer.class.getClassLoader().getResource("swaggerui").toURI().toString();
        swaggerUIResourceHandler.setResourceBase(swaggerui);
        final ContextHandler swaggerUIContext = new ContextHandler();
        swaggerUIContext.setContextPath("/docs/");
        swaggerUIContext.setHandler(swaggerUIResourceHandler);

        return swaggerUIContext;
    }

    public void start() throws Exception {
        this.server.start();
        this.initializer.initialize();
        this.pipelinePreparer.start();
        this.jobAssigner.start();
        this.materialTracker.start();
        if (!Config.getConfiguration().isSingleNode()) {
            this.subsciber.start();
        }
        this.server.join();
    }

    public void stop() {
        RedisManager.disconnect();
    }
}
