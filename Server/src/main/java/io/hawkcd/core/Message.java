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

import io.hawkcd.core.subscriber.Envelopе;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.User;
import io.hawkcd.model.enums.NotificationType;

/*
* The Message class represents a wrapper message object that's sent by Publishers to Subscribers.
* In addition to the message that has to be sent to users it also carries various matadata.
*/
public class Message {

    @Override
    public String toString() {
        return "Message{" +
                "serviceCalled='" + serviceCalled + '\'' +
                ", methodCalled='" + methodCalled + '\'' +
                ", resultNotificationType=" + resultNotificationType +
                ", resultMessage='" + resultMessage + '\'' +
                ", owner=" + owner +
                '}';
    }

    private String serviceCalled;
    private String methodCalled;

    //Holds the result being reterned by the service call
    private Envelopе envelopе;

    private NotificationType resultNotificationType;
    private String resultMessage;

    //The user context in which the message is created
    private User owner;

    Message(String serviceCalled, String methodCalled, ServiceResult serviceResult, User usr) {
        this.serviceCalled = serviceCalled;
        this.methodCalled = methodCalled;
        this.owner = usr;
        if (serviceResult != null) {
            this.envelopе = new Envelopе(serviceResult.getObject());
            this.resultNotificationType = serviceResult.getNotificationType();
            this.resultMessage = serviceResult.getMessage();
        }

    }

    Message(String serviceCalled
                        , String methodCalled
                        , Object resultObject
                        , NotificationType resultNotificationType
                        , String resultMessage
                        , User usr)
    {
        this.serviceCalled = serviceCalled;
        this.methodCalled = methodCalled;
        this.envelopе = new Envelopе(resultObject);
        this.resultNotificationType = resultNotificationType;
        this.resultMessage = resultMessage;
        this.owner = usr;
    }

    public String getServiceCalled() {
        return serviceCalled;
    }

    public String getMethodCalled() {
        return methodCalled;
    }

    public Object getResultObject() {
        return this.envelopе.getObject();
    }

    public NotificationType getResultNotificationType() {
        return resultNotificationType;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setOwner(User usr){ this.owner = usr;}

    public  User getOwner(){return  this.owner;}
}
