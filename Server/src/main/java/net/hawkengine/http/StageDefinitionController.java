package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.StageDefinitionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Consumes("application/json")
@Produces("application/json")
@Path("/pipeline-definitions/{pipelineDefinitionId}/stage-definitions")
public class StageDefinitionController {
    private StageDefinitionService stageDefinitionService;
    private SchemaValidator schemaValidator;

    public StageDefinitionController() {
        this.stageDefinitionService = new StageDefinitionService();
        this.schemaValidator = new SchemaValidator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStageDefinitions() {
        ServiceResult result = this.stageDefinitionService.getAll();
        return Response.status(Status.OK)
                .entity(result.getObject())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{stageDefinitionId}")
    public Response getStageDefinitionById(@PathParam("stageDefinitionId")
                                                   String stageDefinitionId) {
        ServiceResult result = this.stageDefinitionService.getById(stageDefinitionId);
        if (result.hasError()) {
            return Response.status(Status.NOT_FOUND)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        } else {
            return Response.status(Status.OK)
                    .entity(result.getObject())
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewStage(StageDefinition stageDefinition) {
        String isValid = this.schemaValidator.validate(stageDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.stageDefinitionService.add(stageDefinition);
            if (result.hasError()) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            } else {
                return Response.status(Status.CREATED)
                        .entity(result.getObject())
                        .build();
            }
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
            if (result.hasError()) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            } else {
                return Response.status(Status.OK)
                        .entity(result.getObject())
                        .build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST)
                    .entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }

    @DELETE
    @Consumes
    @Path("/{stageDefinitionId}")
    public Response deletePipeline(@PathParam("stageDefinitionId") String stageDefinitionId) {
        ServiceResult result = this.stageDefinitionService.delete(stageDefinitionId);
        if (result.hasError()) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        } else {
            return Response.status(Status.NO_CONTENT)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }
}