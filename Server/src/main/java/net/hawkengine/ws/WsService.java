package net.hawkengine.ws;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.EntityPermissionTypeService;

import java.util.List;

public class WsService {
    public static DbEntry invokeEntityPermissionTypeService(Class objectClass, List<Permission> permissions, DbEntry object) {
        if (objectClass == PipelineGroup.class) {
            return EntityPermissionTypeService.setPermissionTypeToObject(permissions, (PipelineGroup) object);
        } else if (objectClass == PipelineDefinition.class) {
            return EntityPermissionTypeService.setPermissionTypeToObject(permissions, (PipelineDefinition) object);
        } else if (objectClass == Pipeline.class) {
            return EntityPermissionTypeService.setPermissionTypeToObject(permissions, (Pipeline) object);
        } else {
            return object;
        }
    }
}
