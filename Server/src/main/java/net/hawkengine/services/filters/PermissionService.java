package net.hawkengine.services.filters;

import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.User;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.UserService;
import net.hawkengine.services.filters.interfaces.IPermissionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionService implements IPermissionService {
    private IPipelineDefinitionService pipelineDefinitionService;
    private IUserService userService;

    public PermissionService() {
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.userService = new UserService();
    }

    @Override
    public List<Permission> distributePipelineGroupPermissions(Permission permission, User user) {
        List<Permission> permissionsToBeAdded = new ArrayList<>();
        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();

        List<PipelineDefinition> pipelineDefinitionsInCurrentGroup = pipelineDefinitions.stream().filter(p -> p.getPipelineGroupId().equals(permission.getPermittedEntityId())).collect(Collectors.toList());
        for (PipelineDefinition pipelineDefinition: pipelineDefinitionsInCurrentGroup) {
            if (pipelineDefinition.getPipelineGroupId().equals(permission.getPermittedEntityId())){
                Permission permissionToBeAdded = new Permission();
                permissionToBeAdded.setAbleToGet(permission.isAbleToGet());
                permissionToBeAdded.setAbleToAdd(permission.isAbleToAdd());
                permissionToBeAdded.setAbleToUpdate(permission.isAbleToUpdate());
                permissionToBeAdded.setAbleToDelete(permission.isAbleToDelete());
                permissionToBeAdded.setPermissionScope(PermissionScope.PIPELINE);
                permissionToBeAdded.setPermissionType(permission.getPermissionType());
                permissionToBeAdded.setPermittedEntityId(pipelineDefinition.getId());

                permissionsToBeAdded.add(permissionToBeAdded);
            }
        }
        user.getPermissions().addAll(permissionsToBeAdded);
        this.userService.update(user);

        return permissionsToBeAdded;
    }

    @Override
    public List<Permission> distributeServerPermissions(Permission permission, User user) {
        return null;
    }

    @Override
    public List<Permission> distributePipelinePermissions(Permission permission, User user) {
        return null;
    }

    @Override
    public List<Permission> distributeEnvironmentPermissions(Permission permission, User user) {
        return null;
    }
}
