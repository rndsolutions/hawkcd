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

package io.hawkcd.utilities;

import io.hawkcd.core.security.Grant;
import io.hawkcd.model.PipelineGroup;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.User;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.services.PipelineGroupService;
import io.hawkcd.services.UserService;
import io.hawkcd.services.interfaces.IPipelineGroupService;
import io.hawkcd.services.interfaces.IUserService;

import java.util.ArrayList;
import java.util.List;

public class DataImporter {
    private IUserService userService;
    private IPipelineGroupService pipelineGroupService;

    public DataImporter() {
        this.userService = new UserService();
        this.pipelineGroupService = new PipelineGroupService();
    }

    public DataImporter(IUserService userService, IPipelineGroupService pipelineGroupService) {
        this.userService = userService;
        this.pipelineGroupService = pipelineGroupService;
    }

    public void importDefaultEntities() {
        this.addDefaultAdminUser();
        this.addDefualtPipelineGroup();
    }

    private ServiceResult addDefaultAdminUser() {
        User adminUser = new User();
        adminUser.setEmail("admin@admin.com");
        adminUser.setPassword("admin");
        Grant adminUserPermission = new Grant(PermissionScope.SERVER, PermissionType.ADMIN);
        adminUserPermission.setPermittedEntityId("SERVER");
//        adminUserPermission.setPermissionType(PermissionType.ADMIN);
//        adminUserPermission.setPermissionScope(PermissionScope.SERVER);
        List<Grant> permissions = new ArrayList<>();
        permissions.add(adminUserPermission);

        adminUser.setPermissions(permissions);

        return this.userService.add(adminUser);
    }

    private ServiceResult addDefualtPipelineGroup() {
        List<PipelineGroup> pipelineGroups = (List<PipelineGroup>) this.pipelineGroupService.getAll().getEntity();
        if (pipelineGroups.size() == 0) {

            PipelineGroup defaultPipelineGroup = new PipelineGroup();
            defaultPipelineGroup.setName("defaultPipelineGroup");

            return this.pipelineGroupService.add(defaultPipelineGroup);
        }

        return null;
    }
}
