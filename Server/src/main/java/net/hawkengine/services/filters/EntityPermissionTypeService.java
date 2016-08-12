package net.hawkengine.services.filters;

import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;

import java.util.List;

public class EntityPermissionTypeService {
    private static IPipelineDefinitionService pipelineDefinitionService = new PipelineDefinitionService();

    public EntityPermissionTypeService(IPipelineDefinitionService pipelineDefinitionService) {
        EntityPermissionTypeService.pipelineDefinitionService = pipelineDefinitionService;
    }

    public static PipelineGroup setPermissionTypeToObject(List<Permission> permissions, PipelineGroup pipelineGroup){
        String pipelineGroupId = pipelineGroup.getId();
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                pipelineGroup.setPermissionType(permission.getPermissionType());
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                pipelineGroup.setPermissionType(permission.getPermissionType());
            } else if (permission.getPermittedEntityId().equals(pipelineGroupId)) {
                pipelineGroup.setPermissionType(permission.getPermissionType());
            }
        }

        return pipelineGroup;
    }

    public static PipelineDefinition setPermissionTypeToObject(List<Permission> permissions, PipelineDefinition pipelineDefinition){
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                pipelineDefinition.setPermissionType(permission.getPermissionType());
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()) || permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                pipelineDefinition.setPermissionType(permission.getPermissionType());
            } else if (permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId())) {
                pipelineDefinition.setPermissionType(permission.getPermissionType());
            }
            if (permission.getPermittedEntityId().equals(pipelineDefinition.getId())) {
                pipelineDefinition.setPermissionType(permission.getPermissionType());
            }
        }

        return pipelineDefinition;
    }

    public static Pipeline setPermissionTypeToObject(List<Permission> permissions, Pipeline pipeline){
        PipelineDefinition pipelineDefinition = (PipelineDefinition) pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        if (pipelineDefinition == null) {
            return pipeline;
        }

        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)){
                pipeline.setPermissionType(permission.getPermissionType());
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()) || permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                pipeline.setPermissionType(permission.getPermissionType());
            } else if (pipelineDefinition.getPipelineGroupId() != null) {
                if (permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId())) {
                    pipeline.setPermissionType(permission.getPermissionType());
                }
            }
            if (permission.getPermittedEntityId().equals(pipelineDefinition.getId())){
                pipeline.setPermissionType(permission.getPermissionType());
            }
        }

        return pipeline;
    }
}
