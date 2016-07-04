package net.hawkengine.http;
import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.Service;
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
        ServiceResult result = this.stageDefinitionService.getAll();
        return Response.ok()
                .entity(result.getObject())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{stageDefinitionId}")
    public Response getStageDefinitionById(@PathParam("stageDefinitionId")
                                                   String stageDefinitionId) {
        ServiceResult result = this.stageDefinitionService.getById(stageDefinitionId);
        boolean hasError = result.hasError();
        if (hasError) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        } else {
            return Response.ok()
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
            boolean hasError = result.hasError();
            if (hasError) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            } else {
                return Response.status(201)
                        .entity(result.getObject())
                        .build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
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
            boolean hasError = result.hasError();
            if (hasError) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            } else {
                return Response.status(200)
                        .entity(result.getObject())
                        .build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
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
        boolean hasError = result.hasError();
        if (hasError) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        } else {
            return Response.status(204)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }
}