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
import io.hawkcd.db.DbRepositoryFactory;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.model.Material;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.services.interfaces.IMaterialService;

import java.util.List;
import java.util.stream.Collectors;

public class MaterialService extends CrudService<Material> implements IMaterialService {
    private static final Class CLASS_TYPE = Material.class;

    public MaterialService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    public MaterialService(IDbRepository repository) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    @Override
    @Authorization( scope = PermissionScope.SERVER, type = PermissionType.VIEWER )
    public ServiceResult getById(String materialId) {
        return super.getById(materialId);
    }

    @Override
    @Authorization( scope = PermissionScope.SERVER, type = PermissionType.NONE )
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    @Authorization( scope = PermissionScope.SERVER, type = PermissionType.ADMIN )
    public ServiceResult add(Material material) {
        return super.add(material);
    }

    @Override
    @Authorization( scope = PermissionScope.SERVER, type = PermissionType.ADMIN )
    public ServiceResult update(Material material) {
        return super.update(material);
    }

    @Override
    @Authorization( scope = PermissionScope.SERVER, type = PermissionType.ADMIN )
    public ServiceResult delete(Material material) {
        return super.delete(material);
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getAllFromPipelineDefinition(String pipelineDefinitionId) {
        List<Material> materials = (List<Material>) this.getAll().getEntity();
        materials = materials
                .stream()
                .filter(m -> m.getPipelineDefinitionId().equals(pipelineDefinitionId))
                .collect(Collectors.toList());

        return super.createServiceResultArray(materials, NotificationType.SUCCESS, "retrieved successfully");
    }

    @Override
    @Authorization( scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER )
    public ServiceResult getLatestMaterial(String materialDefinitionId, String pipelineDefinitionId) {
        List<Material> materials = (List<Material>) this.getAllFromPipelineDefinition(pipelineDefinitionId).getEntity();
        Material latestMaterial = materials
                .stream()
                .filter(m -> m.getMaterialDefinition().getId().equals(materialDefinitionId))
                .sorted((m1, m2) -> m2.getChangeDate().compareTo(m1.getChangeDate()))
                .findFirst()
                .orElse(null);

        if (latestMaterial != null) {
            return super.createServiceResult(latestMaterial, NotificationType.SUCCESS, "retrieved successfully");
        } else {
            return super.createServiceResult(latestMaterial, NotificationType.ERROR, "not found");
        }
    }
}
