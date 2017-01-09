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
import io.hawkcd.core.config.Config;
import io.hawkcd.core.publisher.Publisher;
import io.hawkcd.core.security.AuthorizationFactory;
import io.hawkcd.core.security.IAuthorizationManager;
import io.hawkcd.model.*;
import io.hawkcd.model.dto.PipelineGroupDto;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.utilities.deserializers.MaterialDefinitionAdapter;
import io.hawkcd.utilities.deserializers.TaskDefinitionAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * The responsibility of the class is to process all requests passed by the WSSocket object
 */
public class RequestProcessor {
    private WsObjectProcessor wsObjectProcessor;
    private IAuthorizationManager authorizationManager;

    public RequestProcessor() {
        this.authorizationManager = AuthorizationFactory.getAuthorizationManager();
        this.wsObjectProcessor = new WsObjectProcessor();
    }

    /**
     * All WS requests flows through this method, auhtorization checks are performed,
     * and the request is broadcasted to all subscribers
     *
     * WF:
     * Get service to be called, get arguments
     * Authorize current User
     * 1. Check if the user has rights to call the method from the service
     * 2. Check if the user can see the result
     * Make a call to a Business service
     * Get all Users filtered by active sessions
     * Perform authorization check for each active User
     * Attach User email and Ids
     */
    public void processRequest(WsContractDto contract, User currentUser)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {

        List<Object> methodArgs = extractTargetMethodArguments(contract);
        boolean isAuthorized = AuthorizationFactory.getAuthorizationManager().isAuthorized(currentUser, contract, methodArgs);

        if (!isAuthorized) {
            sendUnauthorizedMessage(contract, currentUser);
            return;
        }

        ServiceResult result = (ServiceResult) this.wsObjectProcessor.call(contract);

        Message message = MessageConverter.convert(currentUser
                , contract.getClassName()
                , contract.getPackageName()
                , contract.getMethodName()
                , result);

        if (result.getNotificationType() == NotificationType.ERROR) {
            message.setTargetOwner(true);
            Publisher.getInstance().publish("global", message);
        }

        if (result.getEntity() instanceof List) {
            boolean isPipelineGroupDtoList = isPipelineGroupDtoList(result);
            if (isPipelineGroupDtoList) {
                List<PipelineGroupDto> pipelineGroupDtosWithPermissions = this.authorizationManager
                        .attachPermissionsToPipelineDtos((List<PipelineGroupDto>) result.getEntity(), currentUser);
                result.setEntity(pipelineGroupDtosWithPermissions);
            }
            List<Entity> filteredResult = this.authorizationManager.attachPermissionTypeToList(message, methodArgs);
            message.setTargetOwner(true);
            message.setEnvelope(filteredResult);
        } else {
            if(Config.getConfiguration().getIsSingleNode()){
                PermissionType permissionType = this.authorizationManager.determinePermissionTypeForEntity(currentUser.getPermissions(), result.getEntity());
                ((Entity) result.getEntity()).setPermissionType(permissionType);
                message.setEnvelope(result.getEntity());
            } else {
                message = this.authorizationManager.attachPermissionTypeMapToMessage(message, methodArgs);
            }
        }

        MessageDispatcher.dispatchIncomingMessage(message);
    }

    private boolean isPipelineGroupDtoList(ServiceResult result) {
        if (result.getEntity() != null && ((List) result.getEntity()).size() > 0 && ((List) result.getEntity()).get(0) instanceof PipelineGroupDto) {
            return true;
        }

        return false;
    }

    private void sendUnauthorizedMessage(WsContractDto contract, User currentUser) {
        // TODO: Send to current user Session
        Message message = new Message(
                contract.getClassName(),
                contract.getPackageName(),
                contract.getMethodName(),
                null,
                NotificationType.ERROR,
                "Unauthorized",
                currentUser
        );
        message.setTargetOwner(true);
        Publisher.getInstance().publish("global", message);
    }

    private List<Object> extractTargetMethodArguments(WsContractDto contract) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Gson jsonConverter = new GsonBuilder()
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        List<Object> methodArgs = new ArrayList<>();
        int contractArgsLength = contract.getArgs().size();
        for (int i = 0; i < contractArgsLength; i++) {
            if (contract.getArgs().get(i).getObject() != null) {
                Object object = contract.getArgs().get(i).getObject();
                methodArgs.add(object);
            }
        }
        return methodArgs;
    }
}