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

import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.payload.Permission;
import io.hawkcd.services.filters.interfaces.IAuthorizationService;

import java.util.List;

public class AgentAuthorizationService implements IAuthorizationService {

    @Override
    public List getAll(List permissions, List entriesToFilter) {
        return entriesToFilter;
    }

    @Override
    public boolean getById(String entityId, List permissions) {
        return true;
    }

    @Override
    public boolean add(String entity, List permissions) {
        return this.hasPermission(permissions);
    }

    @Override
    public boolean update(String entity, List permissions) {
        return this.hasPermission(permissions);
    }

    @Override
    public boolean delete(String entityId, List permissions) {
        return this.hasPermission(permissions);
    }

    private boolean hasPermission(List<Permission> permissions) {
        for (Permission permission : permissions) {
            if (permission.getPermissionScope() == PermissionScope.SERVER && permission.getPermissionType() == PermissionType.ADMIN) {
                return true;
            }
        }
        return false;
    }
}
