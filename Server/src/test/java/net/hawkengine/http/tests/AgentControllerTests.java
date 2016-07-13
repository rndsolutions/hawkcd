package net.hawkengine.http.tests;
import net.hawkengine.http.AgentController;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;


public class AgentControllerTests extends JerseyTest {

    public Application configure(){
        return new ResourceConfig(AgentController.class);
    }

    @Test
    public void getAllAgentsTest(){
        Response response = target("/agents").request().get();
        System.out.println(response);
    }
}
