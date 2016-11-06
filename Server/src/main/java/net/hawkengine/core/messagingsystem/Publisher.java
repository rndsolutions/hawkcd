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
import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.TaskDefinition;
import redis.clients.jedis.Jedis;

class Publisher {
    private Jedis jedisPublisher;
    private Gson jsonConverter;

    Publisher() {
        this.jedisPublisher = RedisManager.getJedisPool().getResource();
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
    }

    void publish(String channel, PubSubMessage message) {
        String messageAsString = this.jsonConverter.toJson(message);
        this.jedisPublisher.publish(channel, messageAsString);
    }
}
