package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.services.TaskDefinitionService;
import net.hawkengine.services.interfaces.ITaskDefinitionService;

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

@Path("/task-definitions")
@Consumes("application/json")
@Produces("application/json")
public class TaskDefinitionController {
    private ITaskDefinitionService taskDefinitionService;
    private SchemaValidator schemaValidator;

    public TaskDefinitionController() {
        this.taskDefinitionService = new TaskDefinitionService();
        this.schemaValidator = new SchemaValidator();
    }

    public TaskDefinitionController(ITaskDefinitionService taskDefinitionService) {
        this.taskDefinitionService = taskDefinitionService;
        this.schemaValidator = new SchemaValidator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTaskDefinitions() {
        ServiceResult result = this.taskDefinitionService.getAll();
        return Response.status(Status.OK)
                .entity(result.getObject())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{taskDefinitionId}")
    public Response getTaskDefinitionById(@PathParam("taskDefinitionId") String taskDefinitionId) {
        ServiceResult result = this.taskDefinitionService.getById(taskDefinitionId);
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
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewTaskDefinition(TaskDefinition taskDefinition) {
        String isValid = this.schemaValidator.validate(taskDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.taskDefinitionService.addTask(taskDefinition);
            if (result.getNotificationType() == NotificationType.ERROR) {
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
    public Response updateTaskDefinition(TaskDefinition taskDefinition) {
        String isValid = this.schemaValidator.validate(taskDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.taskDefinitionService.updateTask(taskDefinition);
            if (result.getNotificationType() == NotificationType.ERROR) {
                return Response.status(Status.BAD_REQUEST)
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{taskDefinitionId}")
    public Response deleteTaskDefinition(@PathParam("taskDefinitionId") String taskDefinitionId) {
        ServiceResult result = this.taskDefinitionService.delete(taskDefinitionId);
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
