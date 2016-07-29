package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.PipelineGroupService;
import net.hawkengine.services.interfaces.IPipelineGroupService;

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPipelineGroup(PipelineGroup pipelineGroup) {
        String isValid = this.schemaValidator.validate(pipelineGroup);
        if (isValid.equals("OK")) {
            ServiceResult result = this.pipelineGroupService.add(pipelineGroup);
            if (result.hasError()) {
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
            if (result.hasError()) {
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
        if (result.hasError()) {
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
