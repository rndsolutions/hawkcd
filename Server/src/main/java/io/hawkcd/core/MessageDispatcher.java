package io.hawkcd.core;

import io.hawkcd.core.config.Config;
import io.hawkcd.core.publisher.PublisherFactory;
import io.hawkcd.core.session.SessionFactory;
import io.hawkcd.core.subscriber.MessageTranslator;
import io.hawkcd.model.dto.WsContractDto;

public class MessageDispatcher {

    public static void dispatchMessage(Message message) {
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
