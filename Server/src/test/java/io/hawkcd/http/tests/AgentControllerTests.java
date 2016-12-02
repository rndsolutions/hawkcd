package io.hawkcd.http.tests;

import io.hawkcd.core.config.Config;
import io.hawkcd.http.AgentController;
import io.hawkcd.model.Agent;
import io.hawkcd.model.Environment;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.AgentService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AgentControllerTests extends JerseyTest {
    private AgentService agentService;
    private AgentController agentController;
    private Agent agent;
    private ServiceResult serviceResult;

    @BeforeClass
    public static void setUpClass() {
        Config.configure();
    }

    public Application configure() {
        this.agentService = Mockito.mock(AgentService.class);
        this.agentController = new AgentController(this.agentService);
        this.serviceResult = new ServiceResult();
        return new ResourceConfig().register(this.agentController);
    }

    @Test
    public void testAgentController_constructorTest_notNull() {

        AgentController agentController = new AgentController();

        assertNotNull(agentController);
    }

    @Test
    public void getAllAgents_nonExistingAgents_emptyList() {
        //Arrange
        List<Agent> expectedResult = new ArrayList<>();
        this.serviceResult.setEntity(expectedResult);
        Mockito.when(this.agentService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/agents").request().get();
        List<Agent> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.size(), actualResult.size());
    }

    @Test
    public void getAllAgents_existingObjects_twoAgents() {
        //Arrange
        List<Agent> expectedResult = new ArrayList<>();
        expectedResult.add(this.agent);
        expectedResult.add(this.agent);
        this.serviceResult.setEntity(expectedResult);
        Mockito.when(this.agentService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/agents").request().get();
        List<Agent> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.size(), actualResult.size());
    }

    @Test
    public void getAgentById_oneObject_correctObject() {
        //Arrange
        this.prepareAgent();
        this.serviceResult.setEntity(this.agent);
        Mockito.when(this.agentService.getById(Mockito.anyString())).thenReturn(this.serviceResult);
        Agent expectedResult = this.agent;

        //Act
        Response response = target("/agents/" + this.agent.getId()).request().get();
        Agent actualResult = response.readEntity(Agent.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.getId(), actualResult.getId());
    }

    @Test
    public void getAgentById_nonExistingAgent_properErrorMessage() {
        //Arrange
        String expectedResult = "Agent not found.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setNotificationType(NotificationType.ERROR);
        this.serviceResult.setEntity(null);
        Mockito.when(this.agentService.getById(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/agents/wrongId").request().get();
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    //TODO: implement getWork_agentAssigned_workInfo();

    @Test
    public void getWork_agentObject_noJobAssigned() {
        //Arrange
        this.prepareAgent();
        String expectedResult = "This agent has no job assigned.";
        this.serviceResult.setEntity(expectedResult);
        Mockito.when(this.agentService.getWorkInfo(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/agents/" + this.agent.getId() + "/work").request().get();
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void addAgent_oneAgent_successMessage() {
        //Arrange
        this.prepareAgent();
        this.serviceResult.setEntity(this.agent);
        Mockito.when(this.agentService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.agent, MediaType.APPLICATION_JSON);
        Agent expectedResult = this.agent;

        //Act
        Response response = target("/agents").request().post(entity);
        Agent actualAgent = response.readEntity(Agent.class);

        //Assert
        assertEquals(201, response.getStatus());
        assertEquals(expectedResult.getId(), actualAgent.getId());
    }

    @Test
    public void addAgent_invalidField_properEerrorMessage() {
        //Arrange
        this.prepareAgent();
        this.agent.setName(null);
        this.serviceResult.setEntity(this.agent);
        Mockito.when(this.agentService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.agent, MediaType.APPLICATION_JSON);
        String expectedResult = "ERROR: AGENT NAME IS NULL.";

        //Act
        Response response = target("/agents").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    /*
    TODO: service that checks for name collision to be implemented.
    @Test
    public void addPipelineDefinition_withSameName_properErrorMessage() {
        //Arrange
        this.prepareAgent();
        String expectedResult = "Agent with same name already exists.";
        this.serviceResult.setObject(null);
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedResult);
        Mockito.when(this.agentService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.agent, "application/json");

        //Act
        Response response = target("agents/").request().put(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult,actualResult);
    }
    */

    @Test
    public void addAgent_existingaAgent_properErrorMessage() {
        //Arrange
        this.prepareAgent();
        String expectedResult = "Agent already exists.";
        this.serviceResult.setNotificationType(NotificationType.ERROR);
        this.serviceResult.setEntity(null);
        this.serviceResult.setMessage(expectedResult);
        Mockito.when(this.agentService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.agent, "application/json");

        //Act
        Response response = target("/agents").request().post(entity);
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualMessage);
    }

    @Test
    public void updateAgent_existingAgent_updatedAgent() {
        //Arrange
        this.prepareAgent();
        this.serviceResult.setEntity(this.agent);
        this.agent.setName("updatedAgent");
        Mockito.when(this.agentService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.agent, "application/json");
        Agent expectedResult = this.agent;

        //Act
        Response response = target("/agents/").request().put(entity);
        Agent actualResult = response.readEntity(Agent.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.getId(), actualResult.getId());
    }

    @Test
    public void updateAgent_nonExistingAgent_properErrorMessage() {
        //Arrange
        this.prepareAgent();
        String expectedResult = "Agent not found.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setEntity(null);
        this.serviceResult.setNotificationType(NotificationType.ERROR);
        Mockito.when(this.agentService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.agent, "application/json");

        //Act
        Response response = target("/agents/").request().put(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void updateAgent_invalidField_properErrorMessage() {
        //Arrange
        this.prepareAgent();
        this.agent.setName(null);
        this.serviceResult.setEntity(this.agent);
        Mockito.when(this.agentService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.agent, MediaType.APPLICATION_JSON);
        String expectedResult = "ERROR: AGENT NAME IS NULL.";

        //Act
        Response response = target("/agents").request().put(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }


//    @Test
//    public void deleteAgent_agentObject_successMessage() {
//        //Arrange
//        this.prepareAgent();
//        Mockito.when(this.agentService.delete(Mockito.anyString())).thenReturn(this.serviceResult);
//
//        //Act
//        Response response = target("/agents/" + this.agent.getId()).request().delete();
//
//        //Assert
//        assertEquals(204, response.getStatus());
//    }

//    @Test
//    public void deleteAgent_nonExistingAgent_errorMessage() {
//        //Arrange
//        String expectedResult = "Agent not found.";
//        this.serviceResult.setNotificationType(NotificationType.ERROR);
//        this.serviceResult.setMessage(expectedResult);
//        Mockito.when(this.agentService.delete(Mockito.anyString())).thenReturn(this.serviceResult);
//
//        //Act
//        Response response = target("/agents/nonExistingAgent").request().delete();
//        String actualResult = response.readEntity(String.class);
//
//        //Assert
//        assertEquals(404, response.getStatus());
//        assertEquals(expectedResult, actualResult);
//
//    }

    private void prepareAgent() {
        this.agent = new Agent();
        this.agent.setHostName("localhost");
        this.agent.setIpAddress("127.0.0.1");
        this.agent.setRootPath("root");
        this.agent.setName("Blond007");
        this.agent.setOperatingSystem("android");
        this.agent.setEnvironment(new Environment());
    }
}
