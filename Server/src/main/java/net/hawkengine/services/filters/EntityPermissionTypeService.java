package net.hawkengine.services.filters;

import net.hawkengine.model.*;
import net.hawkengine.model.dto.UserDto;
import net.hawkengine.model.dto.UserGroupDto;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;

import java.util.List;

public class EntityPermissionTypeService {
    private IPipelineDefinitionService pipelineDefinitionService;

    public EntityPermissionTypeService(IPipelineDefinitionService pipelineDefinitionService) {
        this.pipelineDefinitionService = pipelineDefinitionService;
    }

    public EntityPermissionTypeService(){
        this.pipelineDefinitionService = new PipelineDefinitionService();
    }

    public PipelineGroup setPermissionTypeToObject(List<Permission> permissions, PipelineGroup pipelineGroup){
        pipelineGroup.setPermissionType(PermissionType.NONE);
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

    public PipelineDefinition setPermissionTypeToObject(List<Permission> permissions, PipelineDefinition pipelineDefinition){
        pipelineDefinition.setPermissionType(PermissionType.NONE);
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

    public Pipeline setPermissionTypeToObject(List<Permission> permissions, Pipeline pipeline){
        pipeline.setPermissionType(PermissionType.NONE);
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
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

    public StageDefinition setPermissionTypeToObject(List<Permission> permissions, StageDefinition stageDefinition){
        stageDefinition.setPermissionType(PermissionType.NONE);
        PipelineDefinition pipelineDefinition = (PipelineDefinition) pipelineDefinitionService.getById(stageDefinition.getPipelineDefinitionId()).getObject();
        if (pipelineDefinition == null) {
            return stageDefinition;
        }

        PipelineDefinition updatedPipelineDefinition = setPermissionTypeToObject(permissions, pipelineDefinition);
        stageDefinition.setPermissionType(updatedPipelineDefinition.getPermissionType());

        return stageDefinition;
    }

    public JobDefinition setPermissionTypeToObject(List<Permission> permissions, JobDefinition jobDefinition){
        jobDefinition.setPermissionType(PermissionType.NONE);
        PipelineDefinition pipelineDefinition = (PipelineDefinition) pipelineDefinitionService.getById(jobDefinition.getPipelineDefinitionId()).getObject();
        if (pipelineDefinition == null) {
            return jobDefinition;
        }

        PipelineDefinition updatedPipelineDefinition = setPermissionTypeToObject(permissions, pipelineDefinition);
        jobDefinition.setPermissionType(updatedPipelineDefinition.getPermissionType());

        return jobDefinition;
    }

    public TaskDefinition setPermissionTypeToObject(List<Permission> permissions, TaskDefinition taskDefinition){
        taskDefinition.setPermissionType(PermissionType.NONE);
        PipelineDefinition pipelineDefinition = (PipelineDefinition) pipelineDefinitionService.getById(taskDefinition.getPipelineDefinitionId()).getObject();
        if (pipelineDefinition == null) {
            return taskDefinition;
        }

        PipelineDefinition updatedPipelineDefinition = setPermissionTypeToObject(permissions, pipelineDefinition);
        taskDefinition.setPermissionType(updatedPipelineDefinition.getPermissionType());

        return taskDefinition;
    }

    public UserDto setPermissionTypeToObject(List<Permission> permissions, UserDto userDto){
        userDto.setPermissionType(PermissionType.NONE);

        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                userDto.setPermissionType(PermissionType.ADMIN);
            }

        }

        return userDto;
    }

    public UserGroupDto setPermissionTypeToObject(List<Permission> permissions, UserGroupDto userGroupDto){
        userGroupDto.setPermissionType(PermissionType.NONE);

        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                userGroupDto.setPermissionType(PermissionType.ADMIN);
            }

            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                    userGroupDto.setPermissionType(permission.getPermissionType());
            }
        }
        return userGroupDto;
    }

    public User setPermissionTypeToObject(List<Permission> permissions, User user){
        user.setPermissionType(PermissionType.NONE);

        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                user.setPermissionType(PermissionType.ADMIN);
            }
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                user.setPermissionType(permission.getPermissionType());
            }
            }

        return user;
    }

    public MaterialDefinition setPermissionTypeToObject(List<Permission> permissions, MaterialDefinition materialDefinition){
        materialDefinition.setPermissionType(PermissionType.ADMIN);
        return materialDefinition;
    }
}
