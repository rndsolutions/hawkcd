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

import io.hawkcd.utilities.SchemaValidator;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.StageDefinition;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.StageDefinitionService;
import io.hawkcd.services.interfaces.IStageDefinitionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Consumes("application/json")
@Produces("application/json")
@Path("/stage-definitions")
public class StageDefinitionController {
    private IStageDefinitionService stageDefinitionService;
    private SchemaValidator schemaValidator;

    public StageDefinitionController() {
        this.stageDefinitionService = new StageDefinitionService();
        this.schemaValidator = new SchemaValidator();
    }

    public StageDefinitionController(IStageDefinitionService stageDefinitionService) {
        this.stageDefinitionService = stageDefinitionService;
        this.schemaValidator = new SchemaValidator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllStageDefinitions() {
        ServiceResult result = this.stageDefinitionService.getAll();
        return Response.status(Status.OK)
                .entity(result.getObject())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{stageDefinitionId}")
    public Response getStageDefinitionById(@PathParam("stageDefinitionId")
                                                   String stageDefinitionId) {
        ServiceResult result = this.stageDefinitionService.getById(stageDefinitionId);
        if (result.getNotificationType() == NotificationType.ERROR) {
            return Response.status(Status.NOT_FOUND)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
        return Response.status(Status.OK)
                .entity(result.getObject())
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewStage(StageDefinition stageDefinition) {
        String isValid = this.schemaValidator.validate(stageDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.stageDefinitionService.add(stageDefinition);
            if (result.getNotificationType() == NotificationType.ERROR) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            }
            return Response.status(Status.CREATED)
                    .entity(result.getObject())
                    .build();
        } else {
            return Response.status(Status.BAD_REQUEST)
                    .entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateStage(StageDefinition stageDefinition) {
        String isValid = this.schemaValidator.validate(stageDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.stageDefinitionService.update(stageDefinition);
            if (result.getNotificationType() == NotificationType.ERROR) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            }
            return Response.status(Status.OK)
                    .entity(result.getObject())
                    .build();
        } else {
            return Response.status(Status.BAD_REQUEST)
                    .entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }

//    @DELETE
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Path("/{stageDefinitionId}")
//    public Response deleteStage(@PathParam("stageDefinitionId") String stageDefinitionId) {
//        ServiceResult result = this.stageDefinitionService.delete(stageDefinitionId);
//        if (result.getNotificationType() == NotificationType.ERROR) {
//            return Response.status(Status.BAD_REQUEST)
//                    .entity(result.getMessage())
//                    .type(MediaType.TEXT_HTML)
//                    .build();
//        }
//        return Response.status(Status.NO_CONTENT)
//                .entity(result.getMessage())
//                .build();
//    }
}