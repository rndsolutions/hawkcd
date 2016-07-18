package net.hawkengine.http.tests;

import net.hawkengine.db.redis.RedisManager;

import net.hawkengine.http.StageDefinitionController;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.StageDefinitionService;
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

public class StageDefinitionControllerTests extends JerseyTest {
    private static StageDefinitionService stageDefinitionService;
    private StageDefinition stageDefinition;
    private PipelineDefinition pipelineDefinition;
    private static PipelineDefinitionService pipelineDefinitionService;

    public Application configure() {
        return new ResourceConfig(StageDefinitionController.class);
    }

    @BeforeClass
    public static void login() throws IOException, URISyntaxException {
        RedisManager.initializeEmbededDb(6379);
        RedisManager.connect("redis");
        stageDefinitionService = new StageDefinitionService();
        pipelineDefinitionService = new PipelineDefinitionService();
    }

    @AfterClass
    public static void logout() {
        RedisManager.release();
    }

    @Test
    public void getAllPipelineDefinition_request_emptyList(){
        //Arrange
        List<StageDefinition> expectedResult = new ArrayList<>();

        //Act
        Response response = target("/stage-definitions").request().get();
        List<StageDefinition> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200,response.getStatus());
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void getById_request_stageDefinitionObject(){
        //Arrange
        this.prepareStageDefinition();
        stageDefinitionService.add(this.stageDefinition);


        //Act
        Response response = target("/stage-definitions/"+this.stageDefinition.getId()).request().get();
        StageDefinition actualResult = response.readEntity(StageDefinition.class);


        assertEquals(200,response.getStatus());
        assertEquals(this.stageDefinition.getId(),actualResult.getId());
        this.removeStageDefinition();
    }

    @Test
    public void getById_getStageDefinitionByWrongId_errorMessage(){
        //Arrange
        String expectedResult = "StageDefinition not found.";

        //Act
        Response response = target("/stage-definitions/wrongId").request().get();

        //Assert
        assertEquals(404,response.getStatus());
        assertEquals(expectedResult,response.readEntity(String.class));
    }

    @Test
    public void addStageDefinition_oneStageDefinition_successMessage(){
        //Arrange
        this.prepareStageDefinition();
        Entity entity = Entity.entity(this.stageDefinition,"application/json");

        //Act
        Response response = target("/stage-definitions").request().post(entity);
        StageDefinition  actualResult = response.readEntity(StageDefinition.class);

        //Assert
        assertEquals(201,response.getStatus());
        assertEquals(this.stageDefinition.getId(),actualResult.getId());
        this.removeStageDefinition();
    }

    /*
    @Test
    public void addStageDefinition_invalidField_errorMessage(){
        //Arrange
        this.prepareStageDefinition();
        Entity entity = Entity.entity(this.stageDefinition, "application/json");

        //Act
        Response response = target().request("/stage-definitions").post(entity);

        //Assert
        assertEquals(400,response.getStatus());
    }
*/

    @Test
    public void updateStageDefinition_existingStageDefinition_updatedStageDefinition(){
        //Arrange
        this.prepareStageDefinition();
        stageDefinitionService.add(this.stageDefinition);

        this.stageDefinition.setName("updated-stageDefinition");
        Entity entity = Entity.entity(this.stageDefinition, "application/json");

        //Act
        Response response = target("stage-definitions/").request().put(entity);
        StageDefinition actualResult = response.readEntity(StageDefinition.class);

        //Assert
        assertEquals(200,response.getStatus());
        assertEquals(this.stageDefinition.getName(), actualResult.getName());
        this.removeStageDefinition();
    }

    @Test
    public void updateStageDefinition_nonExistingStageDefinition_errorMessage(){
        //Arrange
        this.prepareStageDefinition();
        Entity entity = Entity.entity(this.stageDefinition,"application/json");
        String expectedMessage = "StageDefinition not found.";

        //Act
        Response response = target("/stage-definitions").request().put(entity);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedMessage, response.readEntity(String.class));
    }

    @Test
    public void deleteStageDefinition_stageDefinition_successMessage(){
        //Arrange
        this.prepareStageDefinition();
        StageDefinition st = new StageDefinition();
        st.setPipelineDefinitionId(this.pipelineDefinition.getId());
        stageDefinitionService.add(st);
        stageDefinitionService.add(this.stageDefinition);

        //Act
        Response response = target("/stage-definitions/" + this.stageDefinition.getId()).request().delete();

        //Assert
        assertEquals(204,response.getStatus());
        this.removeStageDefinition();
    }


    /*
//StageDefinitionService delete метода да се прегледа.
    @Test
    public void deleteStageDefinition_nonExistingStageDefinition_errorMessage(){
        //Arrange

        //Act
        Response response = target("/stage-definitions/wrongId" ).request().delete();
        System.out.println(response.readEntity(String.class));

        //Assert
        assertEquals(404,response.getStatus());

    }

*/
    private void prepareStageDefinition() {
        this.stageDefinition = new StageDefinition();
        this.pipelineDefinition = new PipelineDefinition();
        this.stageDefinition.setPipelineDefinitionId(this.pipelineDefinition.getId());
        pipelineDefinitionService.add(this.pipelineDefinition);
    }

    private void removeStageDefinition() {
        stageDefinitionService.delete(this.stageDefinition.getId());
        pipelineDefinitionService.delete(this.pipelineDefinition.getId());

    }

}
