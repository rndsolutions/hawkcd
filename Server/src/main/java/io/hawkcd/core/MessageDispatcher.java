package io.hawkcd.core;

import io.hawkcd.core.config.Config;
import io.hawkcd.core.publisher.PublisherFactory;
import io.hawkcd.core.session.ISessionManager;
import io.hawkcd.core.session.SessionFactory;
import io.hawkcd.core.subscriber.MessageTranslator;
import io.hawkcd.model.Entity;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.ws.WSSocket;

import java.util.List;
import java.util.Map;

public class MessageDispatcher {

    public static void dispatchIncomingMessage(Message message) {
        if (Config.getConfiguration().isSingleNode()) {
            WsContractDto contractDto;
            if (message.isTargetOwner()) { // if this is list
                contractDto = MessageConverter.convert(message);
            } else { // if single message
                contractDto = MessageTranslator.translateMessageToContract(message);
            }
            SessionFactory.getSessionManager().sendToAllSessions(contractDto);
        } else {
            PublisherFactory.createPublisher().publish("global", message);
        }
    }
}
