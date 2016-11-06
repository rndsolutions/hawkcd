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

import net.hawkengine.model.ServiceResult;

public class MessagingSystem implements IMessagingSystem {
    static final String DEFAULT_CHANNEL = "global";

    private Thread listener;
    private Publisher publisher;

    public MessagingSystem() {
        this.listener = new Thread(new Listener(), "Listener");
        this.listener.start();
        this.publisher = new Publisher();
    }

    @Override
    public void sendResult(String serviceName, String methodName, ServiceResult serviceResult) {
        PubSubMessage pubSubMessage = new PubSubMessage(serviceName, methodName, serviceResult);
        this.publisher.publish(DEFAULT_CHANNEL, pubSubMessage);
    }

    static void receiveResult(PubSubMessage pubSubMessage) {
        // TODO: Send to Dispatcher
    }
}
