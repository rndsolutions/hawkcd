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

package net.hawkengine.core.messagingsystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.TaskDefinition;
import redis.clients.jedis.JedisPubSub;

class Subscriber extends JedisPubSub {
    private Gson jsonConverter;

    Subscriber() {
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(ResultObjectWrapper.class, new ResultObjectWrapperAdapter())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
    }

    @Override
    public void onMessage(String channel, String message) {
        PubSubMessage pubSubMessage = this.jsonConverter.fromJson(message, PubSubMessage.class);
        MessagingSystem.receiveResult(pubSubMessage);
    }
}
