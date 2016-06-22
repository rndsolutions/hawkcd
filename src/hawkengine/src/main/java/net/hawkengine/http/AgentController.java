package net.hawkengine.http;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.AgentService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/agents")
@Consumes("application/json")
@Produces("application/json")
public class AgentController {
    private AgentService agentService;
    private ServiceResult serviceResult;


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayPlainTextHello() {
        return "Hello, Friend!";
    }


    /*
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/agents/{agentId}")
    public Response getById(@PathParam("agentId") String id) {
        serviceResult = agentService.getById(id);
        if (serviceResult.hasError()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(serviceResult.getObject(), serviceResult.getMessage()).build();
    }
    */
}