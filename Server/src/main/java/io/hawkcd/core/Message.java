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

package io.hawkcd.core;

import io.hawkcd.core.subscriber.Envelop;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.enums.NotificationType;

/*
* The Message class represents a wrapper message object that's sent by Publishers to Subscribers. In addition to the message that has to be sent to users
* it also carries matadata.
*/
public class Message {
    private String serviceCalled;
    private String methodCalled;
    private Envelop envelop;
    private NotificationType resultNotificationType;
    private String resultMessage;

    Message(String serviceCalled, String methodCalled, ServiceResult serviceResult) {
        this.serviceCalled = serviceCalled;
        this.methodCalled = methodCalled;
        if (serviceResult != null) {
            this.envelop = new Envelop(serviceResult.getObject());
            this.resultNotificationType = serviceResult.getNotificationType();
            this.resultMessage = serviceResult.getMessage();
        }
    }

    Message(String serviceCalled, String methodCalled, Object resultObject, NotificationType resultNotificationType, String resultMessage) {
        this.serviceCalled = serviceCalled;
        this.methodCalled = methodCalled;
        this.envelop = new Envelop(resultObject);
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
        return this.envelop.getResultObject();
    }

    NotificationType getResultNotificationType() {
        return resultNotificationType;
    }

    String getResultMessage() {
        return resultMessage;
    }
}
