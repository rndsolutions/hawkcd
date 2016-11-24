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
import io.hawkcd.model.Entity;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.PipelineDefinition;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.payload.Permission;
import io.hawkcd.services.PipelineDefinitionService;
import io.hawkcd.services.filters.interfaces.IAuthorizationService;
import io.hawkcd.services.interfaces.IPipelineDefinitionService;

import java.util.ArrayList;
import java.util.List;

public class PipelineDefinitionAuthorizationService implements IAuthorizationService {
    private IPipelineDefinitionService pipelineDefinitionService;
    private Gson jsonConverter;
    private EntityPermissionTypeService entityPermissionTypeService;

    public PipelineDefinitionAuthorizationService() {
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        this.entityPermissionTypeService = new EntityPermissionTypeService();
    }

    public PipelineDefinitionAuthorizationService(IPipelineDefinitionService pipelineDefinitionService) {
        this.pipelineDefinitionService = pipelineDefinitionService;
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        this.entityPermissionTypeService = new EntityPermissionTypeService(this.pipelineDefinitionService);
    }

    @Override
    public List<Entity> getAll(List permissions, List pipelineDefinitions) {
        List<Entity> result = new ArrayList<>();
        for (PipelineDefinition pipelineDefinition : (List<PipelineDefinition>) pipelineDefinitions) {
            if (this.hasPermissionToRead(permissions, pipelineDefinition)) {
                pipelineDefinition = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineDefinition);
                result.add(pipelineDefinition);
            }
        }
        return result;
    }

    @Override
    public boolean getById(String entityId, List permissions) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(entityId).getObject();
        pipelineDefinition = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineDefinition);
        return this.hasPermissionToRead(permissions, pipelineDefinition);
    }

    @Override
    public boolean delete(String entityId, List permissions) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(entityId).getObject();

        return this.hasPermissionToUpdateAndDelete(permissions, pipelineDefinition);
    }

    @Override
    public boolean update(String entity, List permissions) {
        PipelineDefinition pipelineDefinition = this.jsonConverter.fromJson(entity, PipelineDefinition.class);
        pipelineDefinition = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineDefinition);

        return this.hasPermissionToUpdateAndDelete(permissions, pipelineDefinition);
    }

    @Override
    public boolean add(String entity, List permissions) {
        PipelineDefinition pipelineDefinition = this.jsonConverter.fromJson(entity, PipelineDefinition.class);
        pipelineDefinition = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineDefinition);

        return this.hasPermissionToAdd(permissions, pipelineDefinition);
    }

    public boolean assignUnassign(List<Permission> permissions) {
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasPermissionToRead(List<Permission> permissions, PipelineDefinition pipelineDefinition) {
        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                hasPermission = true;
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()) || permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if ((permission.getPermissionType() != PermissionType.NONE)) {
                    hasPermission = true;
                } else {
                    hasPermission = false;
                }
            } else if (permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId())) {
                if (permission.getPermissionType() == PermissionType.NONE) {
                    hasPermission = false;
                } else {
                    hasPermission = true;
                }
            }
            if (permission.getPermittedEntityId().equals(pipelineDefinition.getId())) {
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

    private boolean hasPermissionToAdd(List<Permission> permissions, PipelineDefinition pipelineDefinition) {
        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                hasPermission = true;
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()) || permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if ((permission.getPermissionType() == PermissionType.ADMIN)) {
                    hasPermission = true;
                } else {
                    hasPermission = false;
                }
            } else if (pipelineDefinition.getPipelineGroupId() != null) {
                if (permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId())) {
                    if (permission.getPermissionType() == PermissionType.ADMIN) {
                        hasPermission = true;
                    } else {
                        hasPermission = false;
                    }
                }
            }
            if (permission.getPermittedEntityId().equals(pipelineDefinition.getId())) {
                if (permission.getPermissionType() == PermissionType.ADMIN) {
                    hasPermission = true;
                    return hasPermission;
                } else {
                    hasPermission = false;
                }
            }
        }
        return hasPermission;
    }

    private boolean hasPermissionToUpdateAndDelete(List<Permission> permissions, PipelineDefinition pipelineDefinition) {
        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                hasPermission = true;
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()) || permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if ((permission.getPermissionType() == PermissionType.ADMIN)) {
                    hasPermission = true;
                } else {
                    hasPermission = false;
                }
            } else if (pipelineDefinition.getPipelineGroupId() != null) {
                if (permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId())) {
                    if (permission.getPermissionType() == PermissionType.ADMIN) {
                        hasPermission = true;
                    } else {
                        hasPermission = false;
                    }
                }
            }
            if (permission.getPermittedEntityId().equals(pipelineDefinition.getId())) {
                if (permission.getPermissionType() == PermissionType.ADMIN) {
                    hasPermission = true;
                    return hasPermission;
                } else {
                    hasPermission = false;
                }
            }
        }
        return hasPermission;
    }
}
