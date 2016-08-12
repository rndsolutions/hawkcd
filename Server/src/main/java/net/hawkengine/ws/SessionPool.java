package net.hawkengine.ws;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.User;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionPool {
    private static SessionPool instance;

    private List<WsEndpoint> sessions;

    private SessionPool() {
        this.sessions = new ArrayList<>();
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
        Class objectClass = contractDto.getResult().getClass();
        for (WsEndpoint session : sessions) {
            List<Permission> permissions = session.getLoggedUser().getPermissions();
            DbEntry result = EntityPermissionTypeServiceInvoker.invoke(objectClass, permissions, (DbEntry) contractDto.getResult());
            if (result.getPermissionType() != PermissionType.NONE) {
                contractDto.setResult(result);
                session.send(contractDto);
            }
        }
    }

    public void logoutUserFromAllSessions(User user){
        List<WsEndpoint> userSessions = sessions
                .stream()
                .filter(e -> e.getLoggedUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
        for (WsEndpoint userSession : userSessions) {
            userSession.getSession().close();

        }
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