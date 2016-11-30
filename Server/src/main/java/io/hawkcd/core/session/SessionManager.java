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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.SessionDetails;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.model.User;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.UserService;
import io.hawkcd.utilities.deserializers.MaterialDefinitionAdapter;
import io.hawkcd.utilities.deserializers.TaskDefinitionAdapter;
import io.hawkcd.utilities.deserializers.WsContractDeserializer;
import io.hawkcd.ws.WSSocket;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by rado on 13.11.16.
 * <p>
 * The @SessionManager class is responsible for application WS session management.
 * Works directly with the WsSessionPool class to store and retrieve WSSocket objects
 */

public class SessionManager implements ISessionManager {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SessionManager.class.getClass());

    private WsSessionPool sessionPool;
    private SessionService sessionService;
    private UserService userService;
    private Gson jsonConverter;

    public SessionManager() {
        this.sessionPool = WsSessionPool.getInstance();
        this.sessionService = new SessionService();
        this.userService = new UserService();
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
    }

    @Override
    public void closeSessionById(String sessionId) {

        WSSocket session = this.sessionPool.getSessions()
                .stream()
                .filter(s -> s.getId().equals(sessionId))
                .findFirst()
                .orElse(null);

        this.sessionPool.removeSession(session);
    }

    @Override
    public void closeSessionByUserEmail(String email) {

        WSSocket session = this.sessionPool.getSessions()
                .stream()
                .filter(s -> s.getLoggedUser().getEmail().equals(email))
                .findFirst()
                .orElse(null);

        this.sessionPool.removeSession(session);
    }

    @Override
    public void sendToAllSessions(WsContractDto contract) {
        Set<WSSocket> sessions = this.sessionPool.getSessions();
        for (WSSocket s : sessions) {
            this.send(s, contract);
        }
    }

    /**
     * We support only a single user session/connection at a time.
     * Upon adding a new session to the pool we check if a user with the same email has already opned a session, if so,
     * we close the previous session and keep the new one
     *
     * @param newSession
     */
    @Override
    public void openSession(WSSocket newSession) {

        String email = newSession.getLoggedUser().getEmail();
        if (this.sessionPool.contains(newSession)) {
            WSSocket sessionToClose = this.sessionPool.getSessionByUserEmail(email);
            this.logoutUser(sessionToClose);
            this.closeSessionByUserEmail(email);
        }

        this.sessionPool.addSession(newSession);
    }

    @Override
    public boolean isUserInSession(WSSocket session, String email) {
        return false;
    }

    /**
     * Sends a notification to connected WS client about:
     * 1. The Session will be closed on the server
     * 2. Asks for logout
     *
     * @param session
     */
    @Override
    public void logoutUser(WSSocket session) {

        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserService"); //TODO: Fix this it should be some other message
        contract.setMethodName("logout");
        contract.setNotificationType(NotificationType.SUCCESS);
        this.send(session, contract);
    }

    @Override
    public SessionDetails getSessionDetailsBySessionId(String sessionId) {
        SessionDetails result = null;
        WSSocket session = this.sessionPool.getSessions()
                .stream()
                .filter(s -> s.getId() == sessionId)
                .findFirst()
                .orElse(null);

        if (session != null) {
            result = session.getSessionDetails();
        }

        return result;
    }

    @Override
    public List<SessionDetails> getAllActiveSessions() {
        List<SessionDetails> activeSessions = ((List<SessionDetails>) this.sessionService.getAll().getEntity())
                .stream()
                .filter(s -> s.isActive())
                .collect(Collectors.toList());
        return activeSessions;
    }

    @Override
    public void sendToAllAuthorizedSession(WsContractDto contractDto) {

    }

    @Override
    public WSSocket getSessionByUserId(String id) {
        return this.sessionPool.getSessions()
                .stream().filter(s -> s.getLoggedUser().getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Receives a WSSocket object and WsContractDto to write it to the stream
     *
     * @param session
     * @param contract
     */
    public void send(WSSocket session, WsContractDto contract) {

        if (session != null && session.isConnected()) {
            RemoteEndpoint remoteEndpoint = session.getRemote();
            String jsonResult = this.jsonConverter.toJson(contract);

            remoteEndpoint.sendStringByFuture(jsonResult);
        }
    }

    public void updateSessionLoggedUser(String... userIds) {
        for (String userId : userIds) {
            WSSocket session = this.sessionPool.getSessionByUserId(userId);
            if (session == null) {
                continue;
            }

            User user = (User) this.userService.getById(userId).getEntity();
            if (user == null) {
                this.logoutUser(session);
                this.closeSessionById(session.getId());
                continue;
            }

            session.setLoggedUser(user);
            WsContractDto wsContractDto = session.extractUserDetails(user);

            this.send(session, wsContractDto);
        }
    }
}
