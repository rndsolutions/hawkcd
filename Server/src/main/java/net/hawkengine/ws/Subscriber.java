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

package net.hawkengine.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.payload.PublishObject;
import redis.clients.jedis.JedisPubSub;

public class Subscriber extends JedisPubSub {
    private Gson jsonConverter;

    public Subscriber() {
        this.jsonConverter = new GsonBuilder()
//                .registerTypeAdapter(PublishObject.class, new PublishObjectAdapter())\
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
    }

    @Override
    public void onMessage(String channel, String message) {
        PublishObject publishObject = this.jsonConverter.fromJson(message, PublishObject.class);

        if (channel.equals("global")) {
            if (publishObject.getUserId() == null) {
                SessionPool.getInstance().sendToAuthorizedSessions();
            } else {
                SessionPool.getInstance().sendToUserSessions();
            }
        } else if (channel.equals("local")) {
            SessionPool.getInstance().sendToSingleUserSession();
        }
    }
}
