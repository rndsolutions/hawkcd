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
import io.hawkcd.core.security.Permission;
import io.hawkcd.core.security.Scope;
import io.hawkcd.db.DbRepositoryFactory;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.model.Pipeline;
import io.hawkcd.model.PipelineDefinition;
import io.hawkcd.model.PipelineGroup;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.dto.PipelineDefinitionDto;
import io.hawkcd.model.dto.PipelineGroupDto;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.services.interfaces.IPipelineDefinitionService;
import io.hawkcd.services.interfaces.IPipelineGroupService;
import io.hawkcd.services.interfaces.IPipelineService;

import java.util.ArrayList;
import java.util.List;

public class PipelineGroupService extends CrudService<PipelineGroup> implements IPipelineGroupService {
    private static final Class CLASS_TYPE = PipelineGroup.class;

    private IPipelineDefinitionService pipelineDefinitionService;
    private IPipelineService pipelineService;

    public PipelineGroupService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.pipelineService = new PipelineService();
    }

    public PipelineGroupService(IDbRepository repository) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    @Override
    @Authorization( scope = {Scope.PipelineGroup, Scope.Server},
            permission = {Permission.Viewer, Permission.Admin, Permission.Operator})
    public ServiceResult getById(String pipelineGroupId) {
        return super.getById(pipelineGroupId);
    }

    @Override
    @Authorization( scope = {Scope.PipelineGroup, Scope.Server},
            permission = {Permission.Viewer, Permission.Admin, Permission.Operator})
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    @Authorization( scope = {Scope.PipelineGroup, Scope.Server},
            permission = {Permission.Admin})
    public ServiceResult add(PipelineGroup pipelineGroup) {
        List<PipelineGroup> pipelineGroups = (List<PipelineGroup>) this.getAll().getObject();
        PipelineGroup existingPipelineGroup = pipelineGroups.stream().filter(p -> p.getName().equals(pipelineGroup.getName())).findFirst().orElse(null);
        if (existingPipelineGroup != null || pipelineGroup.getName().equals("UnassignedPipelines")) {
            ServiceResult result = new ServiceResult(pipelineGroup, NotificationType.ERROR, "Pipeline Group with the same name already exists.");

            return result;
        }
        return super.add(pipelineGroup);
    }

    @Override
    @Authorization( scope = {Scope.PipelineGroup, Scope.Server},
            permission = Permission.Admin)
    public ServiceResult update(PipelineGroup pipelineGroup) {
        return super.update(pipelineGroup);
    }

    @Override
    @Authorization( scope = {Scope.PipelineGroup, Scope.Server},
            permission = Permission.Admin)
    public ServiceResult delete(String pipelineGroupId) {
        return super.delete(pipelineGroupId);
    }

    @Override
    @Authorization( scope = {Scope.PipelineGroup, Scope.Server},
            permission = {Permission.Viewer, Permission.Admin})
    public ServiceResult getAllPipelineGroupDTOs() {
        List<PipelineGroup> pipelineGroups = (List<PipelineGroup>) super.getAll().getObject();
//        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) pipelineDefinitionService.getAll().getObject();
//
//        for (PipelineGroup pipelineGroup : pipelineGroups) {
//            List<PipelineDefinition> pipelineDefinitionsToAdd = new ArrayList<>();
//            for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
//                if (!pipelineDefinition.getPipelineGroupId().isEmpty() && pipelineDefinition.getPipelineGroupId().equals(pipelineGroup.getId())) {
//                    pipelineDefinitionsToAdd.add(pipelineDefinition);
//                }
//            }
//
//            pipelineGroup.setPipelines(pipelineDefinitionsToAdd);
//        }

        ServiceResult result = new ServiceResult(pipelineGroups, NotificationType.SUCCESS, "All Pipeline Groups retrieved successfully.");

        return result;
    }

    public List<PipelineGroup> placePipelinesIntoGroups(List<PipelineGroup> pipelineGroups, List<PipelineDefinition> pipelineDefinitions) {
        PipelineGroup unassignedPipelinesGroup = new PipelineGroup();
        unassignedPipelinesGroup.setPermissionType(PermissionType.VIEWER);
        unassignedPipelinesGroup.setName("UnassignedPipelines");

        for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
            boolean foundGroupForPipeline = false;
            for (PipelineGroup pipelineGroup : pipelineGroups) {
                if (pipelineDefinition.getPipelineGroupId().equals(pipelineGroup.getId())) {
                    if (pipelineGroup.getPermissionType() == PermissionType.NONE) {
                        pipelineGroup.setPermissionType(PermissionType.VIEWER);
                    }

                    pipelineGroup.getPipelines().add(pipelineDefinition);
                    foundGroupForPipeline = true;
                    break;
                }
            }

            if (!foundGroupForPipeline) {
                unassignedPipelinesGroup.getPipelines().add(pipelineDefinition);
            }
        }

        if (!unassignedPipelinesGroup.getPipelines().isEmpty()) {
            pipelineGroups.add(unassignedPipelinesGroup);
        }

        return pipelineGroups;
    }

    public List<PipelineGroupDto> convertGroupsToDtos(List<PipelineGroup> pipelineGroups) {
        List<PipelineGroupDto> pipelineGroupDtos = new ArrayList<>();
        for (PipelineGroup pipelineGroup : pipelineGroups) {
            PipelineGroupDto pipelineGroupDto = new PipelineGroupDto();
            pipelineGroupDto.constructDto(pipelineGroup);
            List<PipelineDefinition> pipelineDefinitions = pipelineGroup.getPipelines();
            List<PipelineDefinitionDto> pipelineDefinitionDtos = new ArrayList<>();
            for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
                PipelineDefinitionDto pipelineDefinitionDto = new PipelineDefinitionDto();
                List<Pipeline> definitionRuns = (List<Pipeline>) this.pipelineService.getAllByDefinitionId(pipelineDefinition.getId()).getObject();
                pipelineDefinitionDto.constructDto(pipelineDefinition, definitionRuns);
                pipelineDefinitionDtos.add(pipelineDefinitionDto);
            }

            pipelineGroupDto.setPipelines(pipelineDefinitionDtos);
            pipelineGroupDtos.add(pipelineGroupDto);
        }

        return pipelineGroupDtos;
    }
}
