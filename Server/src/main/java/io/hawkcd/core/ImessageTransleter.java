package io.hawkcd.core;

import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.dto.WsContractDto;

/**
 * Created by Rado @radoslavMinchev, rminchev@rnd-solutions.net on 24.11.16.
 */
public interface ImessageTransleter {

    WsContractDto constructWSContract(ServiceResult sReslult);

    WsContractDto constructWSContract(Message message);

    Message constructMessage(ServiceResult sReslult);
}
