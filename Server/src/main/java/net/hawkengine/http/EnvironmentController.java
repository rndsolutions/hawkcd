package net.hawkengine.http;

import net.hawkengine.model.Environment;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.EnvironmentService;
import net.hawkengine.services.interfaces.IEnvironmentService;

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

@Path("/environments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EnvironmentController {
    IEnvironmentService environmentService;

    public EnvironmentController() {
        this.environmentService = new EnvironmentService();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllEnvironments() {
        ServiceResult result = this.environmentService.getAll();

        return Response.status(Status.OK).entity(result.getObject()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEnvironment(Environment environment) {
        ServiceResult result = this.environmentService.add(environment);

        if (result.hasError()) {
            return Response.status(Status.BAD_REQUEST).entity(result.getMessage()).build();
        }

        return Response.status(Status.CREATED).entity(result.getObject()).build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{environmentId}")
    public Response deleteEnvironment(@PathParam("environmentId") String environmentId) {
        ServiceResult result = this.environmentService.delete(environmentId);

        if (result.hasError()) {
            return Response.status(Status.NOT_FOUND).entity(result.getMessage()).build();
        }

        return Response.status(Status.NO_CONTENT).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateEnvironments(Environment environment) {
        ServiceResult result = this.environmentService.update(environment);

        if (result.hasError()) {
            return Response.status(Status.NOT_FOUND).entity(result.getMessage()).build();
        }

        return Response.status(Status.OK).entity(result.getMessage()).build();
    }
}
