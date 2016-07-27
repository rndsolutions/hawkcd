package net.hawkengine.http.tests;

import net.hawkengine.http.PipelineDefinitionController;
import net.hawkengine.model.ExecTask;
import net.hawkengine.model.GitMaterial;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.enums.MaterialType;
import net.hawkengine.model.enums.RunIf;
import net.hawkengine.model.enums.TaskType;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class PipelineDefinitionControllerTest extends JerseyTest {
    private IPipelineDefinitionService pipelineDefinitionService;
    private PipelineDefinitionController pipelineDefinitionController;
    private PipelineDefinition pipelineDefinition;
    private ServiceResult serviceResult;

    public Application configure() {
        this.pipelineDefinitionService = Mockito.mock(PipelineDefinitionService.class);
        this.pipelineDefinitionController = new PipelineDefinitionController(this.pipelineDefinitionService);
        this.serviceResult = new ServiceResult();
        return new ResourceConfig().register(this.pipelineDefinitionController);
    }

    @Test
    public void pipelineDefinitionController_constructorTest_notNull(){

        PipelineDefinitionController pipelineDefinitionController = new PipelineDefinitionController();

        assertNotNull(pipelineDefinitionController);
    }


    @Test
    public void getAllPipelineDefinitions_nonExistingObjects_emptyList() {
        //Arrange
        List<PipelineDefinition> expectedResult = new ArrayList<>();
        this.serviceResult.setObject(expectedResult);
        Mockito.when(this.pipelineDefinitionService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/pipeline-definitions").request().get();
        List<PipelineDefinition> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getAllPipelineDefinitions_existingObjects_twoObjects() {
        //Arrange
        this.preparePipelineDefinition();
        Set<PipelineDefinition> expectedResult = new HashSet<>();
        expectedResult.add(this.pipelineDefinition);
        expectedResult.add(this.pipelineDefinition);
        this.serviceResult.setObject(expectedResult);
        Mockito.when(this.pipelineDefinitionService.getAll()).thenReturn(this.serviceResult);

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
        this.serviceResult.setObject(this.pipelineDefinition);
        Mockito.when(this.pipelineDefinitionService.getById(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/pipeline-definitions/" + this.pipelineDefinition.getId()).request().get();
        PipelineDefinition actualResult = response.readEntity(PipelineDefinition.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.pipelineDefinition.getId(), actualResult.getId());
    }


    @Test
    public void getPipelineDefinitionById_nonExistingPiplineDefinition_properErrorMessage() {
        //Arrange
        String expectedResult = "PipelineDefinition not found.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setError(true);
        this.serviceResult.setObject(null);
        Mockito.when(this.pipelineDefinitionService.getById(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/pipeline-definitions/wrongId").request().get();
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void addPipelineDefinition_oneObject_successMessage() {
        //Arrange
        this.preparePipelineDefinition();
        this.serviceResult.setObject(this.pipelineDefinition);
        Mockito.when(this.pipelineDefinitionService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.pipelineDefinition, "application/json");

        //Act
        Response response = target("/pipeline-definitions").request().post(entity);
        PipelineDefinition actualResult = response.readEntity(PipelineDefinition.class);

        //Assert
        assertEquals(201, response.getStatus());
        assertEquals(this.pipelineDefinition.getId(), actualResult.getId());
    }

    @Test
    public void addPipelineDefinition_invalidField_properErrorMessage() {
        //Arrange
        this.preparePipelineDefinition();
        String expectedResult = "ERROR: PIPELINE DEFINITION NAME IS NULL.";
        this.pipelineDefinition.setName(null);
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setError(true);
        this.serviceResult.setObject(this.pipelineDefinition);
        Mockito.when(this.pipelineDefinitionService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.pipelineDefinition, "application/json");

        //Act
        Response response = target("/pipeline-definitions").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void addPipelineDefinition_existingObject_properErrorMessage() {
        //Arrange
        this.preparePipelineDefinition();
        String expectedResult = "PipelineDefinition already exists.";
        this.serviceResult.setError(true);
        this.serviceResult.setObject(null);
        this.serviceResult.setMessage(expectedResult);
        Mockito.when(this.pipelineDefinitionService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.pipelineDefinition, "application/json");

        //Act
        Response response = target("/pipeline-definitions").request().post(entity);
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualMessage);
    }

    /*
    TODO: service that checks for name collision to be implemented.

    @Test
    public void addPipelineDefinition_withSameName_properErrorMessage() {
        //Arrange
        this.preparePipelineDefinition();
        this.pipelineDefinitionService.add(this.pipelineDefinition);
        this.serviceResult.setObject(null);
        this.serviceResult.setError(true);
        this.serviceResult.setMessage("PipelineDefinition with same name already exists.");
        Mockito.when(this.pipelineDefinitionService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.pipelineDefinition, "application/json");

        //Act
        Response response = target("pipeline-definitions/").request().put(entity);
        PipelineDefinition actualResult = response.readEntity(PipelineDefinition.class);

        //Assert
        assertEquals(400, response.getStatus());

    }
    */

    @Test
    public void updatePipelineDefinition_existingPipelineDefinition_updatedPipelineDefinition() {
        //Arrange
        this.preparePipelineDefinition();
        this.serviceResult.setObject(this.pipelineDefinition);
        this.pipelineDefinition.setName("name-updated");
        Mockito.when(this.pipelineDefinitionService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
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
        String expectedMessage = "PipelineDefinition not found.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.pipelineDefinitionService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.pipelineDefinition, "application/json");

        //Act
        Response response = target("/pipeline-definitions/").request().put(entity);
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }

    /*

     TODO: service that checks for name collision to be implemented.

    @Test
    public void updatePipelineDefinition_withSameName_properErrorMessage() {
        //Arrange
        this.preparePipelineDefinition();
        String expectedMessage = "PipelineDefinition with the same name already exist.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.pipelineDefinitionService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.pipelineDefinition, "application/json");

        //Act
        Response response = target("/pipeline-definitions/").request().put(entity);
        String actualMessage = response.readEntity(String.class);


        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }

*/



    @Test
    public void updatePipelineDefinition_invalidField_properErrorMessage() {
        //Arrange
        this.preparePipelineDefinition();
        String expectedResult = "ERROR: PIPELINE DEFINITION NAME IS NULL.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setError(true);
        this.pipelineDefinition.setName(null);
        Mockito.when(this.pipelineDefinitionService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.pipelineDefinition, "application/json");

        //Act
        Response response = target("/pipeline-definitions").request().put(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void deletePipelineDefinition_pipelineDefintionId_successMessage() {
        //Arrange
        this.preparePipelineDefinition();
        Mockito.when(this.pipelineDefinitionService.delete(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/pipeline-definitions/" + this.pipelineDefinition.getId()).request().delete();

        //Assert
        assertEquals(204, response.getStatus());
    }

    @Test
    public void deletePipelineDefinition_nonExistingPipelineDefinition_properErrorMessage() {
        //Arrange
        String expectedMessage = "PipelineDefinition not found.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.pipelineDefinitionService.delete(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/pipeline-definitions/wrongId").request().delete();
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }


    private void preparePipelineDefinition() {
        this.pipelineDefinition = new PipelineDefinition();
        this.pipelineDefinition.setName("pipelineDefinition");
        StageDefinition stageDefinition = new StageDefinition();
        stageDefinition.setName("stageDefinition");
        stageDefinition.setPipelineDefinitionId(this.pipelineDefinition.getId());
        List<JobDefinition> jobDefinitions = new ArrayList<>();
        JobDefinition jobDefinition = new JobDefinition();
        stageDefinition.setJobDefinitions(jobDefinitions);
        jobDefinition.setName("jobDefinition");
        jobDefinition.setStageDefinitionId(stageDefinition.getId());
        List<TaskDefinition> taskDefinitions = new ArrayList<>();
        ExecTask execTask = new ExecTask();
        execTask.setJobDefinitionId(jobDefinition.getId());
        execTask.setName("execTask");
        execTask.setCommand("command");
        ArrayList arguments = new ArrayList();
        arguments.add("argument");
        execTask.setArguments(arguments);
        execTask.setRunIfCondition(RunIf.PASSED);
        execTask.setType(TaskType.EXEC);
        taskDefinitions.add(execTask);
        jobDefinition.setTaskDefinitions(taskDefinitions);
        stageDefinition.setJobDefinitions(jobDefinitions);
        List<MaterialDefinition> materialDefinitions = new ArrayList<>();
        MaterialDefinition materialDefinition = new GitMaterial();
        materialDefinition.setType(MaterialType.GIT);
        materialDefinitions.add(materialDefinition);
        materialDefinition.setName("gitName");
        materialDefinitions.add(materialDefinition);
        this.pipelineDefinition.setMaterialDefinitions(materialDefinitions);
        jobDefinitions.add(jobDefinition);
        List<StageDefinition> stageDefinitions = new ArrayList<>();
        stageDefinitions.add(stageDefinition);
        pipelineDefinition.setStageDefinitions(stageDefinitions);
    }
}
