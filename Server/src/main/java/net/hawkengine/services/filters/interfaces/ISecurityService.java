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

package net.hawkengine.services.filters.interfaces;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.payload.Permission;

import java.util.List;

public interface ISecurityService<T extends DbEntry> {
    List<T> getAll(List<T> entitiesToFilter, String className, List<Permission> permissions);

    List<PipelineGroup> getAllPipelineGroupDTOs(List<T> entitiesToFilter, String className, List<Permission> permissions);

    List getAllPipelineHistoryDtos(List entitiesToFilter, String className, List permissions);

    List getPipelineArtifactDtos(List entitiesToFilter, String className, List permissions);

    boolean getById(String entity, String className, List<Permission> permissions);

    boolean add(String entity, String className, List<Permission> permissions);

    boolean update(String entity, String className, List<Permission> permissions);

    boolean delete(String entity, String className, List<Permission> permissions);

    boolean addUserGroupDto(String entity, String className, List<Permission> permissions);

    boolean updateUserGroupDto(String entity, String className, List<Permission> permissions);

    boolean assignUserToGroup(String entity, String className, List<Permission> permissions);

    boolean unassignUserFromGroup(String entity, String className, List<Permission> permissions);

    List<T> getAllUserGroups(List<T> entitiesToFilter, String className, List<Permission> permissions);

    boolean assignPipelineToGroup(String pipelineGroup, String className, List<Permission> permissions);

    boolean unassignPipelineFromGroup(String pipelineGroup, String className, List<Permission> permissions);

    boolean addUserWithoutProvider(String entity, String className, List<Permission> permissions);

    boolean changeUserPassword(String loggedUserEmail, String entity, String className, List<Permission> permissions);

    boolean addWithMaterialDefinition(String entity, String className, List<Permission> permissions);

    boolean resetUserPassword(String entity, String className, List<Permission> permissions);
}
