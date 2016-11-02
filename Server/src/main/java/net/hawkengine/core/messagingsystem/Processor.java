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
import net.hawkengine.model.User;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.PermissionService;
import net.hawkengine.services.filters.factories.SecurityServiceInvoker;
import net.hawkengine.ws.SessionPool;

import java.util.List;

public class Processor {
    private WsObjectProcessor wsObjectProcessor;
    private Publisher publisher;
    private SecurityServiceInvoker securityServiceInvoker;
    private PermissionService permissionService;

    public Processor() {
        this.wsObjectProcessor = new WsObjectProcessor();
        this.publisher = new Publisher();
        this.securityServiceInvoker = new SecurityServiceInvoker();
        this.permissionService = new PermissionService();
    }

    public void processRequest(WsContractDto contract, User user, String sessionId) {
        user.getPermissions().addAll(this.permissionService.getUniqueUserGroupPermissions(user));

        List<Permission> orderedPermissions = this.permissionService.sortPermissions(user.getPermissions());

        try {
            boolean shouldPublish = this.shouldPublishResult(contract.getMethodName());
            if (shouldPublish) {
                boolean hasPermission = this.securityServiceInvoker.process(contract.getArgs()[0].getObject(), contract.getClassName(), orderedPermissions, contract.getMethodName());
                if (hasPermission) {
                    ServiceResult result = (ServiceResult) this.wsObjectProcessor.call(contract);
                    if (result.getObject() == null) {
                        contract.setResult(result.getObject());
                        contract.setNotificationType(result.getNotificationType());
                        contract.setErrorMessage(result.getMessage());
                        contract.setArgs(null);

                        SessionPool.getInstance().sendToSingleUserSession(contract, sessionId);
                    } else {
                        PubSubMessage message = new PubSubMessage(
                                contract.getClassName(),
                                contract.getMethodName(),
                                result.getObject(),
                                result.getNotificationType(),
                                result.getMessage());

                        this.publisher.publish("global", message);
                    }
                } else {
                    contract.setResult(null);
                    contract.setNotificationType(NotificationType.ERROR);
                    contract.setErrorMessage("Unauthorized");
                    contract.setArgs(null);

                    SessionPool.getInstance().sendToSingleUserSession(contract, sessionId);
                }
            } else {
                ServiceResult result = (ServiceResult) this.wsObjectProcessor.call(contract);
                List<?> filteredEntities = this.securityServiceInvoker.filterEntities((List<?>) result.getObject(), contract.getClassName(), orderedPermissions, contract.getMethodName());
                contract.setResult(filteredEntities);
                contract.setNotificationType(result.getNotificationType());
                contract.setErrorMessage(result.getMessage());

                SessionPool.getInstance().sendToSingleUserSession(contract, sessionId);
            }
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void processResponse(PubSubMessage pubSubMessage) {
        WsContractDto contract = new WsContractDto(pubSubMessage.getClassName(), "", pubSubMessage.getMethodName(), pubSubMessage.getResultObject(), pubSubMessage.getResultNotificationType(), pubSubMessage.getResultMessage());

        SessionPool.getInstance().sendToAuthorizedSessions(contract);
    }

    private boolean shouldPublishResult(String methodName) {
        if (!methodName.startsWith("get")) {
            return true;
        } else {
            return false;
        }
    }
}
