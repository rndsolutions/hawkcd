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

package io.hawkcd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.hawkcd.core.security.Authorization;
import io.hawkcd.core.security.AuthorizationGrant;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;

import java.util.ArrayList;
import java.util.List;

@Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
public class User extends Entity {

    private String email;
    private String password;
    private String ghAuthCode;
    private String provider;
    private List<AuthorizationGrant> permissions;
    private String userGroupId;
    private boolean isEnabled;

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", ghAuthCode='" + ghAuthCode + '\'' +
                ", provider='" + provider + '\'' +
                ", permissions=" + permissions +
                ", userGroupId=" + userGroupId +
                ", isEnabled=" + isEnabled +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        return this.email.equals(user.email);

    }

    @Override
    public int hashCode() {
        return this.email.hashCode();
    }

    public User() {
        this.setPermissions(new ArrayList<>());
        this.setEnabled(true);
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGhAuthCode() {
        return ghAuthCode;
    }

    public void setGhAuthCode(String ghAuthCode) {
        this.ghAuthCode = ghAuthCode;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public List<AuthorizationGrant> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<AuthorizationGrant> permissions) {
        this.permissions = permissions;
    }

    public String getUserGroupId() {
        return this.userGroupId;
    }

    public void setUserGroupId(String userGroupId) {
        this.userGroupId = userGroupId;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    @JsonProperty("isEnabled")
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }
}
