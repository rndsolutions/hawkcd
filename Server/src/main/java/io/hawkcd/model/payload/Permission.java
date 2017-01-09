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

package io.hawkcd.model.payload;

import io.hawkcd.model.Entity;
import io.hawkcd.model.enums.PermissionScope;

public class Permission extends Entity {
    private PermissionScope permissionScope;
    private String permittedEntityId;

    public PermissionScope getPermissionScope() {
        return this.permissionScope;
    }

    public void setPermissionScope(PermissionScope permissionScope) {
        this.permissionScope = permissionScope;
    }

    public String getPermittedEntityId() {
        return this.permittedEntityId;
    }

    public void setPermittedEntityId(String permittedEntityId) {
        this.permittedEntityId = permittedEntityId;
    }
}
