package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.JobDefinitionService;
import net.hawkengine.services.Service;

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
@Path("/pipeline-definitions/{pipelineDefinitionId}/stage-definitions/{stageDefinitionId}/job-definitions")
public class JobDefinitionController {
    private JobDefinitionService jobDefinitionService;
    private ServiceResult serviceResult;
    private SchemaValidator schemaValidator;

    public JobDefinitionController() {
        this.jobDefinitionService = new JobDefinitionService();
        this.serviceResult = new ServiceResult();
        this.schemaValidator = new SchemaValidator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobDefinitions() {
        ServiceResult result = this.jobDefinitionService.getAll();
        return Response.ok()
                .entity(result.getObject())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{jobDefinitionId}")
    public Response getJobDefinitionById(@PathParam("jobDefinitionId")
                                                   String jobDefinitionId) {
        ServiceResult result = this.jobDefinitionService.getById(jobDefinitionId);
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
    public Response addNewJob(JobDefinition jobDefinition) {
        String isValid = this.schemaValidator.validate(jobDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.jobDefinitionService.add(jobDefinition);
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
    public Response updateJob(JobDefinition jobDefinition) {
        String isValid = this.schemaValidator.validate(jobDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.jobDefinitionService.update(jobDefinition);
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
    @Path("/{jobDefinitionId}")
    public Response deleteJob(@PathParam("jobDefinitionId") String jobDefinitionId) {

        ServiceResult result = this.jobDefinitionService.delete(jobDefinitionId);
        boolean hasError = result.hasError();
        if (hasError) {
            return Response.status(Response.Status.NOT_FOUND)
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