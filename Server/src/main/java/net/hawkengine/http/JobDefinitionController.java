package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.JobDefinitionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Consumes("application/json")
@Produces("application/json")
@Path("/job-definitions")
public class JobDefinitionController {
    private JobDefinitionService jobDefinitionService;
    private SchemaValidator schemaValidator;

    public JobDefinitionController() {
        this.jobDefinitionService = new JobDefinitionService();
        this.schemaValidator = new SchemaValidator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobDefinitions() {
        ServiceResult result = this.jobDefinitionService.getAll();
        return Response.status(Status.OK)
                .entity(result.getObject())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{jobDefinitionId}")
    public Response getJobDefinitionById(@PathParam("jobDefinitionId") String jobDefinitionId) {
        ServiceResult result = this.jobDefinitionService.getById(jobDefinitionId);
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
    public Response addNewJob(JobDefinition jobDefinition) {
      //  String isValid = this.schemaValidator.validate(jobDefinition);
      //  if (isValid.equals("OK")) {
            ServiceResult result = this.jobDefinitionService.add(jobDefinition);
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

        }/* else {
            return Response.status(Status.BAD_REQUEST)
                    .entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }
*/
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateJob(JobDefinition jobDefinition) {
      //  String isValid = this.schemaValidator.validate(jobDefinition);
        //if (isValid.equals("OK")) {
            ServiceResult result = this.jobDefinitionService.update(jobDefinition);
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

        }/* else {
            return Response.status(Status.BAD_REQUEST)
                    .entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }
*/
    @DELETE
    @Consumes
    @Path("/{jobDefinitionId}")
    public Response deleteJob(@PathParam("jobDefinitionId") String jobDefinitionId) {
        ServiceResult result = this.jobDefinitionService.delete(jobDefinitionId);
        if (result.hasError()) {
            return Response.status(Status.NOT_FOUND)
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