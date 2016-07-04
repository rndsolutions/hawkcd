package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.JobDefinitionService;
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
        this.serviceResult = this.jobDefinitionService.getAll();
        List<String> stagesList = (List<String>) this.serviceResult.getObject();
        return Response.ok().entity(stagesList)
                .entity(this.serviceResult.getMessage())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{jobDefinitionId}")
    public Response getJobDefinitionById(@PathParam("jobDefinitionId")
                                                   String jobDefinitionId) {
        boolean hasError = this.serviceResult.hasError();
        if (hasError) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(this.serviceResult.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        } else {
            this.serviceResult = this.jobDefinitionService.getById(jobDefinitionId);
            return Response.ok().entity(this.serviceResult.getObject())
                    .entity(this.serviceResult.getMessage())
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewJob(JobDefinition jobDefinition) {
        if (this.schemaValidator.validate(jobDefinition).equals("OK")) {
            this.serviceResult = this.jobDefinitionService.add(jobDefinition);
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
    public Response updateJob(JobDefinition jobDefinition) {
        if (this.schemaValidator.validate(jobDefinition).equals("OK")) {
            this.serviceResult = this.jobDefinitionService.update(jobDefinition);
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
    @Path("/{jobDefinitionId}")
    public Response deleteJob(@PathParam("jobDefinitionId") String jobDefinitionId) {
        this.serviceResult = this.jobDefinitionService.delete(jobDefinitionId);
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