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

import net.hawkengine.model.enums.NotificationType;

import java.util.List;

public class PubSubMessage {
    private String serviceCalled;
    private String methodCalled;
    private NotificationType notificationType;
    private String message;
    private String userId;
    private String sessionId;
    private List<String> updatedObjectsIds;

    public PubSubMessage(String serviceCalled, String methodCalled, NotificationType notificationType, String message, String userId, String sessionId, List<String> updatedObjectsIds) {
        this.serviceCalled = serviceCalled;
        this.methodCalled = methodCalled;
        this.notificationType = notificationType;
        this.message = message;
        this.userId = userId;
        this.sessionId = sessionId;
        this.updatedObjectsIds = updatedObjectsIds;
    }

    public String getServiceCalled() {
        return serviceCalled;
    }

    public String getMethodCalled() {
        return methodCalled;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public List<String> getUpdatedObjectsIds() {
        return updatedObjectsIds;
    }
}
