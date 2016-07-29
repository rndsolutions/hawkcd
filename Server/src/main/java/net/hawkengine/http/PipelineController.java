package net.hawkengine.http;

import net.hawkengine.model.Pipeline;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Consumes("application/json")
@Produces("application/json")
@Path("/pipelines")
public class PipelineController {
    private IPipelineService pipelineService;

    public PipelineController() {

        this.pipelineService = new PipelineService();
    }

    public PipelineController(IPipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPipelines() {
        ServiceResult response = this.pipelineService.getAll();
        return Response.status(Status.OK)
                .entity(response.getObject())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{pipelineDefinitionId}")
    public Response getPipelineById(@PathParam("pipelineDefinitionId") String pipelineDefinitionId) {
        ServiceResult response = this.pipelineService.getById(pipelineDefinitionId);
        if (response.hasError()) {
            return
            Response.status(Status.NOT_FOUND)
                    .entity(response.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        } else {
            return Response.status(Status.OK).entity(response.getObject()).build();
        }
    }

    /*
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{pipelineDefinitionId}/{pipelineId}")
    public Response getSpecificPipelineId(){
        return null;
    }
    TODO: service to be implemented.

    */

    /*
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{pipelineDefinitionId}/latest")
    public Response getLatest(){
        return null;
    }
    TODO: service to be implemented.

    */

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPipeline(Pipeline pipeline){
        ServiceResult response = this.pipelineService.add(pipeline);

        if (response.hasError()){
            return Response.status(Status.BAD_REQUEST)
                    .entity(response.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        } else {
         return Response.status(Status.CREATED)
                 .entity(response.getObject())
                 .build();
        }
    }


    /*
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{pipelineDefinitionId}/start-specific")
    public Response startSpecific(){
        return null;
    }
    TODO: service to be implemented.

    */
}
