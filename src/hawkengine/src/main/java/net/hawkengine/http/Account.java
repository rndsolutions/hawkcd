package net.hawkengine.http;

import net.hawkengine.model.dto.UserInfoDto;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("account")
public class Account {
    @GET
    @Path("test")
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfoDto getUserInfo() {

        UserInfoDto usr = new UserInfoDto();
        usr.setEmail("rado@rnd.com");
        usr.setLoginProvider("no idea");
        usr.setRegistered(false);

        return usr; // usr.toString();
    }
}
