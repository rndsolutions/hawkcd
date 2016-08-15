package net.hawkengine.ws;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.dto.WsContractDto;

public class EndpointConnector {
    public static void passResultToEndpoint(String className, String methodName, ServiceResult serviceResult) {
        WsContractDto contract = new WsContractDto();
        contract.setClassName(className);
        contract.setMethodName(methodName);
        contract.setResult(serviceResult.getObject());
        contract.setError(serviceResult.hasError());
        contract.setErrorMessage(serviceResult.getMessage());

        SessionPool.getInstance().sendToAuthorizedSessions(contract);
    }

    public static void passResultToEndpoint(String className, String methodName) {
        WsContractDto contract = new WsContractDto();
        contract.setClassName(className);
        contract.setMethodName(methodName);

        SessionPool.getInstance().sendToAuthorizedSessions(contract);
    }
}
