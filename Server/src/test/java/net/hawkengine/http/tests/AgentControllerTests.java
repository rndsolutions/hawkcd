package net.hawkengine.http.tests;

import com.google.gson.Gson;
import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.AgentController;
import net.hawkengine.model.Agent;
import net.hawkengine.model.Environment;
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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static org.junit.Assert.assertEquals;


public class AgentControllerTests extends JerseyTest {
    private static AgentService agentService;
    private static Gson gson;
    private Agent agent;

    //TODO: implement getWork_agentAssigned_workInfo();


    public Application configure() {
        return new ResourceConfig(AgentController.class);
    }

    @BeforeClass
    public static void login() throws IOException, URISyntaxException {
        RedisManager.initializeEmbededDb(6379);
        RedisManager.connect("redis");
        // RedisManager.startEmbededDb();
        gson = new Gson();
        agentService = new AgentService();
    }

    @AfterClass
    public static void logout() throws InterruptedException {
        RedisManager.release();
        RedisManager.stopEmbededDb();
    }

    @Test
    public void getAllAngets_request_emptyList() {
        //Arrange
        List expectedResult = new ArrayList();

        //Act
        Response response = target("/agents").request().get();
        List<Agent> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getById_request_oneObject() throws IOException {
        //Arrange
        this.prepareAgent();
        agentService.add(this.agent);

        //Act
        Response response = target("/agents/" +this.agent.getId()).request().get();
        Agent actualResult = response.readEntity(Agent.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.agent.getId(), actualResult.getId());
        this.removeAgent();
    }

    @Test
    public void getById_wrongIdRequest_errorMessage() throws IOException {
        //Arrange
        String expectedMessage = "Agent not found.";

        //Act
        Response response = target("/agents/wrongId").request().get();

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedMessage, response.readEntity(String.class));
    }

    @Test
    public void getWork_agentObject_noJobAssigned() {
        //Arrange
        this.prepareAgent();
        agentService.add(this.agent);
        String expectedResult = "This agent has no job assigned.";

        //Act
        Response response = target("/agents/"+this.agent.getId()+"/work").request().get();
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult,actualResult);
        this.removeAgent();
    }



    @Test
    public void addAgent_oneAgent_successMessage() {
        //Arrange
        this.prepareAgent();
        Entity entity = Entity.entity(this.agent, MediaType.APPLICATION_JSON);

        //Act
        Response response = target("/agents").request().post(entity);
        Agent actualAgent = response.readEntity(Agent.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.agent.getId(), actualAgent.getId());
        this.removeAgent();
    }

    @Test
    public void addAgent_invalidField_errorMessage() {
        //Arrange
        this.prepareAgent();
        this.agent.setName(null);
        Entity entity = Entity.entity(this.agent, MediaType.APPLICATION_JSON);
        String expectedResult = "ERROR: AGENT NAME IS NULL.";

        //Act
        Response response = target("/agents").request().post(entity);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult,response.readEntity(String.class));
    }

    @Test
    public void updateAgent_existingAgent_updatedAgent() {
        //Arrange
        /* adding agent to db */
        this.prepareAgent();
        agentService.add(this.agent);
        /* updating agent's name and encapsulating into entity */
        this.agent.setName("updatedAgent");
        Entity entity = Entity.entity(this.agent,"application/json");

        //Act
        Response response = target("/agents/" + this.agent.getId()).request().put(entity);
        Agent actualResult = response.readEntity(Agent.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.agent.getId(), actualResult.getId());
        this.removeAgent();
    }

    @Test
    public void updateAgent_nonExistingAgent_errorMessage() {
        //Arrange
        this.prepareAgent();
        Entity entity = Entity.entity(this.agent,"application/json");
        String expectedMessage = "Agent not found.";

        //Act
        Response response = target("/agents/").request().put(entity);
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void deleteAgent_agentObject_successMessage(){
        //Arrange
        this.prepareAgent();
        agentService.add(this.agent);

        //Act
        Response response = target("/agents/"+this.agent.getId()).request().delete();

        //Assert
        assertEquals(204,response.getStatus());

    }

    @Test
    public void deleteAgent_nonExistingAgent_errorMessage(){
        //Arrange
        String expectedResult = "Agent not found.";

        //Act
        Response response = target("/agents/nonExistingId").request().delete();
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(404,response.getStatus());
        assertEquals(expectedResult,actualResult);

    }

    private void prepareAgent(){
        this.agent = new Agent();
        this.agent.setHostName("localhost");
        this.agent.setIpAddress("127.0.0.1");
        this.agent.setRootPath("root");
        this.agent.setName("Blond007");
        this.agent.setOperatingSystem("android");
        this.agent.setEnvironment(new Environment());
    }

    private void removeAgent(){
        agentService.delete(this.agent.getId());
    }
}
