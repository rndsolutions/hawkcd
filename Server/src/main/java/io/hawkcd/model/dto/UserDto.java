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

package io.hawkcd.model.dto;

import io.hawkcd.core.security.Authorization;
import io.hawkcd.core.security.Grant;
import io.hawkcd.model.PermissionObject;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.payload.Permission;

import java.util.List;

@Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
public class UserDto extends PermissionObject {
    private String username;
    private List<Grant> permissions;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Grant> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<Grant> permissions) {
        this.permissions = permissions;
    }
}
