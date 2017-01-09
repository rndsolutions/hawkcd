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

package io.hawkcd.utilities;

import io.hawkcd.core.ServerFactory;
import io.hawkcd.core.config.Config;
import io.hawkcd.model.*;
import io.hawkcd.core.security.AuthorizationGrant;
import io.hawkcd.core.session.SessionService;
import io.hawkcd.model.configuration.Configuration;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.services.PipelineGroupService;
import io.hawkcd.services.UserService;
import io.hawkcd.services.interfaces.IPipelineGroupService;
import io.hawkcd.services.interfaces.IUserService;
import org.jvnet.hk2.internal.Collector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Initializer {
    private SessionService sessionService;
    private IUserService userService;
    private IPipelineGroupService pipelineGroupService;

    public Initializer() {
        this.userService = new UserService();
        this.pipelineGroupService = new PipelineGroupService();
        this.sessionService =  new SessionService();
    }

    public Initializer(IUserService userService, IPipelineGroupService pipelineGroupService) {
        this.userService = userService;
        this.pipelineGroupService = pipelineGroupService;
    }

    public void initialize() throws IOException {
        this.addDefaultAdminUser();
        this.addDefualtPipelineGroup();

        Configuration configuration = Config.getConfiguration();
        String serverId = Config.getConfiguration().getServerId();
        Server server;
        if (serverId == null) { // running the server for first time on this node
            server =  new Server();
            ServerFactory.getServerService().add(server);
            configuration.setServerId(server.getId());
            Config.addServerId(server.getId());
        } else {
            server = (Server) ServerFactory.getServerService().getById(serverId).getEntity();
            if (server == null){
                Server srv = new Server();
                srv.setId(serverId);
                ServerFactory.getServerService().add(srv);
            }else
            {
                ServerFactory.getServerService().update(server);
            }
        }
        this.cleanUpleftOverSessions();
    }

    private void cleanUpleftOverSessions(){
        Server server = ServerFactory.getServerService().getServer();

        List<SessionDetails> sessions = ((List<SessionDetails>) this.sessionService.getAll().getEntity())
                .stream()
                .filter(s -> s.getNodeId().equals(server.getId()))
                .collect(Collectors.toList());

        for (Entity session : sessions) {
            this.sessionService.delete((SessionDetails) session);
        }
    }

    private ServiceResult addDefaultAdminUser() {
        User adminUser = new User();
        adminUser.setEmail("admin@admin.com");
        adminUser.setPassword("admin");
        AuthorizationGrant adminUserPermission = new AuthorizationGrant(PermissionScope.SERVER, PermissionType.ADMIN);
        adminUserPermission.setPermittedEntityId("SERVER");
//        adminUserPermission.setPermissionType(PermissionType.ADMIN);
//        adminUserPermission.setPermissionScope(PermissionScope.SERVER);
        List<AuthorizationGrant> permissions = new ArrayList<>();
        permissions.add(adminUserPermission);

        adminUser.setPermissions(permissions);

        return this.userService.add(adminUser);
    }

    private ServiceResult addDefualtPipelineGroup() {
        List<PipelineGroup> pipelineGroups = (List<PipelineGroup>) this.pipelineGroupService.getAll().getEntity();
        if (pipelineGroups.size() == 0) {

            PipelineGroup defaultPipelineGroup = new PipelineGroup();
            defaultPipelineGroup.setName("defaultPipelineGroup");

            return this.pipelineGroupService.add(defaultPipelineGroup);
        }

        return null;
    }
}
