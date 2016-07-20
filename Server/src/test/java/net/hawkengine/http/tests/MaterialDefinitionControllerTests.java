package net.hawkengine.http.tests;

import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.MaterialDefinitionController;
import net.hawkengine.model.GitMaterial;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.services.MaterialDefinitionService;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.interfaces.IMaterialDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
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

public class MaterialDefinitionControllerTests extends JerseyTest {
    private IPipelineDefinitionService pipelineDefinitionService;
    private IMaterialDefinitionService materialDefinitionService;
    private PipelineDefinition pipelineDefinition;
    private GitMaterial materialDefinition;

    @BeforeClass
    public static void login() throws IOException, URISyntaxException {
        RedisManager.initializeEmbededDb(6379);
        RedisManager.connect("redis");
    }

    @AfterClass
    public static void logout() {
        RedisManager.release();
    }
    public Application configure() {
        return new ResourceConfig(MaterialDefinitionController.class);
    }

    @Test
    public void getAllMaterialDefinitions_request_emptyList() {
        //Arrange
        List<MaterialDefinition> expectedResult = new ArrayList<>();

        //Act
        Response response = target("/material-definitions").request().get();
        List<JobDefinition> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getMaterialDefinition_getByIdRequest_materialDefinitionObject(){
        //Arrange
        this.prepareToAddMaterialDefinition();
        this.materialDefinitionService.add(this.materialDefinition);

        //Act
        Response response = target("/material-definitions/" +this.materialDefinition.getId()).request().get();
        MaterialDefinition actualResult = response.readEntity(MaterialDefinition.class);

        //Assert
        assertEquals(200,response.getStatus());
        assertEquals(this.materialDefinition.getId(), actualResult.getId());
        //this.removePipelinesAndMaterialDefinitions();
    }

    @Test
    public void getMaterialDefinition_wrongIdRequest_errorMessage(){
        //Arrange
        String expectedMessage = "MaterialDefinition not found.";

        //Act
        Response response = target("/material-definitions/wrongId").request().get();
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(404,response.getStatus());
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    public void addMaterialDefinition_gitMaterial_MaterialDefinitioniObject() {
        //Arrange
        this.prepareToAddMaterialDefinition();
        Entity entity = Entity.entity(this.materialDefinition,"application/json");

        //Act
        Response response = target("/material-definitions").request().post(entity);
        MaterialDefinition actualResult = response.readEntity(MaterialDefinition.class);

        //Assert
        assertEquals(201, response.getStatus());
        assertEquals(this.materialDefinition.getName(), actualResult.getName());
        this.removePipelinesAndMaterialDefinitions();

    }

    private void prepareToAddMaterialDefinition(){
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.materialDefinitionService = new MaterialDefinitionService();
        this.pipelineDefinition = new PipelineDefinition();
        this.pipelineDefinition.setName("pipelineDefinitionName");
        this.materialDefinition = new GitMaterial();
        this.materialDefinition.setPipelineDefinitionId(this.pipelineDefinition.getId());
        this.materialDefinition.setName("materialDefinitionName");
        this.pipelineDefinitionService.add(this.pipelineDefinition);
    }

    private void removePipelinesAndMaterialDefinitions(){
        this.pipelineDefinitionService.delete(this.pipelineDefinition.getId());
    }
}
