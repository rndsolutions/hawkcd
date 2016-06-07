package net.hawkengine.http;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import net.hawkengine.model.dto.UserInfoDto;


@Path("account")
public class Account {

	@GET
	@Path("test")
	@Produces(MediaType.APPLICATION_JSON)
	public UserInfoDto getUserInfo() {

		UserInfoDto usr = new UserInfoDto();
		usr.email = "rado@rnd.com";
		usr.loginProvider = "no idea";
		usr.hasRegistered = false;

		return usr; // usr.toString();

	}

}
