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

package io.hawkcd.core.security;

import io.hawkcd.core.Message;
import io.hawkcd.core.session.SessionFactory;
import io.hawkcd.model.Entity;
import io.hawkcd.model.PipelineFamily;
import io.hawkcd.model.PipelineGroup;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.SessionDetails;
import io.hawkcd.model.User;
import io.hawkcd.model.dto.PipelineGroupDto;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.services.UserService;

import io.hawkcd.services.UserService;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class is responisble for authorizing User's requests
 * <p>
 * Workflow:
 * Evaluate currentUser permissions,
 * if method call is allowed
 * Get all active sessions from database (cluster)
 * Filter all users with active session
 * Evaluate each user permissions and prepare the message object to be broadcast
 * Send message to all subscribers
 */
public class AuthorizationManager implements IAuthorizationManager {
    private static final Logger LOGGER = Logger.getLogger(AuthorizationManager.class);

    private UserService userService;

    public  AuthorizationManager(){
        this.userService = new UserService();
    }

    @Override
    public boolean isAuthorized(User user, WsContractDto contract, List<Object> parameters)
            throws ClassNotFoundException, NoSuchMethodException {

        Authorization authorizationAttributes = this.getMethodAuthorizationAttributes(contract, parameters);
        if (authorizationAttributes == null) {
            return false;
        }
        String[] entityIds = this.extractEntityIds(parameters);
        AuthorizationGrant grant = new AuthorizationGrant(authorizationAttributes);

        PermissionType permissionType = this.determinePermissionTypeForUser(user.getPermissions(), grant, entityIds);
        if (permissionType != PermissionType.NONE) {
            return true;
        }
        return false;
    }

    @Override
    public void getAllUsersWithPermissions() {
        throw new NotImplementedException();
    }


    private Authorization getMethodAuthorizationAttributes(WsContractDto contractDto, List<Object> parameters)
            throws ClassNotFoundException, NoSuchMethodException {

        String fullyQualifiedName = String.format("%s.%s", contractDto.getPackageName(), contractDto.getClassName());
        Class<?> aClass = Class.forName(fullyQualifiedName);
        Class<?>[] params = new Class[parameters.size()];
        for (int i = 0; i < params.length; i++) {
            Class<?> aClass1 = parameters.get(i).getClass();
            params[i] = aClass1;
        }
        Method method = aClass.getMethod(contractDto.getMethodName(), params);
        Authorization annotation = method.getAnnotation(Authorization.class);

        return annotation;
    }

    /**
     *
     * @param result
     * @param className
     * @param methodName
     * @return
     */
    public Message constructAuthorizedMessage(ServiceResult result, String className, String methodName){

        Message message = new Message(className,methodName,result,null);

        Map<String, PermissionType> userMap =  new HashMap<>();

        message.setPermissionTypeByUser(userMap);

        Entity entity = (Entity)message.getEnvelop();

        List<SessionDetails> allActiveSessions = SessionFactory.getSessionManager().getAllActiveSessions();

        for (SessionDetails sessionDetail : allActiveSessions) {
            User user = (User) userService.getByEmail(sessionDetail.getUserEmail()).getEntity();

            List<AuthorizationGrant> userGrant = user.getPermissions();

            Authorization authorization = entity.getClass().getAnnotation(Authorization.class);
            AuthorizationGrant entityGrant = new AuthorizationGrant(authorization);

            PermissionType permissionType = this.determinePermissionTypeForUser(userGrant, entityGrant, entity.getId());

            userMap.put(user.getId(),permissionType);
        }
        return message;
    }

    /**
     * Determines the Permission Type of the user based on the entity IDs passed to it
     */
    public PermissionType determinePermissionTypeForUser(List<AuthorizationGrant> userGrants, AuthorizationGrant grantToEvaluateAgainst, String... entityIds) {
        PermissionType result = PermissionType.NONE;

        if (grantToEvaluateAgainst.getType() == PermissionType.NONE) {
            return PermissionType.VIEWER;
        }
        // Checks for specific Permissions, e.g. a user is assigned a permission for a specific entity (Pipeline/Group)
        for (String entityId : entityIds) {
            for (AuthorizationGrant grant : userGrants) {
                if (grant.getPermittedEntityId().equals(entityId)) {
                    if (grant.isGreaterThan(grantToEvaluateAgainst)) {
                        return grant.getType();
                    }
                }
            }
        }

        // Checks for generic Permissions, e.g. a user is assigned a permission for ALL Pipelines/Groups
        for (AuthorizationGrant grant : userGrants) {
            if (grant.getPermittedEntityId().startsWith("ALL") || (grant.getScope() == PermissionScope.SERVER)) {
                if (grant.isGreaterThan(grantToEvaluateAgainst)) {
                    return grant.getType();
                }
            }
        }

        return result;
    }

    /**
     * Checks the object for minimum permissions required for the user to receive the object
     */
    public PermissionType determinePermissionTypeForEntity(List<AuthorizationGrant> userGrants, Object object, List<Object> parameters) {
        PermissionType result;
        Authorization authorization = object.getClass().getAnnotation(Authorization.class);
        AuthorizationGrant grant = new AuthorizationGrant(authorization);

        String[] entityIds = this.extractEntityIds(parameters);
        result = this.determinePermissionTypeForUser(userGrants, grant, entityIds);
        return result;
    }

    public PermissionType determinePermissionTypeForEntity(List<AuthorizationGrant> userGrants, Object object) {
        PermissionType result;
        Authorization authorization = object.getClass().getAnnotation(Authorization.class);
        AuthorizationGrant grant = new AuthorizationGrant(authorization);

        String[] entityIds = this.extractEntityIds(object);
        result = this.determinePermissionTypeForUser(userGrants, grant, entityIds);
        return result;
    }

    /**
     * The method extracts the object Ids necessary for the authorization based on the class and
     * method being invoked.
     */
    public String[] extractEntityIds(List<Object> parameters) {
        List<String> entityIds = new ArrayList<>();
        for (Object parameter : parameters) {
            if (parameter instanceof PipelineFamily) {
                PipelineFamily pipelineFamily = (PipelineFamily) parameter;
                entityIds.add(pipelineFamily.getPipelineDefinitionId());
                entityIds.add(pipelineFamily.getPipelineGroupId());
            } else if (parameter instanceof PipelineGroup) {
                Entity pipelineGroup = (Entity) parameter;
                entityIds.add(pipelineGroup.getId());
            }
        }

        return entityIds.toArray(new String[entityIds.size()]);
    }

    public String[] extractEntityIds(Object parameter) {
        List<String> entityIds = new ArrayList<>();
        if (parameter instanceof PipelineFamily) {
            PipelineFamily pipelineFamily = (PipelineFamily) parameter;
            entityIds.add(pipelineFamily.getPipelineDefinitionId());
            entityIds.add(pipelineFamily.getPipelineGroupId());
        } else if (parameter instanceof PipelineGroup || parameter instanceof PipelineGroupDto) {
            Entity pipelineGroup = (Entity) parameter;
            entityIds.add(pipelineGroup.getId());
        }

        return entityIds.toArray(new String[entityIds.size()]);
    }

}
