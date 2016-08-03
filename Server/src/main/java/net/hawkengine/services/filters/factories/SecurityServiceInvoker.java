package net.hawkengine.services.filters.factories;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.SecurityService;
import net.hawkengine.services.filters.interfaces.ISecurityService;

import java.util.List;

public class SecurityServiceInvoker {
    private ISecurityService securityService;

    public SecurityServiceInvoker(){
        this.securityService = new SecurityService();
    }

    public SecurityServiceInvoker(ISecurityService securityService){
        this.securityService = securityService;
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
            case "addUserToGroup":
                return this.securityService.addUserToGroup(contract, permissions);
            case "removeUserFromGroup":
                return this.securityService.removeUserFromGroup(contract, permissions);
            case "getAllUserGroups":
                return this.securityService.getAllUserGroups(contract, permissions);
            case "assignPipeleineToGroup":
                return this.securityService.assignPipelineToGroup(contract, permissions);
            case "unassignPipelineFromGroup":
                return this.securityService.unassignPipelineFromGroup(contract, permissions);
            default:
                return null;
        }
    }
}
