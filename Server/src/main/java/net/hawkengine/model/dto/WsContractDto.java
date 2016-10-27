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

package net.hawkengine.model.dto;

import net.hawkengine.model.enums.NotificationType;

public class WsContractDto {
    private String className;
    private String packageName;
    private String methodName;
    private Object result;
    private NotificationType notificationType;
    private String errorMessage;
    private ConversionObject[] args;

    public WsContractDto() {
    }

    public WsContractDto(String className, String packageName, String methodName, Object result, NotificationType notificationType, String errorMessage) {
        this.className = className;
        this.packageName = packageName;
        this.methodName = methodName;
        this.result = result;
        this.notificationType = notificationType;
        this.errorMessage = errorMessage;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ConversionObject[] getArgs() {
        return this.args;
    }

    public void setArgs(ConversionObject[] args) {
        this.args = args;
    }

    public NotificationType getNotificationType() {
        return this.notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }
}

