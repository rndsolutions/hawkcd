package io.hawkcd.http.tests;


import io.hawkcd.Config;
import io.hawkcd.http.PipelineGroupController;
import io.hawkcd.model.PipelineGroup;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.PipelineGroupService;
import io.hawkcd.services.interfaces.IPipelineGroupService;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PipelineGroupControllerTests extends JerseyTest {
    IPipelineGroupService pipelineGroupService;
    PipelineGroupController pipelineGroupController;
    PipelineGroup pipelineGroup;
    ServiceResult serviceResult;

    @BeforeClass
    public static void setUpClass() {
        Config.configure();
    }

    public Application configure() {
        this.pipelineGroupService = Mockito.mock(PipelineGroupService.class);
        this.pipelineGroupController = new PipelineGroupController(this.pipelineGroupService);
        this.serviceResult = new ServiceResult();
        return new ResourceConfig().register(this.pipelineGroupController);
    }

    @Test
    public void pipelineGroupController_constructorTest_notNull() {

        PipelineGroupController pipelineGroupController = new PipelineGroupController();

        assertNotNull(pipelineGroupController);
    }

    @Test
    public void getAllPipelineGroups_nonExistingObjects_emptyList() {
        //Arrange
        List<PipelineGroup> expectedResult = new ArrayList<>();
        this.serviceResult.setEntity(expectedResult);
        Mockito.when(this.pipelineGroupService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/pipeline-groups").request().get();
        List<PipelineGroup> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.size(), actualResult.size());
    }

    @Test
    public void getAllPipelineGroups_existingObjects_twoObjects() {
        //Arrange
        List<PipelineGroup> expectedResult = new ArrayList<>();
        this.pipelineGroup = new PipelineGroup();
        expectedResult.add(this.pipelineGroup);
        expectedResult.add(this.pipelineGroup);
        this.serviceResult.setEntity(expectedResult);
        Mockito.when(this.pipelineGroupService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/pipeline-groups").request().get();
        List<PipelineGroup> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.size(), actualResult.size());
    }

    @Test
    public void getPipelineGroupById_existingObject_correctObject() {
        //Arrange
        this.pipelineGroup = new PipelineGroup();
        this.serviceResult.setEntity(this.pipelineGroup);
        Mockito.when(this.pipelineGroupService.getById(Mockito.anyString())).thenReturn(this.serviceResult);
        PipelineGroup expectedResult = this.pipelineGroup;

        //Act
        Response response = target("/pipeline-groups/" + this.pipelineGroup.getId()).request().get();
        PipelineGroup actualResult = response.readEntity(PipelineGroup.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.getId(), actualResult.getId());
    }

    @Test
    public void getPipelineGroupById_nonExistingObject_properErrorMessage() {
        //Arrange
        String expectedResult = "PipelineGroup not found.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setNotificationType(NotificationType.ERROR);
        this.serviceResult.setEntity(null);
        Mockito.when(this.pipelineGroupService.getById(Mockito.any())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/pipeline-groups/wrongId").request().get();
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void addPipelineGroup_oneObject_successMessage() {
        //Arrange
        this.preparePipelineGroup();
        this.serviceResult.setEntity(this.pipelineGroup);
        Mockito.when(this.pipelineGroupService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.pipelineGroup, "application/json");
        PipelineGroup expectedResult = this.pipelineGroup;

        //Act
        Response response = target("/pipeline-groups").request().post(entity);
        PipelineGroup actualResult = response.readEntity(PipelineGroup.class);

        //Assert
        assertEquals(201, response.getStatus());
        assertEquals(expectedResult.getId(), actualResult.getId());
    }

    @Test
    public void addPipelineGroup_invalidField_properErrorMessage() {
        //Arrange
        this.preparePipelineGroup();
        this.pipelineGroup.setName("#$!$$@#");
        String expectedResult = "ERROR: PIPELINEGROUP NAME IS INVALID.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setNotificationType(NotificationType.ERROR);
        this.serviceResult.setEntity(this.pipelineGroup);
        Mockito.when(this.pipelineGroupService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.pipelineGroup, "application/json");

        //Act
        Response response = target("/pipeline-groups").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void addPipelineGroup_existingObject_properErrorMessage() {
        //Arrange
        this.preparePipelineGroup();
        String expectedResult = "PipelineGroup already exists.";
        this.serviceResult.setNotificationType(NotificationType.ERROR);
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setEntity(null);
        Mockito.when(this.pipelineGroupService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.pipelineGroup, "application/json");

        //Act
        Response response = target("/pipeline-groups").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    /*
        TODO: service that checks for name collision to be implemented.
        @Test
        public void addPipelineGroup_withSameName_properErrorMessage() {
            //Arrange
            this.preparePipelineGroup();
            this.serviceResult.setObject(null);
            this.serviceResult.setError(true);
            String expectedResult = "PipelineGroup with the same name exists.";
            this.serviceResult.setMessage(expectedResult);
            Mockito.when(this.pipelineGroupService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
            Entity entity = Entity.entity(this.pipelineGroup, "application/json");

            //Act
            Response response = target("/pipeline-groups/").request().post(entity);
            String actualResult = response.readEntity(String.class);

            //Assert
            assertEquals(400, response.getStatus());
            assertEquals(expectedResult, actualResult);

        }
    */

    @Test
    public void updatePipelineGroup_existingPipelineGroup_updatedPipelineGroup() {
        //Arrange
        this.preparePipelineGroup();
        this.serviceResult.setEntity(this.pipelineGroup);
        this.pipelineGroup.setName("name-updated");
        Mockito.when(this.pipelineGroupService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.pipelineGroup, "application/json");
        PipelineGroup expectedResult = this.pipelineGroup;

        //Act
        Response response = target("/pipeline-groups").request().put(entity);
        PipelineGroup actualResult = response.readEntity(PipelineGroup.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.getName(), actualResult.getName());
    }

    @Test
    public void updatePipelineGroup_nonExistingPipelineGroup_properErrorMessage() {
        //Arrange
        this.preparePipelineGroup();
        String expectedMessage = "PipelineGroup not found.";
        this.serviceResult.setNotificationType(NotificationType.ERROR);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.pipelineGroupService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.pipelineGroup, "application/json");

        //Act
        Response response = target("/pipeline-groups").request().put(entity);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, response.readEntity(String.class));
    }

    /*
        TODO: service that checks for name collision to be implemented.
        @Test
        public void updatePipelineGroup_withSameName_properErrorMessage() {
            //Arrange
            this.preparePipelineGroup();
            this.serviceResult.setObject(null);
            this.serviceResult.setError(true);
            String expectedResult = "PipelineGroup with the same name exists.";
            this.serviceResult.setMessage(expectedResult);
            Mockito.when(this.pipelineGroupService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
            Entity entity = Entity.entity(this.pipelineGroup, "application/json");

            //Act
            Response response = target("/pipeline-groups/").request().post(entity);
            String actualResult = response.readEntity(String.class);

            //Assert
            assertEquals(400, response.getStatus());
            assertEquals(expectedResult, actualResult);

        }
    */

    @Test
    public void updatePipelineGroup_invalidField_properErrorMessage() {
        //Arrange
        this.preparePipelineGroup();
        this.pipelineGroup.setName(null);
        String expectedResult = "ERROR: PIPELINEGROUP NAME IS NULL.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setNotificationType(NotificationType.ERROR);
        this.serviceResult.setEntity(this.pipelineGroup);
        Mockito.when(this.pipelineGroupService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.pipelineGroup, "application/json");

        //Act
        Response response = target("/pipeline-groups").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void deletePipelineGroup_pipelineGroup_successMessage() {
        //Arrange
        this.preparePipelineGroup();
        Mockito.when(this.pipelineGroupService.delete(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/pipeline-groups/" + this.pipelineGroup.getId()).request().delete();

        //Assert
        assertEquals(204, response.getStatus());
    }

    @Test
    public void deletePipelineGroup_nonPipelineGroup_errorMessage() {
        //Arrange
        String expectedMessage = "PipelineGroup not found.";
        this.serviceResult.setNotificationType(NotificationType.ERROR);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.pipelineGroupService.delete(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/pipeline-groups/wrongId").request().delete();
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }

    private void preparePipelineGroup() {
        this.pipelineGroup = new PipelineGroup();
        this.pipelineGroup.setName("pipelineGroup");
    }
}
