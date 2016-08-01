package net.hawkengine.http;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.JobService;
import net.hawkengine.services.interfaces.IJobService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/jobs")
public class JobController {
    private IJobService jobService;

    public JobController() {
        this.jobService = new JobService();
    }

    public JobController(IJobService jobService) {
        this.jobService = jobService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllJobs() {
        ServiceResult response = this.jobService.getAll();
        return Response.status(Status.OK).entity(response.getObject()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{jobId}")
    public Response getJobById(@PathParam("jobId") String stageId) {
        ServiceResult response = this.jobService.getById(stageId);
        if (response.hasError()) {
            return Response.status(Status.NOT_FOUND).entity(response.getMessage()).build();
        }
        return Response.status(Status.OK).entity(response.getObject()).build();
    }
/*
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{jobId}/latest")
    public Response getLatest(@PathParam("jobId") String stageId) {
        // TODO: service to be implemented.
        return Response.noContent().build();
    }
    */
}
