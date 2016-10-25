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

package net.hawkengine.services.filters.factories;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.SecurityService;
import net.hawkengine.services.filters.interfaces.ISecurityService;

import java.util.List;

public class SecurityServiceInvoker<T extends DbEntry> {
    private ISecurityService securityService;

    public SecurityServiceInvoker() {
        this.securityService = new SecurityService();
    }

    public SecurityServiceInvoker(ISecurityService securityService) {
        this.securityService = securityService;
    }

    public boolean process(String entity, String className, List<Permission> permissions, String methodName) {
        switch (methodName) {
            case "getById":
                return this.securityService.getById(entity, className, permissions);
            case "add":
                return this.securityService.add(entity, className, permissions);
            case "update":
                return this.securityService.update(entity, className, permissions);
            case "delete":
            case "cancelPipeline":
            case "pausePipeline":
                return this.securityService.delete(entity, className, permissions);
            case "assignUserToGroup":
                return this.securityService.assignUserToGroup(entity, className, permissions);
            case "unassignUserFromGroup":
                return this.securityService.unassignUserFromGroup(entity, className, permissions);
            case "assignPipelineToGroup":
                return this.securityService.assignPipelineToGroup(entity, className, permissions);
            case "unassignPipelineFromGroup":
                return this.securityService.unassignPipelineFromGroup(entity, className, permissions);
            case "addUserWithoutProvider":
                return this.securityService.addUserWithoutProvider(entity, className, permissions);
            case "addUserGroupDto":
                return this.securityService.addUserGroupDto(entity, className, permissions);
            case "updateUserGroupDto":
                return this.securityService.updateUserGroupDto(entity, className, permissions);
            case "addWithMaterialDefinition":
                return this.securityService.addWithMaterialDefinition(entity, className, permissions);
            case "resetUserPassword":
                return this.securityService.resetUserPassword(entity, className, permissions);
            default:
                return false;
        }
    }

    public boolean changeUserPassword(String loggedUserEmail, String entity, String className, List<Permission> permissions, String methodName) {
        switch (methodName) {
            case "changeUserPassword":
                return this.securityService.changeUserPassword(loggedUserEmail, entity, className, permissions);
            default:
                return false;
        }
    }

    public List<T> filterEntities(List<T> entitiesToFilter, String className, List<Permission> permissions, String methodName) {
        switch (methodName) {
            case "getAll":
                return this.securityService.getAll(entitiesToFilter, className, permissions);
            case "getAllPipelineGroupDTOs":
                return this.securityService.getAllPipelineGroupDTOs(entitiesToFilter, className, permissions);
            case "getAllUserGroups":
                return this.securityService.getAllUserGroups(entitiesToFilter, className, permissions);
            case "getAllPipelineHistoryDTOs":
                return this.securityService.getAllPipelineHistoryDtos(entitiesToFilter, className, permissions);
            case "getPipelineArtifactDTOs":
                return this.securityService.getPipelineArtifactDtos(entitiesToFilter, className, permissions);
            default:
                return null;
        }
    }
}
