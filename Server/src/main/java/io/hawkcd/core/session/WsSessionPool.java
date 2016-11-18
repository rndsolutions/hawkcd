/*
 *   Copyright (C) 2016 R&D Solutions Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *
 */

package io.hawkcd.core.session;

import com.sun.istack.internal.NotNull;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jgit.annotations.NonNull;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.SessionDetails;
import io.hawkcd.model.User;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.SessionService;
import io.hawkcd.ws.WSSession;

/**
 * Created by rado on 13.11.16.
 * A singleton class that keep track of all user WS sessions
 */
public class WsSessionPool implements ISessionsPool {

    private Set<WSSession> sessions;
    private static final Logger LOGGER = Logger.getLogger(WsSessionPool.class);
    private static WsSessionPool instance;
    private SessionService sessionService = new SessionService();

    private WsSessionPool() {
        this.sessions = Collections.synchronizedSet(new HashSet<WSSession>());
        this.sessionService = new SessionService();
    }

    public static synchronized WsSessionPool getInstance() {
        if (instance == null) {
            instance = new WsSessionPool();
        }
        return instance;
    }

    @Override
    public Set<WSSession> getSessions() {
        return this.sessions;
    }

    @Override
    public WSSession getSessionByID(String id) {

         WSSession session = this.sessions.stream()
                .filter(s -> s.getId().equals(id) )
                .reduce((a, b) -> {
                    throw new IllegalStateException("Multiple elements: " + a + ", " + b);
                }).get();

        LOGGER.debug(session);
        return session;
    }

    @Override
    public void addSession(WSSession session) {

        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("Session pool size: "+this.sessions.size());
            for (WSSession sess: sessions) {
                LOGGER.debug("Session pool size: "+  sess.getLoggedUser().getEmail());
            }
        }

        this.sessions.add(session);
        SessionDetails sessionDetails = session.getSessionDetails();
        try {
            ServiceResult result = this.sessionService.add(sessionDetails);
            if (result.getNotificationType() == NotificationType.ERROR) {
                throw new RuntimeException(result.getMessage());
            }
        } catch (RuntimeException ex) {
            LOGGER.error(ex);
        }
    }

    @Override
    public void removeSession(WSSession session) {

        try {
            if (session == null){
                throw new NullPointerException("session object is null");
            }
            if (session.getSession()!= null){
                if (session.getSession().isOpen()){
                    session.getSession().close(new CloseStatus(1000,"User Logged out"));
                }
            }

            SessionDetails sessionDetails = session.getSessionDetails();
            sessionDetails.setEndTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
            sessionDetails.setActive(false);

            //update database
            ServiceResult result = this.sessionService.update(sessionDetails);
            if (result.getNotificationType() == NotificationType.ERROR)
                throw new RuntimeException(result.getMessage());

            this.sessions.remove(session);
            LOGGER.debug("Session for user: "+ session.getLoggedUser().getEmail() +" was removed");
        } catch (RuntimeException ex) {
            LOGGER.error(ex.getMessage());
            this.sessions.remove(session);
        }
    }

    @Override
    public boolean contains(WSSession session) {

        String email = session.getLoggedUser().getEmail();

        WSSession ses = this.sessions
                .stream()
                .filter(s -> s.getLoggedUser().getEmail().equals(email) )
                .findFirst()
                .orElse(null);

        if (ses != null ){
            return  true;
        }else {
            return false;
        }
    }

    @Override
    public WSSession getSessionForUser(String email) {

        return this.getSessions()
                .stream()
                .filter(s -> s.getLoggedUser().getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }



}
