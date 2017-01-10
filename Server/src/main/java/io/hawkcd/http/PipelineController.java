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

import io.hawkcd.http.security.Secured;
import io.hawkcd.model.Pipeline;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.PipelineService;
import io.hawkcd.services.interfaces.IPipelineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Consumes("application/json")
@Produces("application/json")
@Api(value = "/pipelines", description = "Web Services to browse entities")
@Path("/pipelines")
public class PipelineController {

    private IPipelineService pipelineService;

    /**
     * Holds the name of the business service PACKAGE the resource endpoints expose
     */
    public static final String SERVICE_PACKAGE_NAME = "io.hawkcd.services";

    /**
     * Holds the name of the business service the resource endpoints expose
     */
    public static final String SERVICE_CLASS_NAME = "PipelineService";


    public PipelineController() {

        this.pipelineService = new PipelineService();
    }

    public PipelineController(IPipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @GET
    @ApiOperation(value = "Return one entity", notes = "Returns one entity at random", response = Pipeline.class)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response getAll(@Context ContainerRequestContext sc) {

            ServiceResult response = this.pipelineService.getAll();
            return Response.status(Status.OK)
                    .entity(response.getEntity())
                    .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{pipelineDefinitionId}")
    @Secured
    public Response getPipelineById(@PathParam("pipelineDefinitionId") String pipelineDefinitionId) {
        ServiceResult response = this.pipelineService.getById(pipelineDefinitionId);
        if (response.getNotificationType() == NotificationType.ERROR) {
            return
                    Response.status(Status.NOT_FOUND)
                            .entity(response.getMessage())
                            .type(MediaType.TEXT_HTML)
                            .build();
        } else {
            return Response.status(Status.OK).entity(response.getEntity()).build();
        }
    }

    /*
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{pipelineDefinitionId}/{pipelineId}")
    public Response getSpecificPipelineId(){
        return null;
    }
    TODO: service to be implemented.

    */

    /*
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{pipelineDefinitionId}/latest")
    public Response getLatest(){
        return null;
    }
    TODO: service to be implemented.

    */

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPipeline(Pipeline pipeline) {
        ServiceResult response = this.pipelineService.add(pipeline);

        if (response.getNotificationType() == NotificationType.ERROR) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(response.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        } else {
            return Response.status(Status.CREATED)
                    .entity(response.getEntity())
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response update(Pipeline pipeline) {
        ServiceResult response = this.pipelineService.update(pipeline);
        if (response.getNotificationType() == NotificationType.ERROR) {
            return Response.status(Status.NOT_FOUND).entity(response.getMessage()).build();
        } else {
            return Response.status(Status.OK).entity(response.getEntity()).build();
        }
    }

    /*
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{pipelineDefinitionId}/start-specific")
    public Response startSpecific(){
        return null;
    }
    TODO: service to be implemented.

    */
}
