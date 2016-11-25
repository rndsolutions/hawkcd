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

import io.hawkcd.model.Entity;
import io.hawkcd.model.PipelineFamily;
import io.hawkcd.model.PipelineGroup;
import io.hawkcd.model.User;
import io.hawkcd.model.dto.PipelineGroupDto;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public boolean isAuthorized(User user, WsContractDto contract, List<Object> parameters)
            throws ClassNotFoundException, NoSuchMethodException {

        Authorization authorizationAttributes = this.getMethodAuthorizationAttributes(contract, parameters);
        if (authorizationAttributes == null) {
            return false;
        }
        String[] entityIds = this.extractEntityIds(parameters);
        Grant grant = new Grant(authorizationAttributes);

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
     * Determines the Permission Type of the user based on the entity IDs passed to it
     */
    public PermissionType determinePermissionTypeForUser(List<Grant> userGrants, Grant grantToEvaluateAgainst, String... entityIds) {
        PermissionType result = PermissionType.NONE;

        if (grantToEvaluateAgainst.getType() == PermissionType.NONE) {
            return PermissionType.VIEWER;
        }
        // Checks for specific Permissions, e.g. a user is assigned a permission for a specific entity (Pipeline/Group)
        for (String entityId : entityIds) {
            for (Grant grant : userGrants) {
                if (grant.getPermittedEntityId().equals(entityId)) {
                    if (grant.isGreaterThan(grantToEvaluateAgainst)) {
                        return grant.getType();
                    }
                }
            }
        }

        // Checks for generic Permissions, e.g. a user is assigned a permission for ALL Pipelines/Groups
        for (Grant grant : userGrants) {
            if (grant.getPermittedEntityId().startsWith("ALL") || grant.getScope().equals(PermissionScope.SERVER)) {
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
    public PermissionType determinePermissionTypeForEntity(List<Grant> userGrants, Object object, List<Object> parameters) {
        PermissionType result;
        Authorization authorization = object.getClass().getAnnotation(Authorization.class);
        Grant grant = new Grant(authorization);

        String[] entityIds = this.extractEntityIds(parameters);
        result = this.determinePermissionTypeForUser(userGrants, grant, entityIds);
        return result;
    }

    public PermissionType determinePermissionTypeForEntity(List<Grant> userGrants, Object object) {
        PermissionType result;
        Authorization authorization = object.getClass().getAnnotation(Authorization.class);
        Grant grant = new Grant(authorization);

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
