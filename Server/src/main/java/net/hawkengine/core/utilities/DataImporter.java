package net.hawkengine.core.utilities;

import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.PipelineGroupService;
import net.hawkengine.services.UserService;
import net.hawkengine.services.interfaces.IPipelineGroupService;
import net.hawkengine.services.interfaces.IUserService;

import java.util.ArrayList;
import java.util.List;

public class DataImporter {
    private IUserService userService;
    private IPipelineGroupService pipelineGroupService;

    public DataImporter(){
        this.userService = new UserService();
        this.pipelineGroupService = new PipelineGroupService();
    }

    public DataImporter(IUserService userService, IPipelineGroupService pipelineGroupService){
        this.userService = userService;
        this.pipelineGroupService = pipelineGroupService;
    }

    public void importDefaultEntities(){
        this.addDefaultAdminUser();
        this.addDefualtPipelineGroup();
    }

    private ServiceResult addDefaultAdminUser(){
        User adminUser = new User();
        adminUser.setEmail("admin@admin.com");
        adminUser.setPassword("admin");
        Permission adminUserPermission = new Permission();
        adminUserPermission.setPermittedEntityId("SERVER");
        adminUserPermission.setPermissionType(PermissionType.ADMIN);
        adminUserPermission.setPermissionScope(PermissionScope.SERVER);
        List<Permission> permissions = new ArrayList<>();
        permissions.add(adminUserPermission);

        adminUser.setPermissions(permissions);

        return this.userService.add(adminUser);
    }

    private ServiceResult addDefualtPipelineGroup(){
        List<PipelineGroup> pipelineGroups = (List<PipelineGroup>) this.pipelineGroupService.getAll().getObject();
        if (pipelineGroups.size() == 0){

            PipelineGroup defaultPipelineGroup = new PipelineGroup();
            defaultPipelineGroup.setName("defaultPipelineGroup");

            return this.pipelineGroupService.add(defaultPipelineGroup);
        }

        return null;
    }
}
