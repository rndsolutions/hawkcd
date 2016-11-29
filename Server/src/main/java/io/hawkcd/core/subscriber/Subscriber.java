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

package io.hawkcd.core.subscriber;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.hawkcd.core.Message;
import io.hawkcd.core.MessageConverter;
import io.hawkcd.core.RequestProcessor;
import io.hawkcd.core.session.ISessionManager;
import io.hawkcd.core.session.SessionFactory;
import io.hawkcd.model.Entity;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.utilities.deserializers.MaterialDefinitionAdapter;
import io.hawkcd.utilities.deserializers.TaskDefinitionAdapter;

import org.apache.log4j.Logger;

import io.hawkcd.ws.WSSocket;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;

/*
* Represents an application process(when running in separate VM)
* or thread ( when running in the same VM) that receives messages broadcasted by publishers
*/
public class Subscriber extends JedisPubSub {

    private Gson jsonConverter;
    private IMessageDispatcher messageDispatcher;
    private IMessageFilter authFilter;
    private RequestProcessor requestProcessor;
    private ISessionManager sessionManager;

    private static final Logger LOGGER = Logger.getLogger(Subscriber.class);

    public Subscriber() {

        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(Envelopе.class, new EnvelopeAdapter())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();

        requestProcessor = new RequestProcessor();
    }

    @Override
    public void onMessage(String channel, String msg) {
        LOGGER.debug(msg);
        Message message = this.jsonConverter.fromJson(msg, Message.class);
        ISessionManager sessionManager = SessionFactory.getSessionManager();

        if (message.isTargetOwner()) { // When is list and targets the user executed the request
            WsContractDto contract = MessageConverter.convert(message);
            
            WSSocket session = sessionManager.getSessionByUserId(message.getOwner().getId());
            sessionManager.send(session, contract);
        } else { // when is single message meant to be broadcast
            Map<String, PermissionType> permissionTypeByUser = message.getPermissionTypeByUser();
            WsContractDto contract = MessageTranslator.translateMessageToContract(message);

            for (Map.Entry<String, PermissionType> entry : permissionTypeByUser.entrySet()) {
                WSSocket session = sessionManager.getSessionByUserId(entry.getKey());
                if (session != null) {
                    ((Entity) contract.getResult()).setPermissionType(entry.getValue());
                    sessionManager.send(session, contract);
                }
            }
        }
    }
}
