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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.hawkcd.utilities.deserializers.MaterialDefinitionAdapter;
import io.hawkcd.utilities.deserializers.TaskDefinitionAdapter;
import io.hawkcd.utilities.deserializers.WsContractDeserializer;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.PipelineGroup;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.payload.Permission;
import io.hawkcd.services.PipelineGroupService;
import io.hawkcd.services.filters.interfaces.IAuthorizationService;
import io.hawkcd.services.interfaces.IPipelineGroupService;

import java.util.ArrayList;
import java.util.List;

public class PipelineGroupAuthorizationService implements IAuthorizationService {
    private Gson jsonConverter;
    private IPipelineGroupService pipelineGroupService;
    private EntityPermissionTypeService entityPermissionTypeService;

    public PipelineGroupAuthorizationService() {
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        this.pipelineGroupService = new PipelineGroupService();
        this.entityPermissionTypeService = new EntityPermissionTypeService();
    }

    public PipelineGroupAuthorizationService(IPipelineGroupService pipelineGroupService) {
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        this.pipelineGroupService = pipelineGroupService;
        this.entityPermissionTypeService = new EntityPermissionTypeService();
    }


    @Override
    public List getAll(List permissions, List pipelineGroups) {
        List<PipelineGroup> result = new ArrayList<>();
        for (PipelineGroup pipelineGroup : (List<PipelineGroup>) pipelineGroups) {
            if (this.hasPermissionToRead(permissions, pipelineGroup)) {
                pipelineGroup = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineGroup);
                result.add(pipelineGroup);
            }
        }
        return result;
    }

    @Override
    public boolean getById(String entitId, List permissions) {
        PipelineGroup pipelineGroup = (PipelineGroup) this.pipelineGroupService.getById(entitId).getObject();
        pipelineGroup = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineGroup);

        return this.hasPermissionToRead(permissions, pipelineGroup);
    }

    @Override
    public boolean add(String entity, List permissions) {
        PipelineGroup pipelineGroup = this.jsonConverter.fromJson(entity, PipelineGroup.class);
        pipelineGroup = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineGroup);

        return this.hasPermissionToAdd(permissions, pipelineGroup.getId());
    }

    @Override
    public boolean update(String entity, List permissions) {
        PipelineGroup pipelineGroup = this.jsonConverter.fromJson(entity, PipelineGroup.class);
        pipelineGroup = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineGroup);

        return this.hasPermissionToUpdateAndDelete(permissions, pipelineGroup.getId());
    }

    @Override
    public boolean delete(String entityId, List permissions) {

        return this.hasPermissionToUpdateAndDelete(permissions, entityId);
    }

    private boolean hasPermissionToRead(List<Permission> permissions, PipelineGroup pipelineGroup) {
        String pipelineGroupId = pipelineGroup.getId();
        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                hasPermission = true;
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if ((permission.getPermissionType() == PermissionType.NONE)) {
                    hasPermission = false;
                } else {
                    hasPermission = true;
                }
            } else if (permission.getPermittedEntityId().equals(pipelineGroupId)) {
                if (permission.getPermissionType() == PermissionType.NONE) {
                    hasPermission = false;
                } else {
                    hasPermission = true;
                    return hasPermission;
                }
            }
        }
        return hasPermission;
    }

    private boolean hasPermissionToAdd(List<Permission> permissions, String pipelineGroupId) {
        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                hasPermission = true;
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if ((permission.getPermissionType() == PermissionType.ADMIN)) {
                    hasPermission = true;
                    return hasPermission;
                } else {
                    hasPermission = false;
                }
            }
        }
        return hasPermission;
    }

    private boolean hasPermissionToUpdateAndDelete(List<Permission> permissions, String pipelineGroupId) {
        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                hasPermission = true;
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if ((permission.getPermissionType() == PermissionType.ADMIN)) {
                    hasPermission = true;
                } else {
                    hasPermission = false;
                }
            } else if (permission.getPermittedEntityId().equals(pipelineGroupId)) {
                if (permission.getPermissionType() != PermissionType.ADMIN) {
                    hasPermission = false;
                } else {
                    hasPermission = true;
                    return hasPermission;
                }
            }
        }
        return hasPermission;
    }
}
