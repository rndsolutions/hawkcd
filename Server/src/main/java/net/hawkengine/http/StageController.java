package net.hawkengine.http;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.Stage;
import net.hawkengine.services.StageService;
import net.hawkengine.services.interfaces.IStageService;

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
@Path("/stages")
public class StageController {
    private IStageService stageService;

    public StageController() {
        this.stageService = new StageService();
    }

    public StageController(IStageService stageService) {
        this.stageService = stageService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllStages() {
        ServiceResult response = this.stageService.getAll();
        return Response.status(Status.OK).entity(response.getObject()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{stageId}")
    public Response getStageById(@PathParam("stageId") String stageId) {
        ServiceResult response = this.stageService.getById(stageId);
        if (response.hasError()) {
            return Response.status(Status.NOT_FOUND).entity(response.getMessage()).build();
        }
        return Response.status(Status.OK).entity(response.getObject()).build();
    }

    /*
        @GET
        @Path("/{stageId}/latest")
        public Response getLatest(@PathParam("stageId") String stageId) {
            // TODO: service to be implemented.
           return Response.noContent().build();
        }
    */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewStage(Stage stage) {
        ServiceResult result = this.stageService.add(stage);
        if (result.hasError()) {
            return Response.status(Status.BAD_REQUEST).entity(result.getMessage()).build();
        }
        return Response.status(Status.CREATED).entity(result.getObject()).build();
    }

    /*
    @POST
    public Response addSpecific(){}
    TODO: service to be implemented.
    */
}
