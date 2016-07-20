package net.hawkengine.http;

import net.hawkengine.services.EnvironmentService;
import net.hawkengine.services.interfaces.IEnvironmentService;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/environments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EnvironmentController {
    IEnvironmentService environmentService;

    public EnvironmentController() {
        this.environmentService = new EnvironmentService();
    }


}
