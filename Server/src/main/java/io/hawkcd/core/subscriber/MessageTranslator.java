package io.hawkcd.core.subscriber;

import java.util.Map;

import io.hawkcd.core.Message;
import io.hawkcd.core.MessageConverter;
import io.hawkcd.model.Entity;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.PermissionType;

/**
 * Created by rado on 11.11.16.
 */
public class MessageTranslator{

    public static WsContractDto translateMessageToContract(Message message) {
        WsContractDto contract = MessageConverter.convert(message);
        Map<String, PermissionType> permissionTypeByUser = message.getPermissionTypeByUser();
        Entity entity = (Entity) message.getEnvelope();

        for (String userId : permissionTypeByUser.keySet()) {
            entity.setPermissionType(permissionTypeByUser.get(userId));
            contract.setResult(entity);
        }
        return contract;
    }
}
