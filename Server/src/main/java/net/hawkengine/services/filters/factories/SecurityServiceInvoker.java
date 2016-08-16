package net.hawkengine.services.filters.factories;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.SecurityService;
import net.hawkengine.services.filters.interfaces.ISecurityService;

import java.util.List;

public class SecurityServiceInvoker<T extends DbEntry> {
    private ISecurityService securityService;

    public SecurityServiceInvoker(){
        this.securityService = new SecurityService();
    }

    public SecurityServiceInvoker(ISecurityService securityService){
        this.securityService = securityService;
    }
    public boolean process(String entity, String className, List<Permission> permissions, String methodName) {
        switch (methodName) {
            case "getById":
                return this.securityService.getById(entity, className, permissions);
            case "add":
                return this.securityService.add(entity, className, permissions);
            case "update":
                return this.securityService.update(entity, className, permissions);
            case "delete":
                return this.securityService.delete(entity, className, permissions);
            case "assignUserToGroup":
                return this.securityService.assignUserToGroup(entity, className, permissions);
            case "unassignUserFromGroup":
                return this.securityService.unassignUserFromGroup(entity, className, permissions);
            case "assignPipelineToGroup":
                return this.securityService.assignPipelineToGroup(entity, className, permissions);
            case "unassignPipelineFromGroup":
                return this.securityService.unassignPipelineFromGroup(entity, className, permissions);
            case "addUserWithoutProvider":
                return this.securityService.addUserWithoutProvider(entity, className, permissions);
            case "addUserGroupDto":
                return this.securityService.addUserGroupDto(entity, className, permissions);
            case "updateUserGroupDto":
                return this.securityService.updateUserGroupDto(contract, permissions);
            case "addWithMaterialDefinition":
                return this.securityService.addWithMaterialDefinition(contract, permissions);
            default:
                return false;
        }
    }

    public List<T> filterEntities(List<T> entitiesToFilter, String className, List<Permission> permissions, String methodName) {
        ServiceResult serviceResult = new ServiceResult();
        switch (methodName) {
            case "getAll":
                return this.securityService.getAll(entitiesToFilter, className, permissions);
            case "getAllPipelineGroupDTOs":
                return this.securityService.getPipelineDTOs(entitiesToFilter, className, permissions);
            case "getAllUserGroups":
                return this.securityService.getAllUserGroups(entitiesToFilter, className, permissions);
            default:
                return null;
        }
    }
}
