package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.PipelineDefinitionService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Consumes("application/json")
@Produces("application/json")
@Path("/pipeline-definitions")

public class PipelineDefinitionController {
    private PipelineDefinitionService pipelineDefinitionService;
    private SchemaValidator schemaValidator;

    public PipelineDefinitionController() {
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.schemaValidator = new SchemaValidator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPipelineDefinitions() {
        ServiceResult result = this.pipelineDefinitionService.getAll();
        return Response.status(Status.OK)
                .entity(result.getObject())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{pipelineDefinitionId}")
    public Response getPipelineDefinitionById(@PathParam("pipelineDefinitionId")
                                                      String pipelineDefinitionId) {
        ServiceResult result = this.pipelineDefinitionService.getById(pipelineDefinitionId);
        if (result.hasError()) {
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
    public Response addNewPipeline(PipelineDefinition pipelineDefinition) {
        String isValid = this.schemaValidator.validate(pipelineDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.pipelineDefinitionService.add(pipelineDefinition);
            if (result.hasError()) {
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
    public Response updatePipeline(PipelineDefinition pipelineDefinition) {
        String isValid = this.schemaValidator.validate(pipelineDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.pipelineDefinitionService.update(pipelineDefinition);
            if (result.hasError()) {
                return Response.status(Status.NOT_FOUND)
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

    @DELETE
    @Consumes
    @Path("/{pipelineDefinitionId}")
    public Response deletePipeline(@PathParam("pipelineDefinitionId")
                                           String pipelineDefinitionId) {
        ServiceResult result = this.pipelineDefinitionService.delete(pipelineDefinitionId);
        if (result.hasError()) {
            return Response.status(Status.NOT_FOUND)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        return Response.status(Status.NO_CONTENT).build();
    }
}