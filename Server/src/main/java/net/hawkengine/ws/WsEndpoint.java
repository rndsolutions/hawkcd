package net.hawkengine.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.hawkengine.core.utilities.constants.LoggerMessages;
import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TokenAdapter;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.User;
import net.hawkengine.model.dto.UserDto;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.model.payload.TokenInfo;
import net.hawkengine.services.UserService;
import net.hawkengine.services.filters.PermissionService;
import net.hawkengine.services.filters.factories.SecurityServiceInvoker;
import net.hawkengine.services.interfaces.IUserService;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

    public class WsEndpoint extends WebSocketAdapter {
    static final Logger LOGGER = Logger.getLogger(WsEndpoint.class.getClass());
    private Gson jsonConverter;
    private UUID id;
    private SecurityServiceInvoker securityServiceInvoker;
    private User loggedUser;
    private PermissionService permissionService;
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
        this.permissionService = new PermissionService();
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
        return this.loggedUser;
    }

    public User getLoggedUserFromDatabase() {
        return (User) this.userService.getById(this.loggedUser.getId()).getObject();
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
//        LOGGER.info("Socket Connected: " + session);

        String tokenQuery = session.getUpgradeRequest().getQueryString();

        if (!tokenQuery.equals("token=null")) {
            String token = tokenQuery.substring(6);

            TokenInfo tokenInfo = TokenAdapter.verifyToken(token);
            this.setLoggedUser(tokenInfo.getUser());
            SessionPool.getInstance().add(this);
            LOGGER.info("Session opened - User: " + this.loggedUser.getEmail());

            if (this.userService.getById(tokenInfo.getUser().getId()).getObject() != null) {

                UserDto userDto = new UserDto();
                userDto.setUsername(tokenInfo.getUser().getEmail());
                userDto.setPermissions(tokenInfo.getUser().getPermissions());

//                ServiceResult serviceResult = new ServiceResult(userDto, NotificationType.SUCCESS, "User details retrieved successfully");

                WsContractDto contract = new WsContractDto();
                contract.setClassName("UserInfo");
                contract.setMethodName("getUser");
                contract.setResult(userDto);
                contract.setNotificationType(NotificationType.SUCCESS);
                contract.setErrorMessage("User details retrieved successfully");
                SessionPool.getInstance().sendToUserSessions(contract, this.getLoggedUser());
            } else {
                UserDto userDto = new UserDto();
                userDto.setUsername(tokenInfo.getUser().getEmail());
                userDto.setPermissions(tokenInfo.getUser().getPermissions());
                ServiceResult result = new ServiceResult(userDto, NotificationType.SUCCESS, "User does not exist.");

                EndpointConnector.passResultToEndpoint("UserInfo", "getUser", result, this.getLoggedUser());
                EndpointConnector.passResultToEndpoint("UserInfo", "logoutSession", result, this.getLoggedUser());
                return;
            }
        }
    }

    @Override
    public void onWebSocketText(String message) {
        WsContractDto contract = null;
        RemoteEndpoint remoteEndpoint = null;

        if (this.getLoggedUserFromDatabase() == null) {
            return;
        }

        if (this.loggedUser == null) {
            this.getSession().close();
            return;
        }

        try {
            remoteEndpoint = this.getSession().getRemote();
            contract = this.resolve(message);
            if (contract == null) {
                contract = new WsContractDto();
                ServiceResult result = new ServiceResult(null, NotificationType.ERROR, "Invalid Json was provided");
                EndpointConnector.passResultToEndpoint("NotificationService", "sendMessage", result, this.getLoggedUser());
                return;
            }

//            SchemaValidator schemaValidator = new SchemaValidator();
//            for (ConversionObject conversionObject : contract.getArgs()) {
//                Class objectClass = Class.forName(conversionObject.getPackageName());
//                Object object = this.jsonConverter.fromJson(conversionObject.getObject(), objectClass);
//                String result = schemaValidator.validate(object);
//
//                if (!result.equals("OK")) {
//                    contract.setNotificationType(true);
//                    contract.setErrorMessage(result);
//                    remoteEndpoint.sendString(serializer.toJson(contract));
//                    return;
//                }
//            }

            User currentUser = (User) this.userService.getById(this.loggedUser.getId()).getObject();

            this.setLoggedUser(currentUser);
            this.loggedUser.getPermissions().addAll(this.permissionService.getUniqueUserGroupPermissions(currentUser));

            List<Permission> orderedPermissions = this.permissionService.sortPermissions(this.loggedUser.getPermissions());

            ServiceResult result;

            if (contract.getMethodName().equals("getAllPipelineHistoryDTOs") || contract.getMethodName().equals("getPipelineArtifactDTOs")) {
                result = (ServiceResult) this.wsObjectProcessor.call(contract);
                List<?> filteredEntities = this.securityServiceInvoker.filterEntities((List<?>) result.getObject(), contract.getClassName(), orderedPermissions, contract.getMethodName());
                contract.setResult(filteredEntities);
                contract.setNotificationType(result.getNotificationType());
                contract.setErrorMessage(result.getMessage());
                this.send(contract);
            } else if (contract.getClassName().equals("PipelineService") && contract.getMethodName().equals("getById")) {
                boolean hasPermission = this.securityServiceInvoker.process(contract.getArgs()[0].getObject(), contract.getClassName(), orderedPermissions, contract.getMethodName());
                if (hasPermission) {
                    result = (ServiceResult) this.wsObjectProcessor.call(contract);
                    contract.setResult(result.getObject());
                    contract.setNotificationType(result.getNotificationType());
                    contract.setErrorMessage(result.getMessage());
                } else {
                    contract.setResult(null);
                    contract.setNotificationType(NotificationType.ERROR);
                    contract.setErrorMessage("Unauthorized");
                }

                this.send(contract);
            } else if (contract.getMethodName().equals("getAll") || contract.getMethodName().equals("getAllPipelineGroupDTOs") || contract.getMethodName().equals("getAllUserGroups")) {
                result = (ServiceResult) this.wsObjectProcessor.call(contract);
                List<?> filteredEntities = this.securityServiceInvoker.filterEntities((List<?>) result.getObject(), contract.getClassName(), orderedPermissions, contract.getMethodName());
                contract.setResult(filteredEntities);
                contract.setNotificationType(result.getNotificationType());
                contract.setErrorMessage(result.getMessage());
                SessionPool.getInstance().sendToUserSessions(contract, this.getLoggedUser());
            } else {
                boolean hasPermission;
                if (contract.getMethodName().equals("changeUserPassword")) {
                    hasPermission = this.securityServiceInvoker.changeUserPassword(this.loggedUser.getEmail(), contract.getArgs()[0].getObject(), contract.getClassName(), orderedPermissions, contract.getMethodName());
                    if (hasPermission) {
                        result = (ServiceResult) this.wsObjectProcessor.call(contract);
                        contract.setResult(result.getObject());
                        contract.setNotificationType(result.getNotificationType());
                        contract.setErrorMessage(result.getMessage());
                        SessionPool.getInstance().sendToUserSessions(contract, this.getLoggedUser());
                    }
                } else {
                    hasPermission = this.securityServiceInvoker.process(contract.getArgs()[0].getObject(), contract.getClassName(), orderedPermissions, contract.getMethodName());
                    if (hasPermission) {
                        result = (ServiceResult) this.wsObjectProcessor.call(contract);
                        contract.setResult(result.getObject());
                        contract.setNotificationType(result.getNotificationType());
                        contract.setErrorMessage(result.getMessage());
                        if (result.getObject() == null) {
                            SessionPool.getInstance().sendToUserSessions(contract, this.loggedUser);
                        } else {
                            SessionPool.getInstance().sendToAuthorizedSessions(contract);
                        }
                    }
                }

                if (!hasPermission) {
                    contract.setResult(null);
                    contract.setNotificationType(NotificationType.ERROR);
                    contract.setErrorMessage("Unauthorized");
                    SessionPool.getInstance().sendToUserSessions(contract, this.getLoggedUser());
                }
            }
        } catch (RuntimeException e) {
            LOGGER.error(String.format(LoggerMessages.WSENDPOINT_ERROR, e));
            e.printStackTrace();
            this.errorDetails(contract, this.jsonConverter, e, remoteEndpoint);
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        //String message = String.format("Session closed - User: %s [%d] %s", this.loggedUser.getEmail(), statusCode, reason == null ? "" : reason);
        //LOGGER.info(message);
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
        contract.setNotificationType(NotificationType.ERROR);
        contract.setErrorMessage(e.getMessage());
        try {
            String errDetails = serializer.toJson(contract);
            endPoint.sendString(errDetails);
        } catch (IOException | RuntimeException e1) {
            e1.printStackTrace();
        }
    }
}