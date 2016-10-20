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

package net.hawkengine.services.filters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import net.hawkengine.model.*;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;

import java.util.ArrayList;
import java.util.List;

public class PipelineAuthorizationService implements IAuthorizationService {
    private IPipelineDefinitionService pipelineDefinitionService;
    private IPipelineService pipelineService;
    private Gson jsonConverter;
    private EntityPermissionTypeService entityPermissionTypeService;

    public PipelineAuthorizationService() {
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.pipelineService = new PipelineService();
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        this.entityPermissionTypeService = new EntityPermissionTypeService();
    }

    public PipelineAuthorizationService(IPipelineService pipelineService, IPipelineDefinitionService pipelineDefinitionService) {
        this.pipelineService = pipelineService;
        this.pipelineDefinitionService = pipelineDefinitionService;
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        this.entityPermissionTypeService = new EntityPermissionTypeService(this.pipelineDefinitionService);
    }

    @Override
    public List getAll(List permissions, List pipelines) {
        List<DbEntry> result = new ArrayList<>();
        for (Pipeline pipeline : (List<Pipeline>) pipelines) {
            if (this.hasPermissionToRead(permissions, pipeline)) {
//                PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
                pipeline = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipeline);
                result.add(pipeline);
            }
        }
        return result;
    }

    @Override
    public boolean getById(String entityId, List permissions) {
        Pipeline pipeline = (Pipeline) this.pipelineService.getById(entityId).getObject();
//        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        pipeline = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipeline);

        return this.hasPermissionToRead(permissions, pipeline);
    }

    @Override
    public boolean add(String entity, List permissions) {
        Pipeline pipeline = this.jsonConverter.fromJson(entity, Pipeline.class);
//        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        pipeline = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipeline);

        return this.hasPermissionToAdd(permissions, pipeline);
    }

    @Override
    public boolean update(String entity, List permissions) {
        Pipeline pipeline = this.jsonConverter.fromJson(entity, Pipeline.class);
//        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        pipeline = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipeline);

        return this.hasPermissionToUpdateAndDelete(permissions, pipeline);
    }

    @Override
    public boolean delete(String entityId, List permissions) {
        Pipeline pipeline = (Pipeline) this.pipelineService.getById(entityId).getObject();

        return this.hasPermissionToUpdateAndDelete(permissions, pipeline);
    }

    public boolean hasPermissionToRead(List<Permission> permissions, Pipeline pipeline) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();

        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                hasPermission = true;
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()) || permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if (permission.getPermissionType() == PermissionType.NONE) {
                    hasPermission = false;
                } else {
                    hasPermission = true;
                }
            } else if (pipelineDefinition.getPipelineGroupId() != null) {
                if (permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId())) {
                    if (permission.getPermissionType() == PermissionType.NONE) {
                        hasPermission = false;
                    } else {
                        hasPermission = true;
                    }
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

    public boolean hasPermissionToAdd(List<Permission> permissions, Pipeline pipeline) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && ((permission.getPermissionType() == PermissionType.ADMIN) || (permission.getPermissionType() == PermissionType.OPERATOR))) {
                hasPermission = true;
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString())) {
                if ((permission.getPermissionType() == PermissionType.ADMIN) || (permission.getPermissionType() == PermissionType.OPERATOR)) {
                    hasPermission = true;
                } else {
                    hasPermission = false;
                }
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if ((permission.getPermissionType() == PermissionType.ADMIN) || (permission.getPermissionType() == PermissionType.OPERATOR)) {
                    hasPermission = true;
                } else {
                    hasPermission = false;
                }
            } else if (pipelineDefinition.getPipelineGroupId() != null) {
                if (permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId())) {
                    if ((permission.getPermissionType() == PermissionType.ADMIN) || (permission.getPermissionType() == PermissionType.OPERATOR)) {
                        hasPermission = true;
                    } else {
                        hasPermission = false;
                    }
                }
            }
            if (permission.getPermittedEntityId().equals(pipelineDefinition.getId())) {
                if ((permission.getPermissionType() == PermissionType.ADMIN) || (permission.getPermissionType() == PermissionType.OPERATOR)) {
                    hasPermission = true;
                    return hasPermission;
                } else {
                    hasPermission = false;
                }
            }
        }
        return hasPermission;
    }

    public boolean hasPermissionToUpdateAndDelete(List<Permission> permissions, Pipeline pipeline) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                hasPermission = true;
            }
            if ((permission.getPermissionScope() == PermissionScope.SERVER) || (permission.getPermissionScope() == PermissionScope.PIPELINE) || (permission.getPermissionScope() == PermissionScope.PIPELINE_GROUP)) {
                if (permission.getPermissionType() == PermissionType.OPERATOR) {
                    hasPermission = true;
                }
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()) || permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if (permission.getPermissionType() == PermissionType.ADMIN) {
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
                if ((permission.getPermissionType() == PermissionType.ADMIN) || (permission.getPermissionType() == PermissionType.OPERATOR)) {
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
