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
        }else if (objectClass == UserGroup.class) {
            return this.entityPermissionTypeService.setPermissionTypeToObject(permissions, (UserGroup) object);
        } else if (objectClass == MaterialDefinition.class) {
            return this.entityPermissionTypeService.setPermissionTypeToObject(permissions, (MaterialDefinition) object);
        } else {
            return object;
        }
    }
}
