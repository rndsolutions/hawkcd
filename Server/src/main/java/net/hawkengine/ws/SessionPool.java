package net.hawkengine.ws;

import net.hawkengine.model.User;
import net.hawkengine.model.dto.WsContractDto;

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

    public void add(WsEndpoint session) {
        sessions.add(session);
    }

    public void remove(WsEndpoint session) {
        sessions.remove(session);
    }

    public void sendToAuthorizedSessions(WsContractDto contractDto, User user) {
        if (contractDto.isError()) {
            List<WsEndpoint> userSessions = sessions
                    .stream()
                    .filter(e -> e.getLoggedUser().getId().equals(user.getId()))
                    .collect(Collectors.toList());
            for (WsEndpoint userSession : userSessions) {
                userSession.send(contractDto);
            }
        } else {
            
        }
    }
}