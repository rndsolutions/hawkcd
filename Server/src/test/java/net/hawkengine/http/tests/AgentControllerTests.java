package net.hawkengine.http.tests;
import com.google.gson.Gson;


import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.AgentController;
import net.hawkengine.model.Agent;
import net.hawkengine.services.AgentService;


import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Application;;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;


public class AgentControllerTests extends JerseyTest {
    private static  AgentService agentService;
    private static Gson gson;


    public Application configure(){

        return new ResourceConfig(AgentController.class);
    }

    @BeforeClass public static void login() throws IOException, URISyntaxException {

        RedisManager.initializeEmbededDb(6379);
        RedisManager.connect("localhost");
        RedisManager.startEmbededDb();
        gson = new Gson();
        agentService = new AgentService();
    }

    @AfterClass public static void logout() throws InterruptedException {
        RedisManager.release();
        RedisManager.stopEmbededDb();

    }

    @Test
    public void getAllAngets_request_emptyList(){
        //Arrange
        List expectedResult = new ArrayList();

        //Act
        List<Agent> actualResult = target("/agents").request().get(List.class);


        //Assert
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void getAllAgents_request_oneObject() throws IOException {
        //Arrange
        Agent expectedAgent = new Agent();

        //Act
        Response response = target("/agents").request().get();
        Agent actualAgent = gson.fromJson(String.valueOf(response.readEntity(List.class).get(0)),Agent.class);

        //Assert




    }


}
