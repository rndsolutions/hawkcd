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
import net.hawkengine.model.enums.NotificationType;

public class PublishObject {
    private String className;
    private String methodName;
    private Object object;
    private String objectType;
    private NotificationType notificationType;
    private String message;
    private String userId;
    private String sessionId;

    public PublishObject(String className, String methodName, ServiceResult serviceResult) {
        this.className = className;
        this.methodName = methodName;

        if (serviceResult.getObject() != null) {
            this.object = serviceResult.getObject();
            this.objectType = serviceResult.getObject().getClass().getCanonicalName();
        }

        this.notificationType = serviceResult.getNotificationType();
        this.message = serviceResult.getMessage();
    }

    public PublishObject(String className, String methodName, ServiceResult serviceResult, String userId) {
        this(className, methodName, serviceResult);
        this.userId = userId;
    }

    public PublishObject(String className, String methodName, ServiceResult serviceResult, String userId, String sessionId) {
        this(className, methodName, serviceResult, userId);
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserId() {
        return userId;
    }
}
