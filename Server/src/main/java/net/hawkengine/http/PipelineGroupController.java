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

package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.services.PipelineGroupService;
import net.hawkengine.services.interfaces.IPipelineGroupService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Consumes("application/json")
@Produces("application/json")
@Path("/pipeline-groups")
public class PipelineGroupController {
    private IPipelineGroupService pipelineGroupService;
    private SchemaValidator schemaValidator;

    public PipelineGroupController() {
        this.pipelineGroupService = new PipelineGroupService();
        this.schemaValidator = new SchemaValidator();
    }

    public PipelineGroupController(IPipelineGroupService pipelineGroupService) {
        this.pipelineGroupService = pipelineGroupService;
        this.schemaValidator = new SchemaValidator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPipelineGroups() {
        ServiceResult result = this.pipelineGroupService.getAll();
        return Response.status(Status.OK).entity(result.getObject()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{pipelineGroupId}")
    public Response getPipelineGroupById(@PathParam("pipelineGroupId") String pipelineGroupId) {
        ServiceResult result = this.pipelineGroupService.getById(pipelineGroupId);
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPipelineGroup(PipelineGroup pipelineGroup) {
        String isValid = this.schemaValidator.validate(pipelineGroup);
        if (isValid.equals("OK")) {
            ServiceResult result = this.pipelineGroupService.add(pipelineGroup);
            if (result.getNotificationType() == NotificationType.ERROR) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            }

            return Response.status(Status.CREATED).entity(result.getObject()).build();

        } else {
            return Response.status(Status.BAD_REQUEST)
                    .entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePipelineGroup(PipelineGroup pipelineGroup) {
        String isValid = this.schemaValidator.validate(pipelineGroup);
        if (isValid.equals("OK")) {
            ServiceResult result = this.pipelineGroupService.update(pipelineGroup);
            if (result.getNotificationType() == NotificationType.ERROR) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            }

            return Response.status(Status.OK).entity(result.getObject()).build();

        } else {
            return Response.status(Status.BAD_REQUEST)
                    .entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{pipelineDefinitionId}")
    public Response deleteTaskDefinition(@PathParam("pipelineGroupId") String pipelineGroupId) {
        ServiceResult result = this.pipelineGroupService.delete(pipelineGroupId);
        if (result.getNotificationType() == NotificationType.ERROR) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
        return Response.status(Status.NO_CONTENT)
                .entity(result.getMessage())
                .build();
    }
}
