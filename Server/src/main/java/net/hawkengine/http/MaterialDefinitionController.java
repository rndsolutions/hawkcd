package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.MaterialDefinitionService;
import net.hawkengine.services.interfaces.IMaterialDefinitionService;

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

@Consumes("application/json")
@Produces("application/json")
@Path("/materials")
public class MaterialDefinitionController {

    private IMaterialDefinitionService materialDefinitionService;
    private SchemaValidator schemaValidator;

    public MaterialDefinitionController() {
        this.materialDefinitionService = new MaterialDefinitionService();
        this.schemaValidator = new SchemaValidator();
    }

    public MaterialDefinitionController(IMaterialDefinitionService materialDefinitionService) {
        this.materialDefinitionService = materialDefinitionService;
        this.schemaValidator = new SchemaValidator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMaterialDefinitions() {
        ServiceResult result = this.materialDefinitionService.getAll();
        return Response.status(Response.Status.OK).entity(result.getObject()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{materialDefinitionId}")
    public Response getMaterialDefinitionById(@PathParam("materialDefinitionId")
                                                      String materialDefinitionId) {
        ServiceResult result = this.materialDefinitionService.getById(materialDefinitionId);

        if (result.hasError()) {
            return Response.status(Status.NOT_FOUND).entity(result.getMessage()).build();
        }

        return Response.status(Status.OK).entity(result.getObject()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addMaterialDefinition(MaterialDefinition materialDefinition) {
        String isValid = this.schemaValidator.validate(materialDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.materialDefinitionService.addMaterialDefinition(materialDefinition);
            if (result.hasError()) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            }

            return Response.status(Status.CREATED).entity(result.getObject()).build();

        } else {
            return Response.status(Status.BAD_REQUEST)
                    .entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMaterialDefinition(MaterialDefinition materialDefinition) {
        String isValid = this.schemaValidator.validate(materialDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.materialDefinitionService.updateMaterialDefinition(materialDefinition);
            if (result.hasError()) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            }

            return Response.status(Status.OK).entity(result.getObject()).build();

        } else {
            return Response.status(Status.BAD_REQUEST)
                    .entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{materialDefinitionId}")
    public Response deleteMaterialDefinition(@PathParam("materialDefinitionId")
                                                     String materialDefinitionId) {
        ServiceResult result = this.materialDefinitionService.delete(materialDefinitionId);
        if (result.hasError()) {
            return Response.status(Status.NOT_FOUND).entity(result.getMessage()).build();
        }

        return Response.status(Status.NO_CONTENT).build();
    }


}