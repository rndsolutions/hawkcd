package net.hawkengine.http.tests;


import net.hawkengine.http.JobDefinitionController;
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
import net.hawkengine.services.JobDefinitionService;
import net.hawkengine.services.interfaces.IJobDefinitionService;

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

public class JobDefinitionControllerTests extends JerseyTest {
    private IJobDefinitionService jobDefinitionService;
    private JobDefinitionController jobDefinitionController;
    private JobDefinition jobDefinition;
    private ServiceResult serviceResult;

    public Application configure() {
        this.jobDefinitionService = Mockito.mock(JobDefinitionService.class);
        this.jobDefinitionController = new JobDefinitionController(this.jobDefinitionService);
        this.serviceResult = new ServiceResult();
        return new ResourceConfig().register(this.jobDefinitionController);
    }

    @Test
    public void testJobDefinitionController_constructorTest_notNull() {

        JobDefinitionController jobDefinitionController = new JobDefinitionController();

        assertNotNull(jobDefinitionController);
    }

    @Test
    public void getAllJobDefinitions_nonExistingObjects_emptyList() {
        //Arrange
        List<JobDefinition> expectedResult = new ArrayList<>();
        this.serviceResult.setObject(expectedResult);
        Mockito.when(this.jobDefinitionService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/job-definitions").request().get();
        List<JobDefinition> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getAllJobDefinitions_existingObjects_twoObjects() {
        //Arrange
        List<JobDefinition> expectedResult = new ArrayList<>();
        this.jobDefinition = new JobDefinition();
        expectedResult.add(this.jobDefinition);
        expectedResult.add(this.jobDefinition);
        this.serviceResult.setObject(expectedResult);
        Mockito.when(this.jobDefinitionService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/job-definitions").request().get();
        List<StageDefinition> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.size(), actualResult.size());
    }


    @Test
    public void getJobDefinitionById_existingObject_correctObject() {
        //Arrange
        this.jobDefinition = new JobDefinition();
        this.serviceResult.setObject(this.jobDefinition);
        Mockito.when(this.jobDefinitionService.getById(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/job-definitions/" + this.jobDefinition.getId()).request().get();
        JobDefinition actualResult = response.readEntity(JobDefinition.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.jobDefinition.getId(), actualResult.getId());
    }

    @Test
    public void getJobDefinitionById_nonExistingObject_properErrorMessage() {
        //Arrange
        String expectedResult = "JobDefinition not found.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setError(true);
        this.serviceResult.setObject(null);
        Mockito.when(this.jobDefinitionService.getById(Mockito.any())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/job-definitions/wrongId").request().get();

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedResult, response.readEntity(String.class));
    }

    @Test
    public void addJobDefinition_oneObject_successMessage() {
        //Arrange
        this.prepareJobDefinition();
        this.serviceResult.setObject(this.jobDefinition);
        Mockito.when(this.jobDefinitionService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.jobDefinition, "application/json");

        //Act
        Response response = target("/job-definitions").request().post(entity);
        JobDefinition actualResult = response.readEntity(JobDefinition.class);

        //Assert
        assertEquals(201, response.getStatus());
        assertEquals(this.jobDefinition.getId(), actualResult.getId());
    }

    @Test
    public void addJobDefinition_invalidField_properErrorMessage() {
        //Arrange
        this.prepareJobDefinition();
        this.jobDefinition.setName(null);
        String expectedResult = "ERROR: JOB DEFINITION NAME IS NULL.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setError(true);
        this.serviceResult.setObject(this.jobDefinition);
        Mockito.when(this.jobDefinitionService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.jobDefinition, "application/json");

        //Act
        Response response = target("/job-definitions").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

/*
    TODO: service check to be implemented.
    @Test
    public void addJobDefinition_existingObject_properErrorMessage(){
        //Arrange
        this.prepareJobDefinition();
        String expectedResult = "JobDefinition already exists.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setObject(null);
        Mockito.when(this.jobDefinitionService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.jobDefinitionService,"application/json");

        //Act
        Response response = target("/job-definitions").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult,actualResult);
    }

*/

    @Test
    public void addJobDefinition_withSameName_properErrorMessage() {
        //Arrange
        this.prepareJobDefinition();
        this.serviceResult.setObject(null);
        this.serviceResult.setError(true);
        String expectedResult = "JobDefinition with the same name exists.";
        this.serviceResult.setMessage(expectedResult);
        Mockito.when(this.jobDefinitionService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.jobDefinition, "application/json");

        //Act
        Response response = target("/job-definitions/").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void updateJobDefinition_existingJobDefinition_updatedJobDefinition() {
        //Arrange
        this.prepareJobDefinition();
        this.serviceResult.setObject(this.jobDefinition);
        this.jobDefinition.setName("name-updated");
        Mockito.when(this.jobDefinitionService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.jobDefinition, "application/json");

        //Act
        Response response = target("job-definitions/").request().put(entity);
        JobDefinition actualResult = response.readEntity(JobDefinition.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.jobDefinition.getName(), actualResult.getName());
    }


    @Test
    public void updateJobDefinition_nonExistingJobDefinition_properErrorMessage() {
        //Arrange
        this.prepareJobDefinition();
        String expectedMessage = "JobDefinition not found.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.jobDefinitionService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.jobDefinition, "application/json");


        //Act
        Response response = target("/job-definitions").request().put(entity);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, response.readEntity(String.class));
    }

    @Test
    public void updateJobDefinition_withSameName_properErrorMessage() {
        //Arrange
        this.prepareJobDefinition();
        String expectedMessage = "JobDefinition with the same name exists.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.jobDefinitionService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.jobDefinition, "application/json");

        //Act
        Response response = target("/job-definitions/").request().put(entity);
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void updateJobDefinition_invalidField_properErrorMessage() {
        //Arrange
        this.prepareJobDefinition();
        String expectedResult = "ERROR: JOB DEFINITION NAME IS NULL.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setError(true);
        this.jobDefinition.setName(null);
        Mockito.when(this.jobDefinitionService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.jobDefinition, "application/json");

        //Act
        Response response = target("/job-definitions").request().put(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void deleteJobDefinition_jobDefinition_successMessage() {
        //Arrange
        this.prepareJobDefinition();
        Mockito.when(this.jobDefinitionService.delete(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/job-definitions/" + this.jobDefinition.getId()).request().delete();

        //Assert
        assertEquals(204, response.getStatus());
    }

    @Test
    public void deleteJobDefinition_nonExistingJobDefinition_errorMessage() {
        //Arrange
        String expectedMessage = "JobDefinition not found.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.jobDefinitionService.delete(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/job-definitions/wrongId").request().delete();
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void deleteJobDefinition_lastJobDefinition_errorMessage() {
        //Arrange
        this.prepareJobDefinition();
        String expectedMessage = this.jobDefinition.getId() + " cannot delete the last job definition.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.jobDefinitionService.delete(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/job-definitions/" + this.jobDefinition.getId()).request().delete();
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }


    private void prepareJobDefinition() {
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("pipelineDefinition");
        StageDefinition stageDefinition = new StageDefinition();
        stageDefinition.setName("stageDefinition");
        stageDefinition.setPipelineDefinitionId(pipelineDefinition.getId());
        this.jobDefinition = new JobDefinition();
        this.jobDefinition.setName("jobDefinition");
        this.jobDefinition.setStageDefinitionId(stageDefinition.getId());
        ExecTask execTask = new ExecTask();
        execTask.setJobDefinitionId(this.jobDefinition.getId());
        execTask.setName("execTask");
        execTask.setCommand("command");
        ArrayList arguments = new ArrayList();
        arguments.add("argument");
        execTask.setArguments(arguments);
        execTask.setRunIfCondition(RunIf.PASSED);
        execTask.setType(TaskType.EXEC);
        List<TaskDefinition> taskDefinitions = new ArrayList<>();
        taskDefinitions.add(execTask);
        this.jobDefinition.setTaskDefinitions(taskDefinitions);
        List<JobDefinition> jobDefinitions = new ArrayList<>();
        jobDefinitions.add(this.jobDefinition);
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
        this.jobDefinitionService.add(this.jobDefinition);
    }
}
