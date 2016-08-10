package net.hawkengine.services.filters;

import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;

import java.util.List;

public class EntityPermissionTypeService {

    public static PipelineGroup setPermissionTypeToPipelineGroup(List<Permission> permissions, PipelineGroup pipelineGroup){

        String pipelineGroupId = pipelineGroup.getId();
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                pipelineGroup.setPermissionType(PermissionType.ADMIN);
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                pipelineGroup.setPermissionType(permission.getPermissionType());
            } else if (permission.getPermittedEntityId().equals(pipelineGroupId)) {
                pipelineGroup.setPermissionType(permission.getPermissionType());

                return pipelineGroup;
            }
        }

        return pipelineGroup;
    }

    public static PipelineDefinition setPermissionTypeToPipelineDefinition(List<Permission> permissions, PipelineDefinition pipelineDefinition){
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

                return pipelineDefinition;
            }
        }

        return pipelineDefinition;
    }

    public static Pipeline setPermissionTypeToPipeline(List<Permission> permissions, Pipeline pipeline, PipelineDefinition pipelineDefinition){
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

                return pipeline;
            }
        }

        return pipeline;
    }
}
