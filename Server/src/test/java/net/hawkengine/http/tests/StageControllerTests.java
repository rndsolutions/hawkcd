package net.hawkengine.http.tests;

import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.StageController;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.Stage;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.StageDefinitionService;
import net.hawkengine.services.StageService;

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
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;


public class StageControllerTests extends JerseyTest {
    private static PipelineService pipelineService;
    private static PipelineDefinitionService pipelineDefinitionService;
    private static StageService stageService;
    private static StageDefinitionService stageDefinitionService;
    private Pipeline pipeline;
    private PipelineDefinition pipelineDefinition;
    private StageDefinition stageDefinition;
    private Stage stage;

    @BeforeClass
    public static void login() throws IOException, URISyntaxException {
        RedisManager.initializeEmbededDb(6379);
        RedisManager.connect("redis");
        pipelineService = new PipelineService();
        pipelineDefinitionService = new PipelineDefinitionService();
        stageService = new StageService();
        stageDefinitionService = new StageDefinitionService();

    }

    @AfterClass
    public static void logout() {
        RedisManager.release();
    }

    public Application configure() {
        return new ResourceConfig(StageController.class);
    }

    @Test
    public void getAllStages_request_emptyList() {
        //Arrange
        List<Stage> expectedResult = new ArrayList<>();

        //Act
        Response response = target("/stages").request().get();
        List<Stage> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getStageById_request_StageObject() {
        //Arrange
        this.prepareStage();
        stageService.add(this.stage);

        //Act
        Response response = target("/stages/" + this.stage.getId()).request().get();
        Stage actualResult = response.readEntity(Stage.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.stage.getId(), actualResult.getId());
        this.removeStage();
    }

    @Test
    public void getStageById_wrongId_errorMessage() {
        //Arrange
        this.prepareStage();
        stageService.add(this.stage);
        String expectedResult = "Stage not found.";

        //Act
        Response response = target("/stages/wrongId").request().get();
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedResult, actualResult);
        this.removeStage();
    }

    @Test
    public void addStage_stageRun_successMessage() {
        //Arrange
        this.prepareStage();
        Entity entity = Entity.entity(this.stage,"application/json");

        //Act
        Response response = target("/stages/").request().post(entity);
        Stage actualObject = response.readEntity(Stage.class);


        //Assert
        assertEquals(200,response.getStatus());
        assertEquals(this.stage.getId(),actualObject.getId());
             this.removeStage();
    }

    private void prepareStage() {
        this.pipelineDefinition = new PipelineDefinition();
        this.pipeline = new Pipeline();
        this.stageDefinition = new StageDefinition();
        this.stage = new Stage();
        this.pipelineDefinition.setName("pipelineDefinition");
        pipelineDefinitionService.add(this.pipelineDefinition);
        this.stage.setPipelineId(this.pipeline.getId());
        this.pipeline.setPipelineDefinitionName(this.pipelineDefinition.getName());
        this.pipeline.setPipelineDefinitionId(this.pipelineDefinition.getId());
        this.stageDefinition.setPipelineDefinitionId(pipeline.getPipelineDefinitionId());
        this.stage.setStageDefinitionId(this.stageDefinition.getId());
        pipelineDefinitionService.update(this.pipelineDefinition);
        pipelineService.add(this.pipeline);
        stageDefinitionService.add(this.stageDefinition);
    }

    private void removeStage() {
        pipelineService.delete(this.pipeline.getId());
        pipelineDefinitionService.delete(this.pipelineDefinition.getId());
    }
}
