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

class PubSubMessage {
    private String className;
    private String methodName;
    private ResultObjectWrapper resultObjectWrapper;
    private NotificationType resultNotificationType;
    private String resultMessage;

    PubSubMessage(String className, String methodName, Object resultObject, NotificationType resultNotificationType, String resultMessage) {
        this.className = className;
        this.methodName = methodName;
        this.resultObjectWrapper = new ResultObjectWrapper(resultObject);
        this.resultNotificationType = resultNotificationType;
        this.resultMessage = resultMessage;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object getResultObject() {
        return this.resultObjectWrapper.getResultObject();
    }

    public NotificationType getResultNotificationType() {
        return resultNotificationType;
    }

    public String getResultMessage() {
        return resultMessage;
    }
}
