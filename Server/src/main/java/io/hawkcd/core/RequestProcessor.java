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

import io.hawkcd.core.publisher.Publisher;
import io.hawkcd.core.security.AuthorizationFactory;
import io.hawkcd.core.security.IAuthorizationManager;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.User;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.payload.Permission;
import io.hawkcd.services.filters.PermissionService;
import io.hawkcd.services.filters.factories.SecurityServiceInvoker;
import io.hawkcd.ws.SessionPool;

import java.util.List;


/*
* The class responsibility is to ptocess all reqeust passed by the WSSocket object
*/
public class RequestProcessor {
    private WsObjectProcessor wsObjectProcessor;
    private Publisher publisher;
    private SecurityServiceInvoker securityServiceInvoker;
    private PermissionService permissionService;

    public RequestProcessor() {
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
                        Message message = new Message(
                                contract.getClassName(),
                                contract.getMethodName(),
                                result.getObject(),
                                result.getNotificationType(),
                                result.getMessage(),
                                user);

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

    /**
     *
     * All WS requests flows through this method, auhtorization checks are performed,
     * and the request is broadcasted to all subscribers
     *
     * Workflow:
     * evaluate current user permissions
     * get all active session from the cluster
     * filter all users that have active sessions with the cluster
     * evaluate
     * 3
     *
     * @param contract
     * @param user
     * @param sessionId
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void prorcessRequest1(WsContractDto contract, User user, String sessionId)
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        //check if caller has permission to execute the call
        //boolean isAuthorized = true;//
        boolean isAuthorized = true; //AuthorizationFactory.getAuthorizationManager().isAuthorized(user, contract);



        if (isAuthorized){
            //Make a call to a Busienss service
            ServiceResult result = (ServiceResult)this.wsObjectProcessor.call(contract);

            //Construct a message from the service call result
            Message message = new Message(
                    contract.getClassName(),
                    contract.getMethodName(),
                    result.getObject(),
                    result.getNotificationType(),
                    result.getMessage(),
                    user
            );

            //broadcast the mssage to all Subscribers
            this.publisher.publish("global", message);
        }else {

            //publish anauthorized message
        }
    }

    public void processResponse(Message pubSubMessage) {
        WsContractDto contract = new WsContractDto(pubSubMessage.getServiceCalled(), "", pubSubMessage.getMethodCalled(), pubSubMessage.getResultObject(), pubSubMessage.getResultNotificationType(), pubSubMessage.getResultMessage());
        //this.get
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
