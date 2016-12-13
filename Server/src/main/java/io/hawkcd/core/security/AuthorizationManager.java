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
import io.hawkcd.core.subscriber.Envelopе;
import io.hawkcd.model.*;
import io.hawkcd.model.dto.PipelineDefinitionDto;
import io.hawkcd.model.dto.PipelineGroupDto;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.PermissionEntity;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.services.UserService;
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

    public AuthorizationManager() {
        this.userService = new UserService();
    }

    @Override
    public boolean isAuthorized(User user, WsContractDto contract, List<Envelopе> parameters)
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

    private Authorization getMethodAuthorizationAttributes(WsContractDto contractDto, List<Envelopе> parameters)
            throws ClassNotFoundException, NoSuchMethodException {

        String fullyQualifiedName = String.format("%s.%s", contractDto.getPackageName(), contractDto.getClassName());
        Class<?> aClass = Class.forName(fullyQualifiedName);
        Class<?>[] params = new Class[parameters.size()];
        for (int i = 0; i < params.length; i++) {
            Class<?> aClass1 = parameters.get(i).getObject().getClass();
            params[i] = aClass1;
        }
        Method method = aClass.getMethod(contractDto.getMethodName(), params);
        Authorization annotation = method.getAnnotation(Authorization.class);

        return annotation;
    }

    /**
     * @param result
     * @param className
     * @param methodName
     * @return
     */
    public Message constructAuthorizedMessage(ServiceResult result, String className, String methodName) {

        Message message = new Message(className, methodName, result, null);

        Map<String, PermissionType> userMap = new HashMap<>();

        message.setPermissionTypeByUser(userMap);

        Entity entity = (Entity) message.getEnvelope();

        List<SessionDetails> allActiveSessions = SessionFactory.getSessionManager().getAllActiveSessions();

        for (SessionDetails sessionDetail : allActiveSessions) {
            User user = (User) userService.getByEmail(sessionDetail.getUserEmail()).getEntity();

            List<AuthorizationGrant> userGrant = user.getPermissions();

            Authorization authorization = entity.getClass().getAnnotation(Authorization.class);
            AuthorizationGrant entityGrant = new AuthorizationGrant(authorization);

            PermissionType permissionType = this.determinePermissionTypeForUser(userGrant, entityGrant, entity.getId());

            userMap.put(user.getId(), permissionType);
        }
        return message;
    }

    /**
     * Determines the Permission Type of the user based on the entity IDs passed to it
     */
    public PermissionType determinePermissionTypeForUser(List<AuthorizationGrant> userGrants, AuthorizationGrant grantToEvaluateAgainst, String... entityIds) {
        PermissionType result = PermissionType.NONE;

        if (grantToEvaluateAgainst.getPermissionType() == PermissionType.NONE) {
            return PermissionType.VIEWER;
        }
        // Checks for specific Permissions, e.g. a user has a grant for a specific entity (Pipeline/Group)
        for (String entityId : entityIds) {
            for (AuthorizationGrant grant : userGrants) {
                if (grant.getPermissionEntity() == PermissionEntity.SPECIFIC_ENTITY && grant.getPermittedEntityId().equals(entityId)) {
                    if (grant.isGreaterThan(grantToEvaluateAgainst)) {
                        return grant.getPermissionType();
                    }
                }
            }
        }

        // Checks for generic Permissions, e.g. a user is assigned a permission for ALL Pipelines/Groups or Server
        for (AuthorizationGrant grant : userGrants) {
            if (grant.getPermissionEntity() != PermissionEntity.SPECIFIC_ENTITY) {
                if (grant.isGreaterThan(grantToEvaluateAgainst)) {
                    return grant.getPermissionType();
                }
            }
        }

        return result;
    }

    /**
     * Checks the object for minimum permissions required for the user to receive the object
     */
    public PermissionType determinePermissionTypeForEntity(List<AuthorizationGrant> userGrants, Object object, List<Envelopе> parameters) {
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
     * The method extracts the object Ids necessary for the authorization, based on the class and
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

    /**
     * Loops through
     */
    public Message attachPermissionTypeMapToMessage(Message message, List<Envelopе> methodArgs) {

        List<SessionDetails> activeSessions = SessionFactory.getSessionManager().getAllActiveSessions();

        Map<String, PermissionType> permissionTypeByUser = this.getPermissionTypeByUser(message, methodArgs, activeSessions);

        message.setPermissionTypeByUser(permissionTypeByUser);

        return message;
    }

    private Map<String, PermissionType> getPermissionTypeByUser(Message message, List<Envelopе> methodArgs, List<SessionDetails> activeSessions) {
        Map<String, PermissionType> permissionTypeByUser = new HashMap<>();

        for (SessionDetails activeSession : activeSessions) {
            User userToSendTo = (User) userService.getById(activeSession.getUserId()).getEntity();
            PermissionType permissionType = this.determinePermissionTypeForEntity(userToSendTo.getPermissions(), message.getEnvelope(), methodArgs);
            permissionTypeByUser.put(userToSendTo.getId(), permissionType);
        }
        return permissionTypeByUser;
    }

    /**
     * Filters the result based on user permissions and sets PermissionType to each object in it If
     * the PermissionType is NONE, the object will not be added to the filtered collection
     * Exception: If the user has no permission for a Pipeline Group, but has a permission for a
     * Pipeline that belongs to it, it will be added to the filtered collection.
     */
    public List<Entity> attachPermissionTypeToList(List<Entity> entities, List<AuthorizationGrant> userGrants) {
        List<Entity> filteredResult = new ArrayList<>();

        for (Entity entity : entities) {
            PermissionType permissionType = this.determinePermissionTypeForEntity(userGrants, entity);

            if (permissionType != PermissionType.NONE) {
                entity.setPermissionType(permissionType);
                filteredResult.add(entity);
            } else if (entity instanceof PipelineGroupDto && ((PipelineGroupDto) entity).getPipelines().size() > 0) {
                entity.setPermissionType(PermissionType.VIEWER);
                filteredResult.add(entity);
            }
        }

        return filteredResult;
    }

    public List<PipelineGroupDto> attachPermissionsToPipelineDtos(List<PipelineGroupDto> pipelineGroupDtos, User currentUser) {
        for (PipelineGroupDto pipelineGroupDto : pipelineGroupDtos) {
            List<PipelineDefinitionDto> pipelineDefinitionDtos = pipelineGroupDto.getPipelines();
            List<PipelineDefinitionDto> filteredPipelineDefinitionDtos = new ArrayList<>();
            for (PipelineDefinitionDto pipelineDefinitionDto : pipelineDefinitionDtos) {
                PermissionType permissionType = this.determinePermissionTypeForEntity(currentUser.getPermissions(), pipelineDefinitionDto);

                if (permissionType != PermissionType.NONE) {
                    pipelineDefinitionDto.setPermissionType(permissionType);
                    filteredPipelineDefinitionDtos.add(pipelineDefinitionDto);
                }
            }

            pipelineGroupDto.setPipelines(filteredPipelineDefinitionDtos);
        }
        return pipelineGroupDtos;
    }

    public List<Entity> filterResponse(List<Entity> entities, User currentUser) {
        List<Entity> entitiesWithPermissions = new ArrayList<>();

        if (entities != null && entities.size() > 0 && (entities.get(0) instanceof PipelineGroupDto)) {
            entitiesWithPermissions = (List<Entity>)(List<?>)this.attachPermissionsToPipelineDtos((List<PipelineGroupDto>)(List<?>) entities, currentUser);
            entitiesWithPermissions = this.attachPermissionTypeToList(entitiesWithPermissions, currentUser.getPermissions());
        } else{
            entitiesWithPermissions = this.attachPermissionTypeToList(entities, currentUser.getPermissions());
        }


        return entitiesWithPermissions;
    }
}