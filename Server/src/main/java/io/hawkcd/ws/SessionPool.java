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

import io.hawkcd.model.PermissionObject;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.User;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.payload.Permission;
import io.hawkcd.services.PipelineDefinitionService;
import io.hawkcd.services.UserGroupService;
import io.hawkcd.services.filters.PermissionService;
import io.hawkcd.services.UserService;
import io.hawkcd.services.filters.factories.SecurityServiceInvoker;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.hawkcd.services.AgentService;
import io.hawkcd.services.PipelineGroupService;
import io.hawkcd.services.PipelineService;

public class SessionPool {
    private static final Logger LOGGER = Logger.getLogger(SessionPool.class.getClass());
    private static SessionPool instance;

    private List<WSSocket> sessions;
    private PermissionService permissionService;
    private SecurityServiceInvoker securityServiceInvoker;
    private AgentService agentService;
    private PipelineGroupService pipelineGroupService;
    private PipelineDefinitionService pipelineDefinitionService;
    private PipelineService pipelineService;
    private UserService userService;
    private UserGroupService userGroupService;
    private EntityPermissionTypeServiceInvoker entityPermissionTypeServiceInvoker;

    private SessionPool() {
        this.sessions = Collections.synchronizedList(new ArrayList<WSSocket>());
        this.permissionService = new PermissionService();
        this.securityServiceInvoker = new SecurityServiceInvoker();

        this.agentService = new AgentService();
        this.pipelineGroupService = new PipelineGroupService();
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.pipelineService = new PipelineService();
        this.userService = new UserService();
        this.userGroupService = new UserGroupService();
        this.entityPermissionTypeServiceInvoker = new EntityPermissionTypeServiceInvoker();
    }

    public static synchronized SessionPool getInstance() {
        if (instance == null) {
            instance = new SessionPool();
        }

        return instance;
    }

    public synchronized void add(WSSocket session) {
        sessions.add(session);
        String email = session.getLoggedUser().getEmail();
        int userActiveSessions = this.countActiveSessions(email);
        LOGGER.info("Session opened - User: " + email + " Active Sessions: " + userActiveSessions);
    }

    public synchronized void remove(WSSocket session) {
        sessions.remove(session);
    }

    public void sendToAuthorizedSessions(WsContractDto contractDto) {
        synchronized (sessions) {
            if (contractDto.getResult() == null) {
                for (WSSocket session : sessions) {
                    session.send(contractDto);
                }
            } else {
                Class objectClass = contractDto.getResult().getClass();
                for (WSSocket session : sessions) {
                    User loggedUser = session.getLoggedUser();
                    if (loggedUser != null) {
                        loggedUser.getPermissions().addAll(this.permissionService.getUniqueUserGroupPermissions(loggedUser));
                        List<Permission> permissions = this.permissionService.sortPermissions(loggedUser.getPermissions());
                        PermissionObject result = this.entityPermissionTypeServiceInvoker.invoke(objectClass, permissions, (PermissionObject) contractDto.getResult());
                        if (result.getPermissionType() != PermissionType.NONE) {
                            contractDto.setResult(result);
                            session.send(contractDto);
                        }
                    }
                }
            }
        }
    }

    public void sendToUserSessions(WsContractDto contractDto, User user) {
        synchronized (sessions) {
            List<WSSocket> userSessions = sessions
                    .stream()
                    .filter(e -> e.getLoggedUser().getId().equals(user.getId()))
                    .collect(Collectors.toList());
            for (WSSocket userSession : userSessions) {
                userSession.send(contractDto);
            }
        }
    }

    public void sendToSingleUserSession(WsContractDto contractDto, String sessionId) {
        synchronized (sessions) {
            WSSocket session = sessions.stream().filter(s -> s.getId().equals(sessionId)).findFirst().orElse(null);
            if (session != null) {
                session.send(contractDto);
            }
        }
    }

