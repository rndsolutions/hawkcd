package net.hawkengine.ws;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.dto.WsContractDto;

public class EndpointConnector {
    public static void passResultToEndpoint(String className, String methodName, ServiceResult serviceResult) {
        WsContractDto contract = new WsContractDto();
        contract.setClassName(className);
        contract.setMethodName(methodName);
        contract.setResult(serviceResult.getObject());
        contract.setNotificationType(serviceResult.getNotificationType());
        contract.setErrorMessage(serviceResult.getMessage());

        SessionPool.getInstance().sendToAuthorizedSessions(contract);
    }

    public static void passResultToEndpoint(String className, String methodName, ServiceResult serviceResult, User user) {
        WsContractDto contract = new WsContractDto();
        contract.setClassName(className);
        contract.setMethodName(methodName);
        contract.setResult(serviceResult.getObject());
        contract.setNotificationType(serviceResult.getNotificationType());
        contract.setErrorMessage(serviceResult.getMessage());

        SessionPool.getInstance().sendToUserSessions(contract, user);
    }

    public static void passResultToEndpoint(String className, String methodName, Object obj) {
        WsContractDto contract = new WsContractDto();
        contract.setClassName(className);
        contract.setMethodName(methodName);
        contract.setResult(obj);
        //contract.setNotificationType(serviceResult.getNotificationType());
        //contract.setErrorMessage(serviceResult.getMessage());

        SessionPool.getInstance().sendToAuthorizedSessions(contract);
    }

}
