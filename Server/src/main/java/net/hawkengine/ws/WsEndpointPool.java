package net.hawkengine.ws;

import net.hawkengine.model.User;
import net.hawkengine.model.dto.WsContractDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WsEndpointPool {
    private static WsEndpointPool instance;
    private List<WsEndpoint> wsEndpoints = new ArrayList<>();

    public static synchronized WsEndpointPool getInstance() {
        if (instance == null) {
            instance = new WsEndpointPool();
        }

        return instance;
    }

    public void add(WsEndpoint socket) {
        wsEndpoints.add(socket);
    }

    public void remove(WsEndpoint socket) {
        wsEndpoints.remove(socket);
    }

    public void sendToAuthorizedSessions(WsContractDto result, User user) {
        if (result.isError()) {
            List<WsEndpoint> userSessions = wsEndpoints
                    .stream()
                    .filter(e -> e.getLoggedUser().getId().equals(user.getId()))
                    .collect(Collectors.toList());
            for (WsEndpoint userSession : userSessions) {
                userSession.send(result);
            }
        } else {
            for (WsEndpoint wsEndpoint : wsEndpoints) {

            }
        }
    }
}