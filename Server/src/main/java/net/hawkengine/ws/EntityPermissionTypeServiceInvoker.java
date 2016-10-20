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

package net.hawkengine.ws;

import net.hawkengine.model.*;
import net.hawkengine.model.dto.UserDto;
import net.hawkengine.model.dto.UserGroupDto;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.EntityPermissionTypeService;

import java.util.List;

public class EntityPermissionTypeServiceInvoker {
    EntityPermissionTypeService entityPermissionTypeService = new EntityPermissionTypeService();

    public PermissionObject invoke(Class objectClass, List<Permission> permissions, PermissionObject object) {
        if (objectClass == PipelineGroup.class) {
            return this.entityPermissionTypeService.setPermissionTypeToObject(permissions, (PipelineGroup) object);
        } else if (objectClass == PipelineDefinition.class) {
            return this.entityPermissionTypeService.setPermissionTypeToObject(permissions, (PipelineDefinition) object);
        } else if (objectClass == Pipeline.class) {
            return this.entityPermissionTypeService.setPermissionTypeToObject(permissions, (Pipeline) object);
        } else if (objectClass == StageDefinition.class) {
            return this.entityPermissionTypeService.setPermissionTypeToObject(permissions, (StageDefinition) object);
        } else if (objectClass == JobDefinition.class) {
            return this.entityPermissionTypeService.setPermissionTypeToObject(permissions, (JobDefinition) object);
        } else if (objectClass == TaskDefinition.class) {
            return this.entityPermissionTypeService.setPermissionTypeToObject(permissions, (TaskDefinition) object);
        } else if (objectClass == UserDto.class) {
            return this.entityPermissionTypeService.setPermissionTypeToObject(permissions, (UserDto) object);
        } else if (objectClass == UserGroupDto.class) {
            return this.entityPermissionTypeService.setPermissionTypeToObject(permissions, (UserGroupDto) object);
        } else if (objectClass == User.class) {
            return this.entityPermissionTypeService.setPermissionTypeToObject(permissions, (User) object);
        } else if (objectClass == UserGroup.class) {
            return this.entityPermissionTypeService.setPermissionTypeToObject(permissions, (UserGroup) object);
        } else if (objectClass == MaterialDefinition.class) {
            return this.entityPermissionTypeService.setPermissionTypeToObject(permissions, (MaterialDefinition) object);
        } else {
            return object;
        }
    }
}
