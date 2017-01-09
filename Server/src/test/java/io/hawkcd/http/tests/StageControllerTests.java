package io.hawkcd.http.tests;

import io.hawkcd.core.config.Config;
import io.hawkcd.http.StageController;
import io.hawkcd.model.Pipeline;
import io.hawkcd.model.PipelineDefinition;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.Stage;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.StageService;
import io.hawkcd.services.interfaces.IStageService;

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

public class StageControllerTests extends JerseyTest {
    private StageController stageController;
    private IStageService stageService;
    private Stage stage;
    private ServiceResult serviceResult;

    @BeforeClass
    public static void setUpClass() {
        Config.configure();
    }

    public Application configure() {
        this.stageService = Mockito.mock(StageService.class);
        this.stageController = new StageController(this.stageService);
        this.serviceResult = new ServiceResult();
        return new ResourceConfig().register(this.stageController);
    }

    @Test
    public void testStageController_constructorTest_notNull() {

        StageController stageController = new StageController();

        assertNotNull(stageController);
    }

    @Test
    public void getAllStages_nonExistingObjects_emptyList() {
        //Arrange
        List<Stage> expectedResult = new ArrayList<>();
        this.serviceResult.setEntity(expectedResult);
        Mockito.when(this.stageService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/stages").request().get();
        List<Stage> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.size(), actualResult.size());
    }

    @Test
    public void getAllStages_existingObjects_twoObjects() {
        //Arrange
        List<Stage> expectedResult = new ArrayList<>();
        this.stage = new Stage();
        expectedResult.add(this.stage);
        expectedResult.add(this.stage);
        this.serviceResult.setEntity(expectedResult);
        Mockito.when(this.stageService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/stages").request().get();
        List<Stage> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.size(), actualResult.size());
    }

    @Test
    public void getStageById_existingObject_correctObject() {
        //Arrange
        this.stage = new Stage();
        this.serviceResult.setEntity(this.stage);
        Mockito.when(this.stageService.getById(Mockito.anyString())).thenReturn(this.serviceResult);
        Stage expectedResult = this.stage;

        //Act
        Response response = target("/stages/" + this.stage.getId()).request().get();
        Stage actualResult = response.readEntity(Stage.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.getId(), actualResult.getId());
    }

    @Test
    public void getStageById_nonExistingObject_properErrorMessage() {
        //Arrange
        String expectedResult = "Stage not found.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setNotificationType(NotificationType.ERROR);
        this.serviceResult.setEntity(null);
        Mockito.when(this.stageService.getById(Mockito.any())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/stages/wrongId").request().get();
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void addNewStage_oneObject_successMessage() {
        //Arrange
        this.prepareStage();
        this.serviceResult.setEntity(this.stage);
        Mockito.when(this.stageService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.stage, "application/json");
        Stage expectedResult = this.stage;

        //Act
        Response response = target("/stages").request().post(entity);
        Stage actualResult = response.readEntity(Stage.class);

        //Assert
        assertEquals(201, response.getStatus());
        assertEquals(expectedResult.getId(), actualResult.getId());
    }

    private void prepareStage() {
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("pipelineDefinition");
        Pipeline pipeline = new Pipeline();
        pipeline.setPipelineDefinitionName(pipelineDefinition.getName());
        pipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        this.stage = new Stage();
        this.stage.setPipelineId(pipeline.getId());
    }
}
