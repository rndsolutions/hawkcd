package net.hawkengine.ws;

import net.hawkengine.model.*;
import net.hawkengine.model.dto.UserDto;
import net.hawkengine.model.dto.UserGroupDto;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.EntityPermissionTypeService;

import java.util.List;

public class EntityPermissionTypeServiceInvoker {
    public static PermissionObject invoke(Class objectClass, List<Permission> permissions, PermissionObject object) {
        if (objectClass == PipelineGroup.class) {
            return EntityPermissionTypeService.setPermissionTypeToObject(permissions, (PipelineGroup) object);
        } else if (objectClass == PipelineDefinition.class) {
            return EntityPermissionTypeService.setPermissionTypeToObject(permissions, (PipelineDefinition) object);
        } else if (objectClass == Pipeline.class) {
            return EntityPermissionTypeService.setPermissionTypeToObject(permissions, (Pipeline) object);
        } else if (objectClass == StageDefinition.class) {
            return EntityPermissionTypeService.setPermissionTypeToObject(permissions, (StageDefinition) object);
        } else if (objectClass == JobDefinition.class) {
            return EntityPermissionTypeService.setPermissionTypeToObject(permissions, (JobDefinition) object);
        } else if (objectClass == TaskDefinition.class) {
            return EntityPermissionTypeService.setPermissionTypeToObject(permissions, (TaskDefinition) object);
        } else if (objectClass == UserDto.class) {
            return EntityPermissionTypeService.setPermissionTypeToObject(permissions, (UserDto) object);
        } else if (objectClass == UserGroupDto.class) {
            return EntityPermissionTypeService.setPermissionTypeToObject(permissions, (UserGroupDto) object);
        } else {
            return object;
        }
    }
}
