package io.hawkcd.core;

import io.hawkcd.core.config.Config;
import io.hawkcd.core.publisher.PublisherFactory;
import io.hawkcd.core.session.ISessionManager;
import io.hawkcd.core.session.SessionFactory;
import io.hawkcd.model.Entity;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.ws.WSSocket;

import java.util.List;
import java.util.Map;

public class MessageDispatcher {

    public static void dispatchIncomingMessage(Message message) {
        if (Config.getConfiguration().isSingleNode()) {
            WsContractDto contractDto = null;
            //TODO: Improve flow of dispatcher to not send to all Nodes, even if isSingleNode is true
            if (message.isUserUpdate()) {
                List<String> ids = (List<String>) message.getEnvelope();
                SessionFactory.getSessionManager().updateSessionLoggedUser(ids.toArray(new String[ids.size()]));
            } else {
                contractDto = MessageConverter.convert(message);
                SessionFactory.getSessionManager().sendToAllSessions(contractDto);
            }
        } else {
            PublisherFactory.createPublisher().publish("global", message);
        }
    }

    public static void dispatchOutgoingMessage(Message message) {
        ISessionManager sessionManager = SessionFactory.getSessionManager();

        if (message.isTargetOwner()) { // when is list and targets the user executed the request
            WsContractDto contract = MessageConverter.convert(message);

            WSSocket session = sessionManager.getSessionByUserId(message.getOwner().getId());
            sessionManager.send(session, contract);
        } else if (message.isUserUpdate()) { // when is message to update the logged users of sessions
            List<String> ids = (List<String>) message.getEnvelope();
            sessionManager.updateSessionLoggedUser(ids.toArray(new String[ids.size()]));
        } else { // when is single message meant to be broadcast
            Map<String, PermissionType> permissionTypeByUser = message.getPermissionTypeByUser();
            WsContractDto contract = MessageConverter.convert(message);

            for (Map.Entry<String, PermissionType> entry : permissionTypeByUser.entrySet()) {
                WSSocket session = sessionManager.getSessionByUserId(entry.getKey());
                if (session != null) {
                    ((Entity) contract.getResult()).setPermissionType(entry.getValue());
                    sessionManager.send(session, contract);
                }
            }
        }
    }
}
