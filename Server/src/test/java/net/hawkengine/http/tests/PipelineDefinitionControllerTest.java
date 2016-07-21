package net.hawkengine.http.tests;

import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.PipelineDefinitionController;
import net.hawkengine.model.ExecTask;
import net.hawkengine.model.GitMaterial;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.enums.MaterialType;
import net.hawkengine.model.enums.RunIf;
import net.hawkengine.model.enums.TaskType;
import net.hawkengine.services.PipelineDefinitionService;

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

;

public class PipelineDefinitionControllerTest extends JerseyTest {
    private PipelineDefinitionService pipelineDefinitionService;
    private PipelineDefinition pipelineDefinition;

    @BeforeClass
    public static void login() throws IOException, URISyntaxException {
        RedisManager.initializeEmbededDb(6379);
        RedisManager.connect("localhost");
        RedisManager.startEmbededDb();
    }

    @AfterClass
    public static void logout() throws InterruptedException {
        RedisManager.release();
        RedisManager.stopEmbededDb();
    }

    public Application configure() {
        return new ResourceConfig(PipelineDefinitionController.class);
    }

    @Test
    public void getAllPipelineDefinition_nonExistingObjects_emptyList()
            throws IOException, InterruptedException {
        //Arrange
        this.clearDB();
        List<PipelineDefinition> expectedResult = new ArrayList<>();

        //Act
        Response response = target("/pipeline-definitions").request().get();
        List<PipelineDefinition> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getAllPipelineDefinition_existingObjects_allObjects()
            throws IOException, InterruptedException {
        //Arrange
        List<PipelineDefinition> expectedResult = new ArrayList<>();
        this.preparePipelineDefinition();
        this.pipelineDefinitionService.add(this.pipelineDefinition);
        expectedResult.add(this.pipelineDefinition);
        this.preparePipelineDefinition();
        this.pipelineDefinitionService.add(this.pipelineDefinition);
        expectedResult.add(this.pipelineDefinition);

        //Act
        Response response = target("/pipeline-definitions").request().get();
        List<PipelineDefinition> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.size(), actualResult.size());
    }

    @Test
    public void getPipelineDefinitionById_existingObject_correctObject() {
        //Arrange
        this.preparePipelineDefinition();
        pipelineDefinitionService.add(this.pipelineDefinition);

        //Act
        Response response = target("/pipeline-definitions/" + this.pipelineDefinition.getId()).request().get();
        PipelineDefinition actualResult = response.readEntity(PipelineDefinition.class);


        assertEquals(200, response.getStatus());
        assertEquals(this.pipelineDefinition.getId(), actualResult.getId());
    }

    @Test
    public void getPipelineDefinitionById_nonExistingPiplineDefinition_properErrorMessage() {
        //Arrange
        String expectedResult = "PipelineDefinition not found.";

        //Act
        Response response = target("/pipeline-definitions/wrongId").request().get();

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedResult, response.readEntity(String.class));
    }

    @Test
    public void addPipelineDefinition_oneObject_successMessage() {
        //Arrange
        this.preparePipelineDefinition();
        Entity entity = Entity.entity(this.pipelineDefinition, "application/json");

        //Act
        Response response = target("/pipeline-definitions").request().post(entity);
        PipelineDefinition actualResult = response.readEntity(PipelineDefinition.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.pipelineDefinition.getId(), actualResult.getId());
    }

    @Test
    public void addPipelineDefinition_invalidValidation_properErrorMessage() {
        //Arrange
        this.preparePipelineDefinition();
        this.pipelineDefinition.setName(null);
        Entity entity = Entity.entity(this.pipelineDefinition, "application/json");
        String expectedMessage = "ERROR: PIPELINE DEFINITION NAME IS NULL.";

        //Act
        Response response = target("/pipeline-definitions").request().post(entity);
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }


    @Test
    public void updatePipelineDefinition_existingPipelineDefinition_updatedPipelineDefinition() {
        //Arrange
        this.preparePipelineDefinition();
        pipelineDefinitionService.add(this.pipelineDefinition);
        this.pipelineDefinition.setName("name-updated");
        Entity entity = Entity.entity(this.pipelineDefinition, "application/json");

        //Act
        Response response = target("pipeline-definitions/").request().put(entity);
        PipelineDefinition actualResult = response.readEntity(PipelineDefinition.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.pipelineDefinition.getName(), actualResult.getName());

    }

    @Test
    public void updatePipelineDefinition_nonExistingObject_properErrorMessage() {
        //Arrange
        this.preparePipelineDefinition();
        Entity entity = Entity.entity(this.pipelineDefinition, "application/json");
        String expectedMessage = "PipelineDefinition not found.";

        //Act
        Response response = target("/pipeline-definitions/").request().put(entity);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedMessage, response.readEntity(String.class));
    }

    @Test
    public void deletePipelineDefinition_pipelineDefintionId_successMessage() {
        //Arrange
        this.preparePipelineDefinition();
        pipelineDefinitionService.add(this.pipelineDefinition);

        //Act
        Response response = target("/pipeline-definitions/" + this.pipelineDefinition.getId()).request().delete();

        //Assert
        assertEquals(204, response.getStatus());
    }

    @Test
    public void deletePipelineDefinition_nonExistingPipelineDefinition_properErrorMessage() {
        //Arrange
        this.preparePipelineDefinition();
        String expectedMessage = "PipelineDefinition not found.";

        //Act
        Response response = target("/pipeline-definitions/wrongId").request().delete();

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedMessage, response.readEntity(String.class));
    }


    private void preparePipelineDefinition() {
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.pipelineDefinition = new PipelineDefinition();
        this.pipelineDefinition.setName("pipelineDefinition");
        List<MaterialDefinition> materialDefinitions = new ArrayList<>();
        MaterialDefinition materialDefinition = new GitMaterial();
        materialDefinition.setType(MaterialType.GIT);
        materialDefinitions.add(materialDefinition);
        this.pipelineDefinition.setMaterialDefinitions(materialDefinitions);
        StageDefinition stageDefinition = new StageDefinition();
        stageDefinition.setName("stageDefinition");
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setName("jobDefinitio");
        List<JobDefinition> jobDefinitions = new ArrayList<>();
        jobDefinitions.add(jobDefinition);
        ExecTask execTask = new ExecTask();
        execTask.setName("execTask");
        execTask.setRunIfCondition(RunIf.PASSED);
        execTask.setType(TaskType.EXEC);
        execTask.setCommand("echo hi");
        List<String> taskArguments = new ArrayList();
        taskArguments.add("1");
        execTask.setArguments(taskArguments);
        List<TaskDefinition> taskDefinitions = new ArrayList<>();
        taskDefinitions.add(execTask);
        jobDefinition.setTaskDefinitions(taskDefinitions);
        stageDefinition.setJobDefinitions(jobDefinitions);
        List<StageDefinition> stageDefinitions = new ArrayList<>();
        stageDefinitions.add(stageDefinition);
        this.pipelineDefinition.setStageDefinitions(stageDefinitions);

    }

    private void removePipelineDefinition() {
        if (this.pipelineDefinitionService != null) {
            this.pipelineDefinitionService.delete(this.pipelineDefinition.getId());
        }
    }

    private void clearDB() throws IOException, InterruptedException {
        RedisManager.stopEmbededDb();
        RedisManager.startEmbededDb();
    }
}
