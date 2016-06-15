package net.hawkengine.ws;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WsServlet extends WebSocketServlet {
    private static final long serialVersionUID = 1L;

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(WsEndpoint.class);
        factory.getPolicy().setIdleTimeout(0);
    }
}