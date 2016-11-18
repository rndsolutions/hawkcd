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

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.logging.Logger;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jgit.annotations.NonNull;

import java.io.IOException;

import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.utilities.deserializers.MaterialDefinitionAdapter;
import io.hawkcd.utilities.deserializers.TaskDefinitionAdapter;
import io.hawkcd.utilities.deserializers.WsContractDeserializer;
import io.hawkcd.ws.WSSession;

/**
 * Created by rado on 13.11.16.
 *
 * The @SessionManager class is responsible for applicaiton WS session management.
 * Works directly with the WsSessionPool class to store and retreive WSSession objects
 *
 */

public class SessionManager implements  ISessionManager{
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SessionManager.class.getClass());
    private  WsSessionPool sessionPool;
    private Gson jsonConverter;

    public SessionManager(){
        this.sessionPool = WsSessionPool.getInstance();
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
    }

    @Override
    public void closeSession(String sessionId) {

        WSSession session = this.sessionPool.getSessions()
                .stream()
                .filter(s -> s.getId() == sessionId)
                .findFirst()
                .orElse(null);

        sessionPool.removeSession(session);
    }

    @Override
    public void closeSessionForUser(String email) {

        WSSession session = sessionPool.getSessions()
                .stream()
                .filter(s -> s.getLoggedUser().getEmail().equals(email))
                .findFirst()
                .orElse(null);

        sessionPool.removeSession(session);
    }

    @Override
    public void sendToAllSessions(WsContractDto contract) {

        for (WSSession s:sessionPool.getSessions()) {
            this.send(s,contract);
        }
    }

    /**
     * We support only single user session/connection at a time.
     * Upon adding a new session to the pool we check if a user with the same email has already opned a session, if so
     * we close the previous session and keep the new one
     * @param newSession
     */
    @Override
    public void addSession(WSSession newSession) {

        String email = newSession.getLoggedUser().getEmail();
        if (this.sessionPool.contains(newSession)){
            WSSession sessionToClose = this.sessionPool.getSessionForUser(email);
            this.logoutUser(sessionToClose);
            this.closeSessionForUser(email);
        }

        this.sessionPool.getInstance().addSession(newSession);
    }

    @Override
    public boolean isUserInSession(WSSession session, String email) {
        return false;
    }

    @Override
    public void logoutUser(WSSession session){

        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserService"); //TODO: Fix this it should be some other message
        contract.setMethodName("logout");
        contract.setNotificationType(NotificationType.SUCCESS);
        this.send(session,contract);
    }

    @NonNull
    void send(WSSession session, WsContractDto contract) {

        if (session.isConnected()){
            RemoteEndpoint remoteEndpoint = session.getRemote();
            String jsonResult = this.jsonConverter.toJson(contract);
            try {
                remoteEndpoint.sendString(jsonResult);
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
    }
}
