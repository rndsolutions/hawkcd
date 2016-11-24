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
import com.google.gson.GsonBuilder;
import io.hawkcd.core.publisher.Publisher;
import io.hawkcd.core.security.AuthorizationFactory;
import io.hawkcd.core.security.Grant;
import io.hawkcd.core.session.SessionFactory;
import io.hawkcd.model.*;
import io.hawkcd.model.dto.PipelineDefinitionDto;
import io.hawkcd.model.dto.PipelineGroupDto;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.services.UserService;
import io.hawkcd.services.filters.PermissionService;
import io.hawkcd.services.filters.factories.SecurityServiceInvoker;
import io.hawkcd.utilities.deserializers.MaterialDefinitionAdapter;
import io.hawkcd.utilities.deserializers.TaskDefinitionAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
* The class responsibility is to ptocess all reqeust passed by the WSSocket object
*/
public class RequestProcessor {
    private WsObjectProcessor wsObjectProcessor;
    private Publisher publisher;
    private SecurityServiceInvoker securityServiceInvoker;
    private PermissionService permissionService;
    private UserService userService;

    public RequestProcessor() {
        this.wsObjectProcessor = new WsObjectProcessor();
        this.publisher = new Publisher();
        this.securityServiceInvoker = new SecurityServiceInvoker();
        this.permissionService = new PermissionService();
        this.userService = new UserService();
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
     * @param currentUser
     * @param sessionId
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void prorcessRequest1(WsContractDto contract, User currentUser, String sessionId) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {

        // Get service to be called, get arguments
        // Authorize current User Request
        // 1. Check if the user has rights to call the method from the service
        // 2. Check if the user can see the result
        // Make a call to a Business service
        // Get all Users filtered by active sessions
        // Perform authorization check for each active User
        // Attach User email and Id to

        // 1. Get service to be called, get arguments
        List<Object> methodArgs = extractTargetMethodArguments(contract);

        // 2. Authorize current User Request
        boolean isAuthorized = AuthorizationFactory.getAuthorizationManager().isAuthorized(currentUser, contract, methodArgs);

        // If User is unauthorized, send unauthorized message to the current User
        if (!isAuthorized) {
            sendUnauthorizedMessage(contract, currentUser);
            return;
        }

        // 3. Make a call to a Business service
        ServiceResult result = (ServiceResult) this.wsObjectProcessor.call(contract);

        //Construct a message from the service call result
        Message message = new Message(
                contract.getClassName(),
                contract.getMethodName(),
                result.getObject(),
                result.getNotificationType(),
                result.getMessage(),
                currentUser
        );

        // Attach permission to object
        if(result.getObject() instanceof List){
            boolean isPipelineGroupDtoList = isPipelineGroupDtoList(result);
            if(isPipelineGroupDtoList){
                attachPermissionsToPipelineDtos(contract, currentUser, result, methodArgs);
            }

            List<PermissionObject> filteredResult = attachPermissionTypeToList(contract, currentUser, result, methodArgs);

            message.setTargetOwner(true);
            message.setResultObject(filteredResult);
        } else {
            // 4. Get all Users filtered by active sessions
            List<SessionDetails> activeSessions =  SessionFactory.getSessionManager().getAllActiveSessions();

            Map<String, PermissionType> permissionTypeByUser = getPermissionTypeByUser(contract, currentUser, methodArgs, result, activeSessions);

            message.setPermissionTypeByUser(permissionTypeByUser);
        }

        //broadcast the message
        this.publisher.publish("global", message);

    }

    private boolean isPipelineGroupDtoList(ServiceResult result) {
        if(result.getObject() != null && ((List) result.getObject()).size() > 0 && ((List) result.getObject()).get(0) instanceof PipelineGroupDto){
            return true;
        }

        return false;
    }

    private Map<String, PermissionType> getPermissionTypeByUser(WsContractDto contract, User currentUser, List<Object> methodArgs, ServiceResult result, List<SessionDetails> activeSessions) {
        Map<String, PermissionType> permissionTypeByUser = new HashMap<>();

        for (SessionDetails activeSession : activeSessions) {
            User userToSendTo = (User) userService.getById(activeSession.getUserId()).getObject();
            List<Grant> userPermissions = this.permissionService.sortPermissions(currentUser.getPermissions());
            PermissionType permissionType = AuthorizationFactory.getAuthorizationManager().determinePermissionTypeForObject(userPermissions, result.getObject(), contract, methodArgs);
            permissionTypeByUser.put(userToSendTo.getId(), permissionType);
        }
        return permissionTypeByUser;
    }

    private void sendUnauthorizedMessage(WsContractDto contract, User currentUser) {
        // TODO: Send to current user Session
        Message message = new Message(
                contract.getClassName(),
                contract.getMethodName(),
                null,
                NotificationType.ERROR,
                "Unauthorized",
                currentUser
        );
        message.setTargetOwner(true);
        this.publisher.publish("global", message);
    }

    /**
     * Filters the result based on user permissions and sets PermissionType to each object in it
     * If the PermissionType is NONE, the object will not be added to the filtered collection
     * Exception: If the user has no permission for a Pipeline Group, but has a permission for a Pipeline that belongs to it, it will not be added to the filtered collection
     * @param contract
     * @param currentUser
     * @param result
     * @return
     */
    private List<PermissionObject> attachPermissionTypeToList(WsContractDto contract, User currentUser, ServiceResult result, List<Object> parameters) {
        List<PermissionObject> permissionObjects =  (List<PermissionObject>) result.getObject();
        List<PermissionObject> filteredResult = new ArrayList<>();

        for (PermissionObject permissionObject : permissionObjects) {
            PermissionType permissionType = AuthorizationFactory
                    .getAuthorizationManager()
                    .determinePermissionTypeForObject(currentUser.getPermissions(), permissionObject, contract, parameters);

            if(permissionObject.getPermissionType() != PermissionType.NONE){
                permissionObject.setPermissionType(permissionType);
                filteredResult.add(permissionObject);
            }
        }
        return filteredResult;
    }

    private void attachPermissionsToPipelineDtos(WsContractDto contract, User currentUser, ServiceResult result, List<Object> parameters) {
        List<PipelineGroupDto> pipelineGroupDtos = (List<PipelineGroupDto>) result.getObject();
        for (PipelineGroupDto pipelineGroupDto : pipelineGroupDtos) {
            List<PipelineDefinitionDto> permissionObjects = pipelineGroupDto.getPipelines();

            for (PipelineDefinitionDto permissionObject : permissionObjects) {
                PermissionType permissionType = AuthorizationFactory.getAuthorizationManager().determinePermissionTypeForObject(currentUser.getPermissions(), permissionObject, contract, parameters);

                if(permissionObject.getPermissionType() != PermissionType.NONE){
                    permissionObject.setPermissionType(permissionType);
                }
            }
            pipelineGroupDto.setPipelines(permissionObjects);
        }
    }

    private List<Object> extractTargetMethodArguments(WsContractDto contract) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Gson jsonConverter = new GsonBuilder()
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
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
        return methodArgs;
    }

    public void processResponse(Message pubSubMessage) {
        WsContractDto contract = new WsContractDto(pubSubMessage.getServiceCalled(), "", pubSubMessage.getMethodCalled(), pubSubMessage.getResultObject(), pubSubMessage.getResultNotificationType(), pubSubMessage.getResultMessage());
        //this.get
//        SessionPool.getInstance().sendToAuthorizedSessions(contract);
    }

    private boolean shouldPublishResult(String methodName) {
        if (!methodName.startsWith("get")) {
            return true;
        } else {
            return false;
        }
    }
}
