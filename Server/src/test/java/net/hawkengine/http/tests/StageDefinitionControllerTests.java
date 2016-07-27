package net.hawkengine.http.tests;

import net.hawkengine.http.StageDefinitionController;
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
import net.hawkengine.services.StageDefinitionService;
import net.hawkengine.services.interfaces.IStageDefinitionService;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StageDefinitionControllerTests extends JerseyTest {
    private IStageDefinitionService stageDefinitionService;
    private StageDefinitionController stageDefinitionController;
    private StageDefinition stageDefinition;
    private ServiceResult serviceResult;

    public Application configure() {
        this.stageDefinitionService = Mockito.mock(StageDefinitionService.class);
        this.stageDefinitionController = new StageDefinitionController(this.stageDefinitionService);
        this.serviceResult = new ServiceResult();
        return new ResourceConfig().register(this.stageDefinitionController);
    }

    @Test
    public void stageDefinitionController_constructorTest_notNull() {

        StageDefinitionController stageDefinitionController = new StageDefinitionController();

        assertNotNull(stageDefinitionController);
    }

    @Test
    public void getAllStageDefinitions_nonExistingObjects_emptyList() {
        //Arrange
        List<PipelineDefinition> expectedResult = new ArrayList<>();
        this.serviceResult.setObject(expectedResult);
        Mockito.when(this.stageDefinitionService.getAll()).thenReturn(this.serviceResult);


        //Act
        Response response = target("/stage-definitions").request().get();
        List<StageDefinition> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getAllStageDefinitions_existingObjects_twoObjects() {
        //Arrange
        List<StageDefinition> expectedResult = new ArrayList<>();
        this.stageDefinition = new StageDefinition();
        expectedResult.add(this.stageDefinition);
        expectedResult.add(this.stageDefinition);
        this.serviceResult.setObject(expectedResult);
        Mockito.when(this.stageDefinitionService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/stage-definitions").request().get();
        List<PipelineDefinition> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.size(), actualResult.size());

    }

    @Test
    public void getStageDefinitionById_existingObject_correctObject() {
        //Arrange
        this.stageDefinition = new StageDefinition();
        this.serviceResult.setObject(this.stageDefinition);
        Mockito.when(this.stageDefinitionService.getById(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/stage-definitions/" + this.stageDefinition.getId()).request().get();
        StageDefinition actualResult = response.readEntity(StageDefinition.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.stageDefinition.getId(), actualResult.getId());

    }

    @Test
    public void getPipelineDefinitionById_nonExistingObject_properErrorMessage() {
        //Arrange
        String expectedResult = "StageDefinition not found.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setError(true);
        this.serviceResult.setObject(null);
        Mockito.when(this.stageDefinitionService.getById(Mockito.any())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/stage-definitions/wrongId").request().get();
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }


    @Test
    public void addStageDefinition_oneObject_successMessage() {
        //Arrange
        this.prepareStageDefinition();
        this.serviceResult.setObject(this.stageDefinition);
        Mockito.when(this.stageDefinitionService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.stageDefinition, "application/json");

        //Act
        Response response = target("/stage-definitions").request().post(entity);
        StageDefinition actualResult = response.readEntity(StageDefinition.class);

        //Assert
        assertEquals(201, response.getStatus());
        assertEquals(this.stageDefinition.getId(), actualResult.getId());
    }

    @Test
    public void addStageDefinition_invalidField_properErrorMessage() {
        //Arrange
        this.prepareStageDefinition();
        this.stageDefinition.setName(null);
        String expectedResult = "ERROR: STAGE DEFINITION NAME IS NULL.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setError(true);
        this.serviceResult.setObject(this.stageDefinition);
        Mockito.when(this.stageDefinitionService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.stageDefinition, "application/json");

        //Act
        Response response = target("/stage-definitions").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void addStageDefinition_existingObject_properErrorMessage() {
        //Arrange
        this.prepareStageDefinition();
        String expectedResult = "StageDefinition already exists.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setObject(null);
        Mockito.when(this.stageDefinitionService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.stageDefinition, "application/json");

        //Act
        Response response = target("/stage-definitions").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void addStageDefinition_withSameName_properErrorMessage() {
        //Arrange
        this.prepareStageDefinition();
        this.serviceResult.setObject(null);
        this.serviceResult.setError(true);
        String expectedResult = "StageDefinition with that name already exists.";
        this.serviceResult.setMessage(expectedResult);
        Mockito.when(this.stageDefinitionService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.stageDefinition, "application/json");

        //Act
        Response response = target("/stage-definitions/").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void updateStageDefinition_existingStageDefinition_updatedStageDefinition() {
        //Arrange
        this.prepareStageDefinition();
        this.serviceResult.setObject(this.stageDefinition);
        this.stageDefinition.setName("name-updated");
        Mockito.when(this.stageDefinitionService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.stageDefinition, "application/json");

        //Act
        Response response = target("stage-definitions/").request().put(entity);
        StageDefinition actualResult = response.readEntity(StageDefinition.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.stageDefinition.getName(), actualResult.getName());
    }

    @Test
    public void updateStageDefinition_nonExistingStageDefinition_properErrorMessage() {
        //Arrange
        this.prepareStageDefinition();
        String expectedMessage = "StageDefinition not found.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.stageDefinitionService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.stageDefinition, "application/json");

        //Act
        Response response = target("/stage-definitions/").request().put(entity);
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void updateStageDefinition_withSameName_properErrorMessage() {
        //Arrange
        this.prepareStageDefinition();
        String expectedMessage = "StageDefinition with that name already exists";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.stageDefinitionService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.stageDefinition, "application/json");

        //Act
        Response response = target("/stage-definitions/").request().put(entity);
        String actualMessage = response.readEntity(String.class);


        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void updateStageDefinition_invalidField_properErrorMessage() {
        //Arrange
        this.prepareStageDefinition();
        String expectedResult = "ERROR: STAGE DEFINITION NAME IS NULL.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setError(true);
        this.stageDefinition.setName(null);
        Mockito.when(this.stageDefinitionService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.stageDefinition, "application/json");

        //Act
        Response response = target("/stage-definitions").request().put(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void deleteStageDefinition_stageDefinition_successMessage() {
        //Arrange
        this.prepareStageDefinition();
        Mockito.when(this.stageDefinitionService.delete(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/stage-definitions/" + this.stageDefinition.getId()).request().delete();

        //Assert
        assertEquals(204, response.getStatus());
    }

    @Test
    public void deleteStageDefinition_nonExistingStageDefinition_errorMessage() {
        //Arrange
        String expectedMessage = "StageDefinition not found.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.stageDefinitionService.delete(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/stage-definitions/wrongId").request().delete();
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void deleteStageDefinition_lastStageDefinition_errorMessage() {
        //Arrange
        this.prepareStageDefinition();
        String expectedMessage = this.stageDefinition.getId() + " is the last Stage Definition and cannot be deleted.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.stageDefinitionService.delete(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/stage-definitions/" + this.stageDefinition.getId()).request().delete();
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }


    private void prepareStageDefinition() {
        PipelineDefinitionService pipelineDefinitionService = new PipelineDefinitionService();
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("pipelineDefinition");
        this.stageDefinition = new StageDefinition();
        this.stageDefinition.setName("stageDefinition");
        this.stageDefinition.setPipelineDefinitionId(pipelineDefinition.getId());
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setName("jobDefinition");
        jobDefinition.setStageDefinitionId(this.stageDefinition.getId());
        ExecTask execTask = new ExecTask();
        execTask.setJobDefinitionId(jobDefinition.getId());
        execTask.setName("execTask");
        execTask.setCommand("command");
        ArrayList arguments = new ArrayList();
        arguments.add("argument");
        execTask.setArguments(arguments);
        execTask.setRunIfCondition(RunIf.PASSED);
        execTask.setType(TaskType.EXEC);
        List<TaskDefinition> taskDefinitions = new ArrayList<>();
        taskDefinitions.add(execTask);
        jobDefinition.setTaskDefinitions(taskDefinitions);
        List<JobDefinition> jobDefinitions = new ArrayList<>();
        jobDefinitions.add(jobDefinition);
        stageDefinition.setJobDefinitions(jobDefinitions);
        List<StageDefinition> stageDefinitions = new ArrayList<>();
        stageDefinitions.add(stageDefinition);
        pipelineDefinition.setStageDefinitions(stageDefinitions);
        List<MaterialDefinition> materialDefinitions = new ArrayList<>();
        MaterialDefinition materialDefinition = new GitMaterial();
        materialDefinition.setType(MaterialType.GIT);
        materialDefinitions.add(materialDefinition);
        materialDefinition.setName("gitName");
        materialDefinitions.add(materialDefinition);
        pipelineDefinition.setMaterialDefinitions(materialDefinitions);
        stageDefinitionService.add(stageDefinition);
    }
}
