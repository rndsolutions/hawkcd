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

package io.hawkcd.ws;

import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.User;
import io.hawkcd.model.dto.WsContractDto;

public class EndpointConnector {
    public static void passResultToEndpoint(String className, String methodName, ServiceResult serviceResult) {
        WsContractDto contract = new WsContractDto();
        contract.setClassName(className);
        contract.setMethodName(methodName);
        contract.setResult(serviceResult.getObject());
        contract.setNotificationType(serviceResult.getNotificationType());
        contract.setErrorMessage(serviceResult.getMessage());

        SessionPool.getInstance().sendToAuthorizedSessions(contract);
    }

    public static void passResultToEndpoint(String className, String methodName, ServiceResult serviceResult, User user) {
        WsContractDto contract = new WsContractDto();
        contract.setClassName(className);
        contract.setMethodName(methodName);
        contract.setResult(serviceResult.getObject());
        contract.setNotificationType(serviceResult.getNotificationType());
        contract.setErrorMessage(serviceResult.getMessage());

        SessionPool.getInstance().sendToUserSessions(contract, user);
    }
}
