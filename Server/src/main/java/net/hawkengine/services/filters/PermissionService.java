package net.hawkengine.services.filters;

import net.hawkengine.model.User;
import net.hawkengine.model.UserGroup;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.UserGroupService;
import net.hawkengine.services.interfaces.IUserGroupService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionService {
    private IUserGroupService userGroupService;

    public PermissionService() {
        this.userGroupService = new UserGroupService();
    }

    public PermissionService(IUserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    public List<Permission> getUniqueUserGroupPermissions(User user) {
        List<Permission> userGroupPermissions = new ArrayList<>();
        String userId = user.getId();
        List<String> userGroupIds = user.getUserGroupIds();

        for (String userGroupId : userGroupIds) {
            UserGroup userGroup = (UserGroup) this.userGroupService.getById(userGroupId).getObject();
            List<String> userIds = userGroup.getUserIds();
            boolean isPresent = false;

            for (String userWithinGroupId : userIds) {
                if (userWithinGroupId.equals(userId)) {
                    isPresent = true;
                    break;
                }
            }
            if (isPresent) {
                List<Permission> userGroupPermissionsFromDb = userGroup.getPermissions();

                for (Permission userGroupPermissionFromDb : userGroupPermissionsFromDb) {
                    boolean isPermissionPresent = false;
                    for (Permission userPersmission : user.getPermissions()) {
                        if (userGroupPermissionFromDb.getPermittedEntityId().equals(userPersmission.getPermittedEntityId())) {
                            isPermissionPresent = true;
                            break;
                        }
                    }
                    if (!isPermissionPresent) {
                        userGroupPermissions = this.addPermissionToList(userGroupPermissions, userGroupPermissionFromDb);
                    }
                }
            }
        }

        return userGroupPermissions;
    }

    public List<Permission> sortPermissions(List<Permission> permissions) {
        List<Permission> sortedPermissions = new ArrayList<>();

        List<Permission> adminPermissions = permissions
                .stream()
                .filter(permission -> permission.getPermissionScope() == PermissionScope.SERVER)
                .sorted((p1, p2) -> p2.getPermissionType().compareTo(p1.getPermissionType()))
                .collect(Collectors.toList());
        List<Permission> pipelineGroupGlobalPermissions = permissions
                .stream()
                .filter(permission -> permission.getPermissionScope() == PermissionScope.PIPELINE_GROUP)
                .filter(permission -> permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString()))
                .sorted((p1, p2) -> p2.getPermissionType().compareTo(p1.getPermissionType()))
                .collect(Collectors.toList());
        List<Permission> pipelineGlobalPermissions = permissions
                .stream()
                .filter(permission -> permission.getPermissionScope() == PermissionScope.PIPELINE)
                .filter(permission -> permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()))
                .sorted((p1, p2) -> p2.getPermissionType().compareTo(p1.getPermissionType()))
                .collect(Collectors.toList());
        List<Permission> pipelineGroupPermissions = permissions
                .stream()
                .filter(permission -> permission.getPermissionScope() == PermissionScope.PIPELINE_GROUP)
                .filter(permission -> !permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString()))
                .sorted((p1, p2) -> p2.getPermissionType().compareTo(p1.getPermissionType()))
                .collect(Collectors.toList());
        List<Permission> pipelinePermissions = permissions
                .stream()
                .filter(permission -> permission.getPermissionScope() == PermissionScope.PIPELINE)
                .filter(permission -> !permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()))
                .sorted((p1, p2) -> p2.getPermissionType().compareTo(p1.getPermissionType()))
                .collect(Collectors.toList());

        sortedPermissions.addAll(adminPermissions);
        sortedPermissions.addAll(pipelineGroupGlobalPermissions);
        sortedPermissions.addAll(pipelineGlobalPermissions);
        sortedPermissions.addAll(pipelineGroupPermissions);
        sortedPermissions.addAll(pipelinePermissions);

        return sortedPermissions;
    }

    private List<Permission> addPermissionToList(List<Permission> permissions, Permission permissionToAdd) {
        List<Permission> equalPermissions = new ArrayList<>();
        equalPermissions.add(permissionToAdd);
        int index = 0;

        for (int i = 0; i < permissions.size(); i++) {
            Permission permission = permissions.get(i);
            if (permission.getPermittedEntityId().equals(permissionToAdd.getPermittedEntityId())) {
                equalPermissions.add(permission);
                index = i;
            }
        }
        if (equalPermissions.size() > 1) {
            Permission permissionWithPriority = equalPermissions.stream().sorted((p1, p2) -> p1.getPermissionType().compareTo(p2.getPermissionType())).findFirst().orElse(null);
            permissions.set(index, permissionWithPriority);

            return permissions;
        }
        permissions.add(permissionToAdd);

        return permissions;
    }
}
