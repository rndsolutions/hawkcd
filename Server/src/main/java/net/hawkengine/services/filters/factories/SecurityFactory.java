package net.hawkengine.services.filters.factories;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.SecurityService;
import net.hawkengine.services.filters.interfaces.ISecurityService;

import java.util.List;

public class SecurityFactory {
    private ISecurityService securityService;

    public SecurityFactory(){
        this.securityService = new SecurityService();
    }
    public ServiceResult process(WsContractDto contract, List<Permission> permissions) {
        switch (contract.getMethodName()) {
            case "getAll":
                return this.securityService.getAll(contract, permissions);
            case "getAllPipelineGroupDTOs":
                return this.securityService.getPipelineDTOs(contract, permissions);
            case "getById":
                return this.securityService.getById(contract, permissions);
            case "add":
                return this.securityService.add(contract, permissions);
            case "update":
                return this.securityService.update(contract, permissions);
            case "delete":
                return this.securityService.delete(contract, permissions);
            default:
                return null;
        }
    }
}
