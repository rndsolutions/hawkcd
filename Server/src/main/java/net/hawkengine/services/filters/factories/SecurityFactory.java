package net.hawkengine.services.filters.factories;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.SecurityService;

import java.util.List;

public class SecurityFactory {
    public ServiceResult process(WsContractDto contract, List<Permission> permissions) {
        switch (contract.getMethodName()) {
            case "getAll":
                return new SecurityService().getAll(contract, permissions);
            case "getById":
                return new SecurityService().getById(contract, permissions);
            case "add":
                return new SecurityService().add(contract, permissions);
            case "upload":
                return new SecurityService().update(contract, permissions);
            case "delete":
                return new SecurityService().delete(contract, permissions);
            default:
                return null;
        }
    }
}
