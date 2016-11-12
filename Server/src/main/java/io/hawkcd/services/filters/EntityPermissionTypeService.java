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

import io.hawkcd.model.JobDefinition;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.Pipeline;
import io.hawkcd.model.PipelineDefinition;
import io.hawkcd.model.PipelineGroup;
import io.hawkcd.model.StageDefinition;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.model.User;
import io.hawkcd.model.UserGroup;
import io.hawkcd.model.dto.UserDto;
import io.hawkcd.model.dto.UserGroupDto;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.payload.Permission;
import io.hawkcd.services.PipelineDefinitionService;
import io.hawkcd.services.interfaces.IPipelineDefinitionService;

import java.util.List;

public class EntityPermissionTypeService {
    private IPipelineDefinitionService pipelineDefinitionService;

    public EntityPermissionTypeService(IPipelineDefinitionService pipelineDefinitionService) {
        this.pipelineDefinitionService = pipelineDefinitionService;
    }

    public EntityPermissionTypeService() {
        this.pipelineDefinitionService = new PipelineDefinitionService();
    }

    public PipelineGroup setPermissionTypeToObject(List<Permission> permissions, PipelineGroup pipelineGroup) {
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

    public PipelineDefinition setPermissionTypeToObject(List<Permission> permissions, PipelineDefinition pipelineDefinition) {
        pipelineDefinition.setPermissionType(PermissionType.NONE);
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                pipelineDefinition.setPermissionType(permission.getPermissionType());
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()) || permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                pipelineDefinition.setPermissionType(permission.getPermissionType());
            } else if (permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId())) {
                pipelineDefinition.setPermissionType(permission.getPermissionType());
            } else if ((permission.getPermissionScope() == PermissionScope.PIPELINE_GROUP) && permission.getPermissionType() == PermissionType.ADMIN) {
                pipelineDefinition.setPermissionType(permission.getPermissionType());
            }
            if (permission.getPermittedEntityId().equals(pipelineDefinition.getId())) {
                pipelineDefinition.setPermissionType(permission.getPermissionType());
            }
        }

        return pipelineDefinition;
    }

    public Pipeline setPermissionTypeToObject(List<Permission> permissions, Pipeline pipeline) {
        pipeline.setPermissionType(PermissionType.NONE);
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        if (pipelineDefinition == null) {
            return pipeline;
        }

        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                pipeline.setPermissionType(permission.getPermissionType());
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()) || permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                pipeline.setPermissionType(permission.getPermissionType());
            } else if (pipelineDefinition.getPipelineGroupId() != null) {
                if (permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId())) {
                    pipeline.setPermissionType(permission.getPermissionType());
                }
            }
            if (permission.getPermittedEntityId().equals(pipelineDefinition.getId())) {
                pipeline.setPermissionType(permission.getPermissionType());
            }
        }

        return pipeline;
    }

    public StageDefinition setPermissionTypeToObject(List<Permission> permissions, StageDefinition stageDefinition) {
        stageDefinition.setPermissionType(PermissionType.NONE);
        PipelineDefinition pipelineDefinition = (PipelineDefinition) pipelineDefinitionService.getById(stageDefinition.getPipelineDefinitionId()).getObject();
        if (pipelineDefinition == null) {
            return stageDefinition;
        }

        PipelineDefinition updatedPipelineDefinition = setPermissionTypeToObject(permissions, pipelineDefinition);
        stageDefinition.setPermissionType(updatedPipelineDefinition.getPermissionType());

        return stageDefinition;
    }

    public JobDefinition setPermissionTypeToObject(List<Permission> permissions, JobDefinition jobDefinition) {
        jobDefinition.setPermissionType(PermissionType.NONE);
        PipelineDefinition pipelineDefinition = (PipelineDefinition) pipelineDefinitionService.getById(jobDefinition.getPipelineDefinitionId()).getObject();
        if (pipelineDefinition == null) {
            return jobDefinition;
        }

        PipelineDefinition updatedPipelineDefinition = setPermissionTypeToObject(permissions, pipelineDefinition);
        jobDefinition.setPermissionType(updatedPipelineDefinition.getPermissionType());

        return jobDefinition;
    }

    public TaskDefinition setPermissionTypeToObject(List<Permission> permissions, TaskDefinition taskDefinition) {
        taskDefinition.setPermissionType(PermissionType.NONE);
        PipelineDefinition pipelineDefinition = (PipelineDefinition) pipelineDefinitionService.getById(taskDefinition.getPipelineDefinitionId()).getObject();
        if (pipelineDefinition == null) {
            return taskDefinition;
        }

        PipelineDefinition updatedPipelineDefinition = setPermissionTypeToObject(permissions, pipelineDefinition);
        taskDefinition.setPermissionType(updatedPipelineDefinition.getPermissionType());

        return taskDefinition;
    }

    public UserDto setPermissionTypeToObject(List<Permission> permissions, UserDto userDto) {
        userDto.setPermissionType(PermissionType.NONE);

        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                userDto.setPermissionType(PermissionType.ADMIN);
            }

        }

        return userDto;
    }

    public UserGroupDto setPermissionTypeToObject(List<Permission> permissions, UserGroupDto userGroupDto) {
        userGroupDto.setPermissionType(PermissionType.NONE);

        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                userGroupDto.setPermissionType(permission.getPermissionType());
            }
        }
        return userGroupDto;
    }

    public User setPermissionTypeToObject(List<Permission> permissions, User user) {
        user.setPermissionType(PermissionType.NONE);

        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                user.setPermissionType(permission.getPermissionType());
            }
        }

        return user;
    }

    public UserGroup setPermissionTypeToObject(List<Permission> permissions, UserGroup userGroup) {
        userGroup.setPermissionType(PermissionType.NONE);

        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                userGroup.setPermissionType(PermissionType.ADMIN);
            }
        }

        return userGroup;
    }

    public MaterialDefinition setPermissionTypeToObject(List<Permission> permissions, MaterialDefinition materialDefinition) {
        materialDefinition.setPermissionType(PermissionType.ADMIN);
        return materialDefinition;
    }
}
