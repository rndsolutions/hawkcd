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

class PubSubMessage {
    private String serviceCalled;
    private String methodCalled;
    private ResultObjectWrapper resultObjectWrapper;
    private NotificationType resultNotificationType;
    private String resultMessage;

    PubSubMessage(String serviceCalled, String methodCalled, ServiceResult serviceResult) {
        this.serviceCalled = serviceCalled;
        this.methodCalled = methodCalled;
        if (serviceResult != null) {
            this.resultObjectWrapper = new ResultObjectWrapper(serviceResult.getObject());
            this.resultNotificationType = serviceResult.getNotificationType();
            this.resultMessage = serviceResult.getMessage();
        }
    }

    PubSubMessage(String serviceCalled, String methodCalled, Object resultObject, NotificationType resultNotificationType, String resultMessage) {
        this.serviceCalled = serviceCalled;
        this.methodCalled = methodCalled;
        this.resultObjectWrapper = new ResultObjectWrapper(resultObject);
        this.resultNotificationType = resultNotificationType;
        this.resultMessage = resultMessage;
    }

    String getServiceCalled() {
        return serviceCalled;
    }

    String getMethodCalled() {
        return methodCalled;
    }

    Object getResultObject() {
        return this.resultObjectWrapper.getResultObject();
    }

    NotificationType getResultNotificationType() {
        return resultNotificationType;
    }

    String getResultMessage() {
        return resultMessage;
    }
}
