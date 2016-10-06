package net.hawkengine.ws;

import net.hawkengine.model.PermissionObject;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.*;
import net.hawkengine.services.filters.PermissionService;
import net.hawkengine.services.filters.factories.SecurityServiceInvoker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionPool {
    private static SessionPool instance;

    private List<WsEndpoint> sessions;
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
        this.sessions = new ArrayList<>();
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

    public List<WsEndpoint> getSessions() {
        return sessions;
    }

    public void add(WsEndpoint session) {
        sessions.add(session);
    }

    public void remove(WsEndpoint session) {
        sessions.remove(session);
    }

    public void sendToUserSessions(WsContractDto contractDto, User user) {
        List<WsEndpoint> userSessions = sessions
                .stream()
                .filter(e -> e.getLoggedUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
        for (WsEndpoint userSession : userSessions) {
            userSession.send(contractDto);
        }
    }

    public void sendToAuthorizedSessions(WsContractDto contractDto) {
        if (contractDto.getResult() == null) {
            for (WsEndpoint session : sessions) {
                session.send(contractDto);
            }
        } else {
            Class objectClass = contractDto.getResult().getClass();
            for (WsEndpoint session : sessions) {
                User loggedUser = session.getLoggedUser();
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

    public void logoutUserFromAllSessions(String email) {

        List<WsEndpoint> userSessions = sessions
                .stream()
                .filter(e -> e.getLoggedUser().getEmail().equals(email))
                .collect(Collectors.toList());

        for (WsEndpoint userSession : userSessions) {
            WsContractDto contract = new WsContractDto();
            contract.setClassName("UserService");
            contract.setMethodName("logout");
            contract.setNotificationType(NotificationType.SUCCESS);
            userSession.send(contract);
            userSession.getSession().close(1000, "User logged out successfully.");
            userSession.setLoggedUser(null);
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

//    public void placeholder(WsContractDto contractDto, User user) {
//        if (contractDto.isError()) {
//            List<WsEndpoint> userSessions = sessions
//                    .stream()
//                    .filter(e -> e.getLoggedUser().getId().equals(user.getId()))
//                    .collect(Collectors.toList());
//            for (WsEndpoint userSession : userSessions) {
//                userSession.send(contractDto);
//            }
//        } else {
//            Class<?> objectClass = contractDto.getResult().getClass();
//            if (objectClass == ArrayList.class) {
//                List<?> resultAsList = (List<?>) contractDto.getResult();
//                if (!resultAsList.isEmpty()) {
//                    objectClass = resultAsList.get(0).getClass();
//                    for (WsEndpoint session : sessions) {
//                        List<Permission> permissions = session.getLoggedUser().getPermissions();
//                        List<Object> result = new ArrayList<>();
//                        for (Object object : resultAsList) {
//                            DbEntry updatedObject = EntityPermissionTypeServiceInvoker.invoke(objectClass, permissions, (DbEntry) object);
//                            if (updatedObject.getPermissionType() != PermissionType.NONE) {
//                                result.add(updatedObject);
//                            }
//                        }
//
//                        contractDto.setResult(result);
//                        session.send(contractDto);
//                    }
//                } else {
//                    for (WsEndpoint session : sessions) {
//                        session.send(contractDto);
//                    }
//                }
//            } else {
//                for (WsEndpoint session : sessions) {
//                    List<Permission> permissions = session.getLoggedUser().getPermissions();
//                    DbEntry result = EntityPermissionTypeServiceInvoker.invoke(objectClass, permissions, (DbEntry) contractDto.getResult());
//                    if (result.getPermissionType() != PermissionType.NONE) {
//                        contractDto.setResult(result);
//                        session.send(contractDto);
//                    }
//                }
//            }
//        }
//    }
}