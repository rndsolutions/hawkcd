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

package io.hawkcd.services;

import io.hawkcd.core.security.Authorization;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.utilities.constants.NotificationMessages;
import io.hawkcd.db.DbRepositoryFactory;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.model.GitMaterial;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.PipelineDefinition;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.interfaces.IMaterialDefinitionService;
import io.hawkcd.services.interfaces.IPipelineDefinitionService;

import java.util.ArrayList;
import java.util.List;

public class MaterialDefinitionService extends CrudService<MaterialDefinition> implements IMaterialDefinitionService {
    private static final Class CLASS_TYPE = MaterialDefinition.class;

    private IPipelineDefinitionService pipelineDefinitionService;

    public MaterialDefinitionService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.pipelineDefinitionService = new PipelineDefinitionService();
    }

    public MaterialDefinitionService(IDbRepository repository, IPipelineDefinitionService pipelineDefinitionService) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.pipelineDefinitionService = pipelineDefinitionService;
    }

    @Override
    @Authorization( scope = PermissionScope.SERVER, type = PermissionType.VIEWER )
    public ServiceResult getById(String materialDefinitionId) {
        return super.getById(materialDefinitionId);
    }

    @Override
    @Authorization( scope = PermissionScope.SERVER, type = PermissionType.VIEWER )
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAllFromPipelineDefinition(String pipelineDefinitionId) {
        List<MaterialDefinition> materialDefinitions = new ArrayList<>();

        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipelineDefinitionId).getObject();
        if (pipelineDefinition != null) {
            for (String materialDefinitionId : pipelineDefinition.getMaterialDefinitionIds()) {
                MaterialDefinition materialDefinition = (MaterialDefinition) this.getById(materialDefinitionId).getObject();
                if (materialDefinition != null) {
                    materialDefinitions.add(materialDefinition);
                }
            }
        }

        return super.createServiceResultArray(materialDefinitions, NotificationType.SUCCESS, NotificationMessages.RETRIEVED_SUCCESSFULLY);
    }

    @Override
    @Authorization( scope = PermissionScope.SERVER, type = PermissionType.ADMIN )
    public ServiceResult add(MaterialDefinition materialDefinition) {
        return super.add(materialDefinition);
    }

    @Override
    @Authorization( scope = PermissionScope.SERVER, type = PermissionType.ADMIN )
    public ServiceResult add(GitMaterial materialDefinition) {
        return super.add(materialDefinition);
    }

    @Override
    @Authorization( scope = PermissionScope.SERVER, type = PermissionType.ADMIN )
    public ServiceResult update(MaterialDefinition materialDefinition) {
        return super.update(materialDefinition);
    }

    @Override
    @Authorization( scope = PermissionScope.SERVER, type = PermissionType.ADMIN )
    public ServiceResult update(GitMaterial materialDefinition) {
        return super.update(materialDefinition);
    }

    @Override
    @Authorization( scope = PermissionScope.SERVER, type = PermissionType.ADMIN )
    public ServiceResult delete(MaterialDefinition materialDefinitionId) {
        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
        List<String> assignedIds = new ArrayList<>();
        for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
            for (String assignedId : pipelineDefinition.getMaterialDefinitionIds()) {
                if (assignedId.equals(materialDefinitionId)) {
                    assignedIds.add(assignedId);
                }
            }
        }

        if (!assignedIds.isEmpty()) {
            String assignedIdsAsString = String.join(", ", assignedIds);
            return super.createServiceResult(null, NotificationType.ERROR, String.format(NotificationMessages.COULD_NOT_BE_DELETED, assignedIdsAsString));
        }

        return super.delete(materialDefinitionId);
    }
}
