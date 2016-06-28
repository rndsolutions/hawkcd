package net.hawkengine.core.utilities;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.ws.WsEndpoint;

public class EndpointConnector {
    private static WsEndpoint wsEndpoint = new WsEndpoint();

    public static void setWsEndpoint(WsEndpoint wsEndpoint) {
        EndpointConnector.wsEndpoint = wsEndpoint;
    }

    public static void passResultToEndpoint(String className, String packageName, String methodName, ServiceResult serviceResult) {
        WsContractDto contract = new WsContractDto();
        contract.setClassName(className);
        contract.setPackageName(packageName);
        contract.setMethodName(methodName);
        contract.setResult(serviceResult.getObject());
        contract.setError(serviceResult.hasError());
        contract.setErrorMessage(serviceResult.getMessage());

        wsEndpoint.send(contract);
    }
}
