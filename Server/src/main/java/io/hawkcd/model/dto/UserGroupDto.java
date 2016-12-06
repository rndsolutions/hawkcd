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
import io.hawkcd.core.security.AuthorizationGrant;
import io.hawkcd.model.Entity;
import io.hawkcd.model.User;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;

import java.util.ArrayList;
import java.util.List;

@Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
public class UserGroupDto extends Entity {
    private List<User> users;
    private List<String> userIds;
    private List<AuthorizationGrant> permissions;

    public UserGroupDto() {
        this.setUsers(new ArrayList<>());
    }

    public List<User> getUsers() {
        return this.users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<String> getUserIds() {
        return this.userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<AuthorizationGrant> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<AuthorizationGrant> permissions) {
        this.permissions = permissions;
    }
}
