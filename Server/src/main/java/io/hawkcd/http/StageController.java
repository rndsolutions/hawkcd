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
import io.hawkcd.model.Stage;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.StageService;
import io.hawkcd.services.interfaces.IStageService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Consumes("application/json")
@Produces("application/json")
@Path("/stages")
public class StageController {
    private IStageService stageService;

    public StageController() {
        this.stageService = new StageService();
    }

    public StageController(IStageService stageService) {
        this.stageService = stageService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllStages() {
        ServiceResult response = this.stageService.getAll();
        return Response.status(Status.OK).entity(response.getEntity()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{stageId}")
    public Response getStageById(@PathParam("stageId") String stageId) {
        ServiceResult response = this.stageService.getById(stageId);
        if (response.getNotificationType() == NotificationType.ERROR) {
            return Response.status(Status.NOT_FOUND).entity(response.getMessage()).build();
        }
        return Response.status(Status.OK).entity(response.getEntity()).build();
    }

    /*
        @GET
        @Path("/{stageId}/latest")
        public Response getLatest(@PathParam("stageId") String stageId) {
            // TODO: service to be implemented.
           return Response.noContent().build();
        }
    */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewStage(Stage stage) {
        ServiceResult result = this.stageService.add(stage);
        if (result.getNotificationType() == NotificationType.ERROR) {
            return Response.status(Status.BAD_REQUEST).entity(result.getMessage()).build();
        }
        return Response.status(Status.CREATED).entity(result.getEntity()).build();
    }

    /*
    @POST
    public Response addSpecific(){}
    TODO: service to be implemented.
    */
}
