/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hawkcd.services.filters;

import io.hawkcd.model.User;
import io.hawkcd.model.UserGroup;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.payload.Permission;
import io.hawkcd.services.UserGroupService;
import io.hawkcd.services.interfaces.IUserGroupService;

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
