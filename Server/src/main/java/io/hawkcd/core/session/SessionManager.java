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

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jgit.annotations.NonNull;

import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.model.dto.WsContractDto;
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
        sessionPool.removeSession(sessionId);
    }

    @Override
    public void sendToAllSessions(WsContractDto contract) {

        for (WSSession s:sessionPool.getSessions()) {
            this.send(s,contract);
        }
    }

    @Override
    @NotNull
    public void addSession(WSSession session) {

        this.sessionPool.getInstance().addSession(session);
    }


    @NonNull
    void send(WSSession session, WsContractDto contract) {

        if (session.isConnected()){
            RemoteEndpoint remoteEndpoint = session.getRemote();
            String jsonResult = this.jsonConverter.toJson(contract);
            remoteEndpoint.sendStringByFuture(jsonResult);
        }
    }
}
