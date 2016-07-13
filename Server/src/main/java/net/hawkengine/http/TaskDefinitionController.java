package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.services.TaskDefinitionService;

import javax.annotation.security.DeclareRoles;
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
    private TaskDefinitionService taskDefinitionService;
    private SchemaValidator schemaValidator;

    public TaskDefinitionController(){
        this.taskDefinitionService = new TaskDefinitionService();
        this.schemaValidator = new SchemaValidator();
    }

    @GET
    public Response getAllTaskDefinitions(){
        ServiceResult response = this.taskDefinitionService.getAll();
        return Response.status(Status.OK).entity(response.getObject()).build();
    }

    @GET
    @Path("taskDefinitionId")
    public Response getTaskById(@PathParam("taskDefinitionId") String taskDefinitionId){
        ServiceResult response = this.taskDefinitionService.getById(taskDefinitionId);
        if (response.hasError()){
            return Response.status(Status.NOT_FOUND)
                    .entity(response.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        return Response.status(Status.OK).entity(response.getObject()).build();
    }

    @POST
    public Response addNewTaskDefinition(TaskDefinition taskDefinition){
        String isValid = this.schemaValidator.validate(taskDefinition);

        if (isValid != "OK"){
            return Response.status(Status.BAD_REQUEST).entity(isValid).build();
        }

        ServiceResult result = this.taskDefinitionService.addTask(taskDefinition);

        if (result.hasError()){
            return Response.status(Status.BAD_REQUEST).entity(result.getMessage()).build();
        }

        return Response.status(Status.CREATED).entity(result.getObject()).build();
    }

    @PUT
    public Response updateTaskDefinition(TaskDefinition taskDefinition){
        String isValid = this.schemaValidator.validate(taskDefinition);

        if (isValid != "OK"){
            return Response.status(Status.BAD_REQUEST).entity(isValid).build();
        }

        ServiceResult result = this.taskDefinitionService.update(taskDefinition);

        if (result.hasError()){
            return Response.status(Status.BAD_REQUEST).entity(result.getMessage()).build();
        }

        return Response.status(Status.CREATED).entity(result.getObject()).build();
    }

    @DELETE
    @Path("taskDefinitionId")
    public Response deleteTaskDefinition(@PathParam("taskDefinitionId") String taskDefinitionId){
        ServiceResult result = this.taskDefinitionService.delete(taskDefinitionId);

        if (result.hasError()){
            return Response.status(Status.NOT_FOUND).entity(result.getMessage()).build();
        }

        return Response.status(Status.NO_CONTENT).entity(result.getMessage()).build();

    }
}
