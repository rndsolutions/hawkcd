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

package net.hawkengine.core.messagingsystem;

import net.hawkengine.model.User;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.services.filters.PermissionService;

import java.util.ArrayList;
import java.util.List;

public class Processor {
    private Publisher publisher;
    private PermissionService permissionService;

    public Processor() {
        this.publisher = new Publisher();
        this.permissionService = new PermissionService();
    }

    public void processRequest(WsContractDto contractDto, User user) {
//        user.getPermissions().addAll(this.permissionService.getUniqueUserGroupPermissions(user));

//        List<Permission> orderedPermissions = this.permissionService.sortPermissions(user.getPermissions());

        // Authorize method

        // Determine channel

        List<String> updatedObjectsIds = new ArrayList<>();
        updatedObjectsIds.add("123");
        updatedObjectsIds.add("125");
        PubSubMessage pubSubMessage = new PubSubMessage("Pipe", "method", NotificationType.SUCCESS, "", "", "", updatedObjectsIds);
        publisher.publish("global", pubSubMessage);
    }

    public void processResponse(PubSubMessage pubSubMessage, String channel) {

        if (channel.equals("global")) {
            if (pubSubMessage.getUserId() == null) {
//                SessionPool.getInstance().sendToAuthorizedSessions();
            } else {
//                SessionPool.getInstance().sendToUserSessions();
            }
        } else if (channel.equals("local")) {
//            SessionPool.getInstance().sendToSingleUserSession();
        }
    }
}
