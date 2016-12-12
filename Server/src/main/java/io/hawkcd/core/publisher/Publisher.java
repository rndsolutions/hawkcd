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

package io.hawkcd.core.publisher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.hawkcd.core.Message;
import io.hawkcd.db.redis.RedisManager;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.utilities.deserializers.MaterialDefinitionAdapter;
import io.hawkcd.utilities.deserializers.TaskDefinitionAdapter;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

/**
 * The class is used to publish messages across the system.
 */
public class Publisher implements IPublisher {
    private static final Logger LOGGER = Logger.getLogger(Publisher.class);

    private Jedis jedisPublisher;
    private Gson jsonConverter;

    private static Publisher instance;

    public static synchronized Publisher getInstance() {
        if (instance == null) {
            instance = new Publisher();
        }

        return instance;
    }

    private Publisher() {
        this.jedisPublisher = RedisManager.getJedisPool().getResource();
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
    }

    public void publish(String channel, Message message) {
        LOGGER.debug(message);
        String messageAsString = this.jsonConverter.toJson(message);
        this.jedisPublisher.publish(channel, messageAsString);
    }
}
