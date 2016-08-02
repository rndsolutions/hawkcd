package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.UserGroup;
import net.hawkengine.services.UserGroupService;
import net.hawkengine.services.interfaces.IUserGroupService;
import net.hawkengine.services.interfaces.IValidator;

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

@Path("/user-groups")
@Consumes("application/json")
@Produces("application/json")
public class UserGroupController {
    private IUserGroupService userGroupService;
    private IValidator validator;

    public UserGroupController() {
        this.userGroupService = new UserGroupService();
        this.validator = new SchemaValidator();
    }

    public UserGroupController(IUserGroupService userGroupService) {
        this.userGroupService = userGroupService;
        this.validator = new SchemaValidator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUserGroups() {

        ServiceResult result = this.userGroupService.getAll();

        return Response.status(Status.OK).entity(result.getObject()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getUserGroupById(@PathParam("id") String id) {
        ServiceResult result = this.userGroupService.getById(id);
        if (result.hasError()) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        return Response.status(Status.OK).entity(result.getObject()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewUserGroup(UserGroup userGroup) {
        String isValid = this.validator.validate(userGroup);
        if (isValid.equals("OK")) {
            ServiceResult result = this.userGroupService.add(userGroup);
            if (result.hasError()) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            }

            return Response.status(Status.CREATED).entity(result.getObject()).build();

        } else {
            return Response.status(Status.BAD_REQUEST).
                    entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserGroup(UserGroup userGroup) {
        String isValid = this.validator.validate(userGroup);
        if (isValid.equals("OK")) {
            ServiceResult result = this.userGroupService.update(userGroup);
            if (result.hasError()) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            }

            return Response.status(Status.OK).entity(result.getObject()).build();

        } else {
            return Response.status(Status.BAD_REQUEST).
                    entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response deleteUserGroup(@PathParam("id") String id) {
        ServiceResult result = this.userGroupService.delete(id);
        if (result.hasError()) {
            return Response.status(Status.NOT_FOUND)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        return Response.status(Status.NO_CONTENT).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{groupId}")
    public Response addUserToUserGroup(@PathParam("groupId") String groupId, String userId) {
        ServiceResult result = this.userGroupService.addUserToGroup(userId,groupId);
        if (result.hasError()) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        return Response.status(Status.OK).entity(result.getObject()).build();
    }


    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{groupId}/{userId}")
    public Response removeUserFromUserGroup(@PathParam("groupId") String groupId,@PathParam("userId") String userId) {
        ServiceResult result = this.userGroupService.removeUserFromGroup(userId,groupId);
        if (result.hasError()) {
            return Response.status(Status.BAD_REQUEST)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        return Response.status(Status.NO_CONTENT).build();
    }

}
