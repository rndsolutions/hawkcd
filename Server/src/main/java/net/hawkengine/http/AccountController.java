package net.hawkengine.http;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.services.UserService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


//TODO: implement scheme validation

@Consumes("application/json")
@Produces("application/json")
@Path("/account")
public class AccountController {

    private UserService usrService;

    public AccountController() {
        this.usrService = new UserService();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/users")
    public Response getUsers() {
        ServiceResult result = this.usrService.getAll();
        return Response.status(Status.OK)
                .entity(result.getObject())
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/users")
    public Response addUser(User user) {

        ServiceResult result = this.usrService.add(user);

        if (result.hasError()){
            return Response.status(Status.BAD_REQUEST)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        }else{
            return Response.status(Status.CREATED)
                    .entity(result.getObject())
                    .build();
        }
    }

}
