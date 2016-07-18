package net.hawkengine.http.tests;


import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.PipelineDefinitionController;

import net.hawkengine.model.PipelineDefinition;

import net.hawkengine.services.PipelineDefinitionService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.IOP.ENCODING_CDR_ENCAPS;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class PipelineDefinitionControllerTest extends JerseyTest {
    private static PipelineDefinitionService pipelineDefinitionService;
    private PipelineDefinition pipelineDefinition;

    public Application configure() {
        return new ResourceConfig(PipelineDefinitionController.class);
    }

    @BeforeClass
     public static void login() throws IOException, URISyntaxException {
        RedisManager.initializeEmbededDb(6379);
        RedisManager.connect("redis");
        pipelineDefinitionService = new PipelineDefinitionService();
    }

    @AfterClass
    public static void logout() {
        RedisManager.release();
    }

    @Test
    public void getAllPipelineDefinition_request_emptyList(){
        //Arrange
        List<PipelineDefinition> expectedResult = new ArrayList<>();

        //Act
        Response response = target("/pipeline-definitions").request().get();
        List<PipelineDefinition> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200,response.getStatus());
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void getById_request_pipelineDefinitionObject(){
        //Arrange
        this.preparePipelineDefinition();
        pipelineDefinitionService.add(this.pipelineDefinition);


        //Act
        Response response = target("/pipeline-definitions/"+this.pipelineDefinition.getId()
        +"stage-definitions").request().get();
        PipelineDefinition actualResult = response.readEntity(PipelineDefinition.class);


        assertEquals(200,response.getStatus());
        assertEquals(this.pipelineDefinition.getId(),actualResult.getId());
        this.removePipelineDefinition();
    }

    @Test
    public void getById_getPipelieDefinitionByWrongId_errorMessage(){
        //Arrange
        String expectedResult = "PipelineDefinition not found.";

        //Act
        Response response = target("/pipeline-definitions/wrongId").request().get();

        //Assert
        assertEquals(404,response.getStatus());
        assertEquals(expectedResult,response.readEntity(String.class));

    }


    @Test
    public void addPipelineDefinition_onePipelineDefinition_successMessage(){
        //Arrange
        this.preparePipelineDefinition();
        Entity entity = Entity.entity(this.pipelineDefinition,"application/json");


        //Act
        Response response = target("/pipeline-definitions").request().post(entity);
        PipelineDefinition  actualResult = response.readEntity(PipelineDefinition.class);

        //Assert
        assertEquals(201,response.getStatus());
        assertEquals(this.pipelineDefinition.getId(),actualResult.getId());
        this.removePipelineDefinition();

    }
/*
    @Test
    public void addPipelineDefinition_invalidField_errorMessage(){
        //Arrange
        this.preparePipelineDefinition();
        Entity entity = Entity.entity(this.pipelineDefinition, "application/json");

        //Act
        Response response = target().request("/pipeline-definitions).post(entity);

        //Assert
        assertEquals(400,response.getStatus());
    }

*/
    @Test
    public void updatePipelineDefinition_existingPipelineDefinition_updatedPipelineDefinition(){
        //Arrange
        this.preparePipelineDefinition();
        pipelineDefinitionService.add(this.pipelineDefinition);
        this.pipelineDefinition.setName("name-updated");
        Entity entity = Entity.entity(this.pipelineDefinition, "application/json");

        //Act
        Response response = target("pipeline-definitions/").request().put(entity);
        PipelineDefinition actualResult = response.readEntity(PipelineDefinition.class);

        //Assert
        assertEquals(200,response.getStatus());
        assertEquals(this.pipelineDefinition.getName(), actualResult.getName());
        this.removePipelineDefinition();
    }

    @Test
    public void updatePipelineDefinition_nonExisting_errorMessage(){
        //Arrange
        this.preparePipelineDefinition();
        Entity entity = Entity.entity(this.pipelineDefinition,"application/json");
        String expectedMessage = "PipelineDefinition not found.";

        //Act
        Response response = target("/pipeline-definitions/").request().put(entity);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedMessage, response.readEntity(String.class));
    }

    @Test
    public void deletePipelineDefinition_pipelineDefintion_successMessage(){
        //Arrange
        this.preparePipelineDefinition();
        pipelineDefinitionService.add(this.pipelineDefinition);

        //Act
        Response response = target("/pipeline-definitions/" + this.pipelineDefinition.getId()).request().delete();

        //Assert
        assertEquals(204,response.getStatus());
    }

    @Test
    public void deletePipelineDefinition_nonExistingPipelineDefinition_errorMessage(){
        //Arrange
        this.preparePipelineDefinition();
        String expectedMessage = "PipelineDefinition not found.";

        //Act
        Response response = target("/pipeline-definitions/wrongId").request().delete();

        //Assert
        assertEquals(404,response.getStatus());
        assertEquals(expectedMessage, response.readEntity(String.class));
    }





    private void preparePipelineDefinition() {
        this.pipelineDefinition = new PipelineDefinition();
        /*
        ExecTask execTask = new ExecTask();
        execTask.setName("execTask");
        execTask.setRunIfCondition(RunIf.PASSED);
        execTask.setType(TaskType.EXEC);
        List <MaterialDefinition> materialDefinitions = new ArrayList<>();
        MaterialDefinition materialDefinition = new GitMaterial();
        materialDefinition.setType(MaterialType.GIT);
        materialDefinitions.add(materialDefinition);
        this.pipelineDefinition.setMaterials(materialDefinitions);
        this.pipelineDefinition.setName("pipelineDefinitio");
        List <StageDefinition> stageDefinitions  = new ArrayList<>();
        StageDefinition stageDefinition = new StageDefinition();
        stageDefinition.setName("stageDefinitio");
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setName("jobDefinitio");
        List <TaskDefinition> taskDefinitions = new ArrayList<>();
        List<String> taskArguments = new ArrayList();
        taskArguments.add("1");
        execTask.setArguments(taskArguments);
        execTask.setCommand("echo hi");
        taskDefinitions.add(execTask);
        jobDefinition.setTaskDefinitions(taskDefinitions);
        List <JobDefinition> jobDefinitions = new ArrayList<>();
        jobDefinitions.add(jobDefinition);
        stageDefinition.setJobDefinitions(jobDefinitions);
        stageDefinitions.add(stageDefinition);
        this.pipelineDefinition.setStageDefinitions(stageDefinitions);
*/

    }

    private void removePipelineDefinition() {
        this.pipelineDefinitionService.delete(this.pipelineDefinition.getId());
    }


}
