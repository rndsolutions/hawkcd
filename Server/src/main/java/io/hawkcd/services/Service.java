/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hawkcd.services;

import io.hawkcd.model.DbEntry;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.interfaces.IService;

import java.util.List;

public abstract class Service<T extends DbEntry> implements IService<T> {
    private String objectType;

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    @Override
    public ServiceResult createServiceResult(T object, NotificationType notificationType, String messsage) {
        String resultMessage;
        if (notificationType == NotificationType.SUCCESS) {
            if (object == null) {
                resultMessage = this.getObjectType() + " " + messsage + ".";
            } else {
                resultMessage = this.getObjectType() + " " + object.getId() + " " + messsage + ".";
            }
        } else {
            resultMessage = this.getObjectType() + " " + messsage + ".";
        }
        ServiceResult result = new ServiceResult(object, notificationType, resultMessage);

        return result;
    }

    @Override
    public ServiceResult createServiceResultArray(List<?> object, NotificationType notificationType, String messsage) {
        String resultMessage = this.getObjectType() + "s " + messsage + ".";
        ServiceResult result = new ServiceResult(object, notificationType, resultMessage);

        return result;
    }
}
