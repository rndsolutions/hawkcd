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
import io.hawkcd.core.subscriber.Envelopе;
import io.hawkcd.model.Entity;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.User;
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

        List<Envelopе> methodArgs = contract.getArgs();
        boolean isAuthorized = AuthorizationFactory.getAuthorizationManager().isAuthorized(currentUser, contract, methodArgs);

        if (!isAuthorized) {
            Message message = new Message(
                    contract.getClassName(),
                    contract.getPackageName(),
                    contract.getMethodName(),
                    contract.getResult(),
                    NotificationType.ERROR,
                    "Unauthorized",
                    currentUser,
                    true
            );
            Publisher.getInstance().publish("global", message);
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

        // Attach permission to object

        if(result.getEntity() instanceof List){
            List<Entity> filteredEntities = AuthorizationFactory.getAuthorizationManager().filterResponse((List<Entity>) result.getEntity(), currentUser);
            result.setEntity(filteredEntities);
            message.setTargetOwner(true);
            message.setEnvelope(result.getEntity());
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
}