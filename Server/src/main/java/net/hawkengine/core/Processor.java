/*
 *   Copyright (C) 2016 R&D Solutions Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *
 */

package net.hawkengine.core;

import net.hawkengine.model.User;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.PermissionService;
import net.hawkengine.ws.Publisher;

import java.util.List;

public class Processor {
    private Publisher jedisPublisher;
    private PermissionService permissionService;

    public void process(WsContractDto contractDto, User user) {
        user.getPermissions().addAll(this.permissionService.getUniqueUserGroupPermissions(user));

        List<Permission> orderedPermissions = this.permissionService.sortPermissions(user.getPermissions());

        // Authorize method

        // Determine channel

        jedisPublisher.publish("channelName", object);

    }
}
