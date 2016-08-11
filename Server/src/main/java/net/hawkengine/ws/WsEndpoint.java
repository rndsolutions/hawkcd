package net.hawkengine.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mongodb.client.ListDatabasesIterable;
import net.hawkengine.core.utilities.EndpointConnector;
import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TokenAdapter;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import net.hawkengine.model.*;
import net.hawkengine.model.dto.UserDto;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.model.payload.TokenInfo;
import net.hawkengine.services.UserGroupService;
import net.hawkengine.services.UserService;
import net.hawkengine.services.filters.factories.SecurityServiceInvoker;
import net.hawkengine.services.interfaces.IUserGroupService;
import net.hawkengine.services.interfaces.IUserService;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WsEndpoint extends WebSocketAdapter {
    static final Logger LOGGER = Logger.getLogger(WsEndpoint.class.getClass());
    private Gson jsonConverter;
    private UUID id;
    private SecurityServiceInvoker securityServiceInvoker;
    private User loggedUser;
    private IUserGroupService userGroupService;
    private IUserService userService;
    private WsObjectProcessor wsObjectProcessor;

    public WsEndpoint() {
        this.id = UUID.randomUUID();
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        this.securityServiceInvoker = new SecurityServiceInvoker();
        this.userGroupService = new UserGroupService();
        this.userService = new UserService();
        this.wsObjectProcessor = new WsObjectProcessor();
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        LOGGER.info("Socket Connected: " + session);

        String tokenQuery = session.getUpgradeRequest().getQueryString();

        if (!tokenQuery.equals("token=null")) {
            String token = tokenQuery.substring(6);

            TokenInfo tokenInfo = TokenAdapter.verifyToken(token);
            this.setLoggedUser(tokenInfo.getUser());
            SessionPool.getInstance().add(this);

            UserDto userDto = new UserDto();
            userDto.setUsername(tokenInfo.getUser().getEmail());
            userDto.setPermissions(tokenInfo.getUser().getPermissions());

            ServiceResult serviceResult = new ServiceResult();
            serviceResult.setError(false);
            serviceResult.setMessage("User details retrieved successfully");
            serviceResult.setObject(userDto);

            EndpointConnector.passResultToEndpoint("UserInfo", "getUser", serviceResult);
        }
    }

    @Override
    public void onWebSocketText(String message) {
        WsContractDto contract = null;
        RemoteEndpoint remoteEndpoint = null;

        try {
            remoteEndpoint = this.getSession().getRemote();
            contract = this.resolve(message);
            if (contract == null) {
                contract = new WsContractDto();
                contract.setError(true);
                contract.setErrorMessage("Invalid Json was provided");
                remoteEndpoint.sendString(this.jsonConverter.toJson(contract));
                return;
            }

//            SchemaValidator schemaValidator = new SchemaValidator();
//            for (ConversionObject conversionObject : contract.getArgs()) {
//                Class objectClass = Class.forName(conversionObject.getPackageName());
//                Object object = this.jsonConverter.fromJson(conversionObject.getObject(), objectClass);
//                String result = schemaValidator.validate(object);
//
//                if (!result.equals("OK")) {
//                    contract.setError(true);
//                    contract.setErrorMessage(result);
//                    remoteEndpoint.sendString(serializer.toJson(contract));
//                    return;
//                }
//            }
            User currentUser = (User) this.userService.getById(this.loggedUser.getId()).getObject();

            this.setLoggedUser(currentUser);
            this.loggedUser.getPermissions().addAll(this.getUniqueUserGroupPermissions(this.loggedUser));

            List<Permission> orderedPermissions = this.sortPermissions(this.loggedUser.getPermissions());

            ServiceResult result = new ServiceResult();

            if (contract.getMethodName().equals("getAll") || contract.getMethodName().equals("getAllPipelineGroupDTOs") || contract.getMethodName().equals("getAllUserGroups")){
                    result = (ServiceResult)this.wsObjectProcessor.call(contract);
                    List<?> filteredEntities = this.securityServiceInvoker.processList((List<?>) result.getObject(), contract.getClassName(), orderedPermissions, contract.getMethodName());
                    result.setObject(filteredEntities);
            } else{
                boolean hasPermission = this.securityServiceInvoker.process(contract.getArgs()[0].getObject(), contract.getClassName(), orderedPermissions, contract.getMethodName());

                if (hasPermission){
                    result = (ServiceResult)this.wsObjectProcessor.call(contract);
                }
                else{
                    result.setError(true);
                    result.setObject(null);
                    result.setMessage("Unauthorized");
                }
            }
            contract.setResult(result.getObject());
            contract.setError(result.hasError());
            contract.setErrorMessage(result.getMessage());

            String jsonResult = this.jsonConverter.toJson(contract);
            remoteEndpoint.sendStringByFuture(jsonResult);
        } catch (RuntimeException e) {
            LOGGER.error(String.format(LoggerMessages.WSENDPOINT_ERROR, e));
            e.printStackTrace();
            this.errorDetails(contract, this.jsonConverter, e, remoteEndpoint);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        LOGGER.info("Socket Closed: [" + statusCode + "] " + reason);
        SessionPool.getInstance().remove(this);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }

    public WsContractDto resolve(String message) {
        WsContractDto contract = null;
        try {
            contract = this.jsonConverter.fromJson(message, WsContractDto.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }

        return contract;
    }

    public void send(WsContractDto contract) {
        if ((this.getSession() == null) || !this.getSession().isOpen()) {
            return;
        }

        RemoteEndpoint remoteEndpoint = this.getSession().getRemote();
        String jsonResult = this.jsonConverter.toJson(contract);
        remoteEndpoint.sendStringByFuture(jsonResult);
    }


    private void errorDetails(WsContractDto contract, Gson serializer, Exception e, RemoteEndpoint endPoint) {
        contract.setError(true);
        contract.setErrorMessage(e.getMessage());
        try {
            String errDetails = serializer.toJson(contract);
            endPoint.sendString(errDetails);
        } catch (IOException | RuntimeException e1) {
            e1.printStackTrace();
        }
    }

    private List<Permission> getUniqueUserGroupPermissions(User user) {
        List<Permission> userGroupPermissions = new ArrayList<>();
        String userId = user.getId();
        List<String> userGroupIds = user.getUserGroupIds();

        for (String userGroupId : userGroupIds) {
            UserGroup userGroup = (UserGroup) this.userGroupService.getById(userGroupId).getObject();
            List<String> userIds = userGroup.getUserIds();
            boolean isPresent = false;

            for (String userWithinGroupId : userIds) {
                if (userWithinGroupId.equals(userId)) {
                    isPresent = true;
                    break;
                }
            }
            if (isPresent) {
                List<Permission> userGroupPermissionsFromDb = userGroup.getPermissions();

                for (Permission userGroupPermissionFromDb : userGroupPermissionsFromDb) {
                    boolean isPermissionPresent = false;
                    for (Permission userPersmission : user.getPermissions()) {
                        if (userGroupPermissionFromDb.getPermissionScope() == userPersmission.getPermissionScope() &&
                                userGroupPermissionFromDb.getPermittedEntityId().equals(userPersmission.getPermittedEntityId()) &&
                                userGroupPermissionFromDb.getPermissionType() == userPersmission.getPermissionType()) {
                            isPermissionPresent = true;
                            break;
                        }
                    }
                    if (!isPermissionPresent) {
                        userGroupPermissions = this.addPermissionToList(userGroupPermissions, userGroupPermissionFromDb);
                    }
                }
            }
        }

        return userGroupPermissions;
    }

    private List<Permission> addPermissionToList(List<Permission> permissions, Permission permissionToAdd) {
        List<Permission> equalPermissions = new ArrayList<>();
        equalPermissions.add(permissionToAdd);
        int index = 0;

        for (int i = 0; i < permissions.size(); i++) {
            Permission permission = permissions.get(i);
            if (permission.getPermittedEntityId().equals(permissionToAdd.getPermittedEntityId())) {
                equalPermissions.add(permission);
                index = i;
            }
        }
        if (equalPermissions.size() > 1) {
            Permission permissionWithPriority = equalPermissions.stream().sorted((p1, p2) -> p1.getPermissionType().compareTo(p2.getPermissionType())).findFirst().orElse(null);
            permissions.set(index, permissionWithPriority);

            return permissions;
        }
        permissions.add(permissionToAdd);

        return permissions;
    }

    private List<Permission> sortPermissions(List<Permission> permissions) {
        List<Permission> sortedPermissions = new ArrayList<>();

        List<Permission> adminPermissions = permissions
                .stream()
                .filter(permission -> permission.getPermissionScope() == PermissionScope.SERVER)
                .sorted((p1, p2) -> p2.getPermissionType().compareTo(p1.getPermissionType()))
                .collect(Collectors.toList());
        List<Permission> pipelineGroupGlobalPermissions = permissions
                .stream()
                .filter(permission -> permission.getPermissionScope() == PermissionScope.PIPELINE_GROUP)
                .filter(permission -> permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString()))
                .sorted((p1, p2) -> p2.getPermissionType().compareTo(p1.getPermissionType()))
                .collect(Collectors.toList());
        List<Permission> pipelineGlobalPermissions = permissions
                .stream()
                .filter(permission -> permission.getPermissionScope() == PermissionScope.PIPELINE)
                .filter(permission -> permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()))
                .sorted((p1, p2) -> p2.getPermissionType().compareTo(p1.getPermissionType()))
                .collect(Collectors.toList());
        List<Permission> pipelineGroupPermissions = permissions
                .stream()
                .filter(permission -> permission.getPermissionScope() == PermissionScope.PIPELINE_GROUP)
                .filter(permission -> !permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString()))
                .sorted((p1, p2) -> p2.getPermissionType().compareTo(p1.getPermissionType()))
                .collect(Collectors.toList());
        List<Permission> pipelinePermissions = permissions
                .stream()
                .filter(permission -> permission.getPermissionScope() == PermissionScope.PIPELINE)
                .filter(permission -> !permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()))
                .sorted((p1, p2) -> p2.getPermissionType().compareTo(p1.getPermissionType()))
                .collect(Collectors.toList());

        sortedPermissions.addAll(adminPermissions);
        sortedPermissions.addAll(pipelineGroupGlobalPermissions);
        sortedPermissions.addAll(pipelineGlobalPermissions);
        sortedPermissions.addAll(pipelineGroupPermissions);
        sortedPermissions.addAll(pipelinePermissions);

        return sortedPermissions;
    }
}