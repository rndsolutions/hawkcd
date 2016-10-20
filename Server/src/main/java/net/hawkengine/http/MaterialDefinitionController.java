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
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.services.MaterialDefinitionService;
import net.hawkengine.services.interfaces.IMaterialDefinitionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Consumes("application/json")
@Produces("application/json")
@Path("/materials")
public class MaterialDefinitionController {

    private IMaterialDefinitionService materialDefinitionService;
    private SchemaValidator schemaValidator;

    public MaterialDefinitionController() {
        this.materialDefinitionService = new MaterialDefinitionService();
        this.schemaValidator = new SchemaValidator();
    }

    public MaterialDefinitionController(IMaterialDefinitionService materialDefinitionService) {
        this.materialDefinitionService = materialDefinitionService;
        this.schemaValidator = new SchemaValidator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMaterialDefinitions() {
        ServiceResult result = this.materialDefinitionService.getAll();
        return Response.status(Response.Status.OK).entity(result.getObject()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{materialDefinitionId}")
    public Response getMaterialDefinitionById(@PathParam("materialDefinitionId")
                                                      String materialDefinitionId) {
        ServiceResult result = this.materialDefinitionService.getById(materialDefinitionId);

        if (result.getNotificationType() == NotificationType.ERROR) {
            return Response.status(Status.NOT_FOUND).entity(result.getMessage()).build();
        }

        return Response.status(Status.OK).entity(result.getObject()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMaterialDefinition(MaterialDefinition materialDefinition) {
        String isValid = this.schemaValidator.validate(materialDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.materialDefinitionService.add(materialDefinition);
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
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMaterialDefinition(MaterialDefinition materialDefinition) {
        String isValid = this.schemaValidator.validate(materialDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.materialDefinitionService.update(materialDefinition);
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
    @Path("/{materialDefinitionId}")
    public Response deleteMaterialDefinition(@PathParam("materialDefinitionId")
                                                     String materialDefinitionId) {
        ServiceResult result = this.materialDefinitionService.delete(materialDefinitionId);
        if (result.getNotificationType() == NotificationType.ERROR) {
            return Response.status(Status.NOT_FOUND).entity(result.getMessage()).build();
        }

        return Response.status(Status.NO_CONTENT).build();
    }
}