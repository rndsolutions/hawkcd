package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.PipelineDefinitionService;

import java.util.List;

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


@Consumes("application/json")
@Produces("application/json")
@Path("/pipeline-definitions")
public class PipelineDefinitionController {
    private PipelineDefinitionService pipelineDefinitionService;
    private ServiceResult serviceResult;
    private SchemaValidator schemaValidator;

    public PipelineDefinitionController() {
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.serviceResult = new ServiceResult();
        this.schemaValidator = new SchemaValidator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPipelineDefinitions() {
        this.serviceResult = this.pipelineDefinitionService.getAll();
        List<String> pipelinesList = (List<String>) this.serviceResult.getObject();
        return Response.ok().entity(pipelinesList)
                .entity(this.serviceResult.getMessage())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{pipelineDefinitionId}")
    public Response getPipelineDefinitionById(@PathParam("pipelineDefinitionId")
                                                      String pipelineDefinitionId) {
        boolean hasError = this.serviceResult.hasError();
        if (hasError) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(this.serviceResult.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        } else {
            this.serviceResult = this.pipelineDefinitionService.getById(pipelineDefinitionId);
            return Response.ok().entity(this.serviceResult.getObject())
                    .entity(this.serviceResult.getMessage())
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewPipeline(PipelineDefinition pipelineDefinition) {
        if (this.schemaValidator.validate(pipelineDefinition).equals("OK")) {
            this.serviceResult = this.pipelineDefinitionService.add(pipelineDefinition);
            return Response.status(201)
                    .entity(this.serviceResult.getObject())
                    .entity(this.serviceResult.getMessage())
                    .build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(this.serviceResult.getMessage())
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePipeline(PipelineDefinition pipelineDefinition) {
        if (this.schemaValidator.validate(pipelineDefinition).equals("OK")) {
            this.serviceResult = this.pipelineDefinitionService.update(pipelineDefinition);
            return Response.ok()
                    .entity(this.serviceResult.getObject())
                    .entity(this.serviceResult.getMessage())
                    .build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(this.serviceResult.getMessage())
                    .build();
        }
    }

    @DELETE
    @Consumes
    @Path("/{pipelineDefinitionId}")
    public Response deletePipeline(@PathParam("pipelineDefinitionId") String pipelineDefinitionId) {
        this.serviceResult = this.pipelineDefinitionService.delete(pipelineDefinitionId);
        boolean hasError = this.serviceResult.hasError();
        if (hasError) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(this.serviceResult.getMessage())
                    .build();
        } else {
            return Response.status(204).entity(this.serviceResult.getMessage()).build();
        }
    }
}