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

package io.hawkcd.http;

import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.User;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

//TODO: implement scheme validation

@Consumes("application/json")
@Produces("application/json")
@Path("/account")
public class AccountController {

    private UserService usrService;

    public AccountController() {
        this.usrService = new UserService();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/users")
    public Response getUsers() {
        ServiceResult result = this.usrService.getAll();
        return Response.status(Status.OK)
                .entity(result.getEntity())
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/users")
    public Response addUser(User user) {

        ServiceResult result = this.usrService.add(user);

        if (result.getNotificationType() == NotificationType.ERROR) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        } else {
            return Response.status(Status.CREATED)
                    .entity(result.getEntity())
                    .build();
        }
    }

}
