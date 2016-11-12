package io.hawkcd.core.session;

import com.sun.xml.internal.ws.api.server.WSEndpoint;

import java.util.List;

import io.hawkcd.ws.WSSession;

/**
 * Created by rado on 11.11.16.
 */
public interface ISessionsPool  {

    List<WSSession> getSessions();

    WSEndpoint getSessionByID(String id);

    void addSession(WSSession session);

    void removeSession(String sessionID);

}
