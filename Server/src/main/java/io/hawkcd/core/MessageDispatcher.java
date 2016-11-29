package io.hawkcd.core;

import io.hawkcd.core.config.Config;
import io.hawkcd.core.publisher.PublisherFactory;
import io.hawkcd.core.session.SessionFactory;
import io.hawkcd.core.subscriber.MessageTranslator;
import io.hawkcd.model.dto.WsContractDto;

/**
 * Created by rado on 11.11.16.
 */
public class MessageDispatcher {

    public static void dispatchMessage(Message message) {
        if (Config.getConfiguration().isSingleNode()) {
            WsContractDto ctr = null;
            if(message.isTargetOwner()){ // if this is list
                ctr = MessageConverter.convert(message);
            }else { // if single message
                ctr = MessageTranslator.translateMessageToContract(message);
            }
            SessionFactory.getSessionManager().sendToAllSessions(ctr);
        } else {
            PublisherFactory.createPublisher().publish("global", message);
        }
    }
}
