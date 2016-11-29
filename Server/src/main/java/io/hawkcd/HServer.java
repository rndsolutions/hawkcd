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
import io.hawkcd.materials.MaterialTracker;
import io.hawkcd.core.subscriber.SubscriberComponent;
import io.hawkcd.scheduler.JobAssigner;
import io.hawkcd.scheduler.PipelinePreparer;
import io.hawkcd.utilities.Initializer;
import io.hawkcd.db.redis.RedisManager;
import io.hawkcd.ws.WsServlet;
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

    public void configureJetty() {
        // HTTP connector
        ServerConnector connector = new ServerConnector(this.server);
        int port = Config.getConfiguration().getServerPort();
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
        restServlet.setInitParameter("jersey.config.server.provider.packages", "io.hawkcd.http");
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
//        this.subsciber.start();
        this.server.start();
        this.initializer.initialize();
        this.pipelinePreparer.start();
        this.jobAssigner.start();
        this.materialTracker.start();
        this.subsciber.start();
        this.server.join();
    }

    public void stop() {
        RedisManager.disconnect();
    }
}