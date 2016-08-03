package net.hawkengine.http;

import com.google.gson.Gson;

import net.hawkengine.core.utilities.deserializers.TokenAdapter;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.dto.LoginDto;

import net.hawkengine.model.dto.RegisterDto;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/auth")
public class AuthController {
    private UserService userService;

    public AuthController(){
        this.userService =  new UserService();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    @Path("/login")
    public Response login(LoginDto login) throws IOException {
        String hashedPassword = DigestUtils.sha256Hex(login.getPassword());
        ServiceResult serviceResult = this.userService.getByEmailAndPassword(login.getEmail(), hashedPassword);
        if (serviceResult.hasError()){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(serviceResult)
                    .build();
        }

        User userFromDb = (User) serviceResult.getObject();

        String token = TokenAdapter.createJsonWebToken(userFromDb, 30L);

        Gson gson = new Gson();

        String jsonToken = gson.toJson(token);

        return Response.ok(jsonToken).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Response register(RegisterDto newUser){

        List<Permission> userPermissions = new ArrayList<>();

        User user =  new User();
        user.setEmail(newUser.getEmail());
        user.setPassword(newUser.getPassword());

        //TODO: move this to update user, should not be here, left till adminUsers is ready
        if (newUser.getPermissions() != null) {
            userPermissions.addAll(newUser.getPermissions());
        }
        user.setPermissions(userPermissions);

        ServiceResult serviceResult = this.userService.addUserWithoutProvider(user);

        if (serviceResult.hasError()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(serviceResult)
                    .build();
        } else {
            return Response.status(Response.Status.CREATED)
                    .entity(serviceResult)
                    .build();
        }
    }



}
