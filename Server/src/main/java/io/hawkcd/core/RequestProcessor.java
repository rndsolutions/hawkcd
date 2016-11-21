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

import com.google.gson.Gson;
import io.hawkcd.core.publisher.Publisher;
import io.hawkcd.model.PermissionObject;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.SessionDetails;
import io.hawkcd.model.User;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.payload.Permission;
import io.hawkcd.services.SessionService;
import io.hawkcd.services.UserService;
import io.hawkcd.services.filters.PermissionService;
import io.hawkcd.services.filters.factories.SecurityServiceInvoker;
import io.hawkcd.ws.EntityPermissionTypeServiceInvoker;
import io.hawkcd.ws.SessionPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
     * All WS requests flows through this method, auhtorization checks are performed,
     * and the request is broadcasted to all subscribers
     * <p>
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
    public void prorcessRequest1(WsContractDto contract, User user, String sessionId) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {

        // Get service to be called, get arguments
        // Authorize current User Request
        // Make a call to a Business service
        // Get all Users filtered by active sessions
        // Perform authorization check for each active User
        // Attach User email and Id to


        // 1. Get service to be called, get arguments
        Gson jsonConverter = new Gson();
        String fullPackageName = String.format("%s.%s", contract.getPackageName(), contract.getClassName());
        Object service = Class.forName(fullPackageName).newInstance();
        List<Object> methodArgs = new ArrayList<>();
        int contractArgsLength = contract.getArgs().length;
        for (int i = 0; i < contractArgsLength; i++) {
            if (contract.getArgs()[i] != null) {
                Class objectClass = Class.forName(contract.getArgs()[i].getPackageName());
                Object object = jsonConverter.fromJson(contract.getArgs()[i].getObject(), objectClass);
                methodArgs.add(object);
            }
        }

        // TODO: Logic to be removed
        UserService userService = new UserService();
        User userFromDb = (User) userService.getById(user.getId()).getObject();
        List<Permission> permissions = this.permissionService.sortPermissions(userFromDb.getPermissions());
        userFromDb.setPermissions(permissions);

        // 2. Authorize current User Request
        boolean isAuthorized = true;
//            boolean isAuthorized = AuthorizationFactory.getAuthorizationManager().isAuthorized(user, contract, methodArgs);
        if (!isAuthorized) {
            //publish unauthorized message to local channel
        }

        // 3. Make a call to a Business service
        // TODO: refactor wsObjectProcessor
        ServiceResult result = (ServiceResult) this.wsObjectProcessor.call(contract);

        // TODO: Check if the call is GetAll of some type and execute a different flow

        // 4. Get all Users filtered by active sessions
        SessionService sessionService = new SessionService();
        List<SessionDetails> sessions = (List<SessionDetails>) sessionService.getAll().getObject();
        List<SessionDetails> activeSessions = sessions.stream().filter(s -> s.isActive()).collect(Collectors.toList());

        // 5. Perform authorization check for each active User
        EntityPermissionTypeServiceInvoker invoker = new EntityPermissionTypeServiceInvoker();
        PermissionObject permissionObject = (PermissionObject) result.getObject();
        Class<?> objectClass = result.getObject().getClass();
        Map<String, PermissionType> permissionTypeByUser = new HashMap<>();

        for (SessionDetails activeSession : activeSessions) {
            User userToSendTo = (User) userService.getById(activeSession.getUserId()).getObject();
            List<Permission> userPermissions = this.permissionService.sortPermissions(userFromDb.getPermissions());
            permissionObject = invoker.invoke(objectClass, userPermissions, permissionObject);
            permissionTypeByUser.put(userToSendTo.getEmail(), permissionObject.getPermissionType());
        }




        //Construct a message from the service call result
        Message message = new Message(
                contract.getClassName(),
                contract.getMethodName(),
                result.getObject(),
                result.getNotificationType(),
                result.getMessage(),
                user
        );

        message.setPermissionTypeByUser(permissionTypeByUser);

        // Determine channel to broadcast message
        // TODO: Implement logic for "local" channel

        //broadcast the message
        this.publisher.publish("global", message);

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
