package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.services.TaskDefinitionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/pipeline-definitions/{pipelineDefinitionId}/stage-definitions/{stageDefinitionId}/job-definitions/{jobDefinitionId}/task-definitions")
@Consumes("application/json")
@Produces("application/json")
public class TaskDefinitionController {
    private TaskDefinitionService taskDefinitionService;
    private SchemaValidator schemaValidator;

    public TaskDefinitionController() {
        this.taskDefinitionService = new TaskDefinitionService();
        this.schemaValidator = new SchemaValidator();
    }

    public TaskDefinitionController(TaskDefinitionService taskDefinitionService) {
        this.taskDefinitionService = taskDefinitionService;
        this.schemaValidator = new SchemaValidator();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTaskDefinitions() {
        ServiceResult result = this.taskDefinitionService.getAll();
        return Response.status(Response.Status.OK)
                .entity(result.getObject())
                .build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{taskDefinitionId}")
    public Response getById(@PathParam("taskDefinitionId") String taskDefinitionId) {
        ServiceResult result = this.taskDefinitionService.getById(taskDefinitionId);
        if (result.hasError()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(result)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
        return Response.status(Response.Status.OK)
                .entity(result.getObject())
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(TaskDefinition taskDefinition) {
        String isValid = this.schemaValidator.validate(taskDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.taskDefinitionService.addTask(taskDefinition);
            if (result.hasError()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            }

            return Response.status(Response.Status.OK)
                    .entity(result.getObject())
                    .build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(TaskDefinition taskDefinition) {
        String isValid = this.schemaValidator.validate(taskDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.taskDefinitionService.addTask(taskDefinition);
            if (result.hasError()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            }

            return Response.status(Response.Status.OK)
                    .entity(result.getObject())
                    .build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }
}
