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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import io.hawkcd.ws.WSSession;

/**
 * Created by rado on 13.11.16.
 * A singleton class that keep track of all user WS sessions
 */
public class WsSessionPool implements ISessionsPool {

    private List<WSSession> sessions;
    private static final Logger LOGGER = Logger.getLogger(WsSessionPool.class);
    private static WsSessionPool instance;

    private WsSessionPool(){
        this.sessions = Collections.synchronizedList(new ArrayList<WSSession>());
    }

    public static synchronized WsSessionPool getInstance() {
        if (instance == null) {
            instance = new WsSessionPool();
        }
        return instance;
    }

    @Override
    public List<WSSession> getSessions() {
        return sessions;
    }

    @Override
    @NotNull
    public WSSession getSessionByID(String id) {

        final WSSession session = this.sessions.stream()
                .filter(s -> s.getId() == id)
                .reduce((a, b) -> {
                    throw new IllegalStateException("Multiple elements: " + a + ", " + b);
                }).get();

        LOGGER.debug(session);
        return session;
    }

    @Override
    @NotNull
    public void addSession(WSSession session) {
        LOGGER.debug(session);
        this.sessions.add(session);

    }

    @Override
    public void removeSession(String sessionID) {

        final WSSession session = this.sessions.stream().
                    filter(x -> x.getId() == sessionID)
                    .reduce((a, b) -> {
                        throw new IllegalStateException("Multiple elements: " + a + ", " + b);
                    }).get();
        LOGGER.debug(session);
        this.sessions.remove(session);
    }
}