    public void logoutUserFromAllSessions(String email) {
        synchronized (sessions) {
            List<WSSocket> userSessions = sessions
                    .stream()
                    .filter(e -> e.getLoggedUser().getEmail().equals(email))
                    .collect(Collectors.toList());

            for (WSSocket userSession : userSessions) {
                WsContractDto contract = new WsContractDto();
                contract.setClassName("UserService");
                contract.setMethodName("logout");
                contract.setNotificationType(NotificationType.SUCCESS);
                userSession.send(contract);
                userSession.getSession().close(1000, "User logged out successfully.");
                userSession.setLoggedUser(null);
            }

            LOGGER.info("All Sessions closed - User: " + email);
        }
    }

    public int countActiveSessions(String userEmail) {
        synchronized (sessions) {
            int count = 0;
            for (WSSocket session : sessions) {
                if (session.getLoggedUser().getEmail().equals(userEmail)) {
                    count++;
                }
            }

            return count;
        }
    }

    public void updateUserObjects(String userId) {
        User loggedUser = (User) this.userService.getById(userId).getObject();
        loggedUser.getPermissions().addAll(this.permissionService.getUniqueUserGroupPermissions(loggedUser));
        List<Permission> permissions = this.permissionService.sortPermissions(loggedUser.getPermissions());

        ServiceResult agentsResult = this.agentService.getAll();
        List<?> filteredAgents = this.securityServiceInvoker.filterEntities((List<?>) agentsResult.getObject(), "AgentService", permissions, "getAll");
        agentsResult.setObject(filteredAgents);
        EndpointConnector.passResultToEndpoint("AgentService", "getAll", agentsResult, loggedUser);

        ServiceResult pipelineGroupDtosResult = this.pipelineGroupService.getAllPipelineGroupDTOs();
        List<?> filteredPipelineGroupDtos = this.securityServiceInvoker.filterEntities((List<?>) pipelineGroupDtosResult.getObject(), "PipelineGroupService", permissions, "getAllPipelineGroupDTOs");
        pipelineGroupDtosResult.setObject(filteredPipelineGroupDtos);
        EndpointConnector.passResultToEndpoint("PipelineGroupService", "getAllPipelineGroupDTOs", pipelineGroupDtosResult, loggedUser);

        ServiceResult pipelineDefinitionsResult = this.pipelineDefinitionService.getAll();
        List<?> filteredPipelineDefinitions = this.securityServiceInvoker.filterEntities((List<?>) pipelineDefinitionsResult.getObject(), "PipelineDefinitionService", permissions, "getAll");
        pipelineDefinitionsResult.setObject(filteredPipelineDefinitions);
        EndpointConnector.passResultToEndpoint("PipelineDefinitionService", "getAll", pipelineDefinitionsResult, loggedUser);

        ServiceResult pipelinesResult = this.pipelineService.getAll();
        List<?> filteredPipelines = this.securityServiceInvoker.filterEntities((List<?>) pipelinesResult.getObject(), "PipelineService", permissions, "getAll");
        pipelinesResult.setObject(filteredPipelines);
        EndpointConnector.passResultToEndpoint("PipelineService", "getAll", pipelinesResult, loggedUser);

        ServiceResult usersResult = this.userService.getAll();
        List<?> filteredUsers = this.securityServiceInvoker.filterEntities((List<?>) usersResult.getObject(), "UserService", permissions, "getAll");
        usersResult.setObject(filteredUsers);
        EndpointConnector.passResultToEndpoint("UserService", "getAll", usersResult, loggedUser);

        ServiceResult userGroupDtosResult = this.userGroupService.getAllUserGroups();
        List<?> filteredUserGroupDtos = this.securityServiceInvoker.filterEntities((List<?>) userGroupDtosResult.getObject(), "UserGroupService", permissions, "getAllUserGroups");
        userGroupDtosResult.setObject(filteredUserGroupDtos);
        EndpointConnector.passResultToEndpoint("UserGroupService", "getAllUserGroups", userGroupDtosResult, loggedUser);
    }
}