package net.hawkengine.http;
import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.StageDefinitionService;
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
@Path("/pipeline-definitions/{pipelineDefinitionId}/stage-definitions")
public class StageDefinitionController {
    private StageDefinitionService stageDefinitionService;
    private ServiceResult serviceResult;
    private SchemaValidator schemaValidator;

    public StageDefinitionController() {
        this.stageDefinitionService = new StageDefinitionService();
        this.serviceResult = new ServiceResult();
        this.schemaValidator = new SchemaValidator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStageDefinitions() {
        this.serviceResult = this.stageDefinitionService.getAll();
        List<String> stagesList = (List<String>) this.serviceResult.getObject();
        return Response.ok().entity(stagesList)
                .entity(this.serviceResult.getMessage())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{stageDefinitionId}")
    public Response getStageDefinitionById(@PathParam("stageDefinitionId")
                                                      String stageDefinitionId) {
        boolean hasError = this.serviceResult.hasError();
        if (hasError) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(this.serviceResult.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        } else {
            this.serviceResult = this.stageDefinitionService.getById(stageDefinitionId);
            return Response.ok().entity(this.serviceResult.getObject())
                    .entity(this.serviceResult.getMessage())
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewStage(StageDefinition stageDefinition) {
        if (this.schemaValidator.validate(stageDefinition).equals("OK")) {
            this.serviceResult = this.stageDefinitionService.add(stageDefinition);
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
    public Response updateStage(StageDefinition stageDefinition) {
        if (this.schemaValidator.validate(stageDefinition).equals("OK")) {
            this.serviceResult = this.stageDefinitionService.update(stageDefinition);
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
    @Path("/{stageDefinitionId}")
    public Response deletePipeline(@PathParam("stageDefinitionId") String stageDefinitionId) {
        this.serviceResult = this.stageDefinitionService.delete(stageDefinitionId);
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