package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.ExecTask;
import net.hawkengine.model.FetchArtifactTask;
import net.hawkengine.model.FetchMaterialTask;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.UploadArtifactTask;
import net.hawkengine.model.enums.RunIf;
import net.hawkengine.services.JobDefinitionService;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.StageDefinitionService;
import net.hawkengine.services.TaskDefinitionService;
import net.hawkengine.services.interfaces.IJobDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IStageDefinitionService;
import net.hawkengine.services.interfaces.ITaskDefinitionService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import redis.clients.jedis.JedisPoolConfig;


public class TaskDefinitionServiceTests {
    private IPipelineDefinitionService pipelineDefinitionService;
    private IStageDefinitionService stageDefinitionService;
    private IJobDefinitionService jobDefinitionService;
    private ITaskDefinitionService taskDefinitionService;
    private PipelineDefinition pipelineDefinition;
    private StageDefinition stageDefinition;
    private JobDefinition jobDefinition;
    private String notFoundMessage = "TaskDefinition not found.";
    private String existingNameErrorMessage = "TaskDefinition with the same name exists.";
    private String retrievalSuccessMessage = "TaskDefinitions retrieved successfully.";
    private String deletionSuccessMessage = "TaskDefinition deleted successfully.";
    private String notDeletedFailureMessage = "not deleted successfully";

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testJobDefinitionService");
        IDbRepository pipelineRepo = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.pipelineDefinitionService = new PipelineDefinitionService(pipelineRepo);
        this.stageDefinitionService = new StageDefinitionService(this.pipelineDefinitionService);
        this.jobDefinitionService = new JobDefinitionService(this.stageDefinitionService);
        this.taskDefinitionService = new TaskDefinitionService(this.jobDefinitionService);
        this.pipelineDefinition = new PipelineDefinition();
        this.stageDefinition = new StageDefinition();
        this.jobDefinition = new JobDefinition();

        this.injectDataForTestingStageDefinitionService();
    }

    @Test
    public void getById_validId_correctObject() {
        //Arrange
        ExecTask expectedTaskDefinition = new ExecTask();
        String args = "/c echo test";
        expectedTaskDefinition.setName("myExecTask");
        expectedTaskDefinition.setWorkingDirectory("D//:myDir");
        expectedTaskDefinition.setIgnoringErrors(true);
        expectedTaskDefinition.setCommand("cmd");
        expectedTaskDefinition.setArguments(args);
        expectedTaskDefinition.setPipelineDefinitionId(this.pipelineDefinition.getId());
        expectedTaskDefinition.setJobDefinitionId(this.jobDefinition.getId());
        expectedTaskDefinition.setStageDefinitionId(this.stageDefinition.getId());
        expectedTaskDefinition.setRunIfCondition(RunIf.PASSED);
        String successMessage = "TaskDefinition " + expectedTaskDefinition.getId() + " retrieved successfully.";
        this.addTaskToPipeline(expectedTaskDefinition);
        this.pipelineDefinitionService.update(this.pipelineDefinition);

        //Act
        ServiceResult res = this.pipelineDefinitionService.getAll();
        ServiceResult actualResult = this.taskDefinitionService.getById(expectedTaskDefinition.getId());
        ExecTask actualResultObject = (ExecTask) actualResult.getObject();

        //Assert
        Assert.assertNotNull(actualResult.getObject());
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedTaskDefinition.getId(), actualResultObject.getId());
        Assert.assertEquals(expectedTaskDefinition.getName(), actualResultObject.getName());
        Assert.assertEquals(expectedTaskDefinition.getJobDefinitionId(), actualResultObject.getJobDefinitionId());
        Assert.assertEquals(expectedTaskDefinition.getPipelineDefinitionId(), actualResultObject.getPipelineDefinitionId());
        Assert.assertEquals(expectedTaskDefinition.getStageDefinitionId(), actualResultObject.getStageDefinitionId());
        Assert.assertEquals(expectedTaskDefinition.getArguments(), actualResultObject.getArguments());
        Assert.assertEquals(expectedTaskDefinition.getCommand(), actualResultObject.getCommand());
        Assert.assertEquals(expectedTaskDefinition.getWorkingDirectory(), actualResultObject.getWorkingDirectory());
        Assert.assertEquals(expectedTaskDefinition.getRunIfCondition(), actualResultObject.getRunIfCondition());
        Assert.assertEquals(successMessage, actualResult.getMessage());
    }

    @Test
    public void getById_invalidId_null() {
        //Arrange
        String invalidId = "12345";

        //Act
        ServiceResult actualResult = this.taskDefinitionService.getById(invalidId);

        //Assert
        Assert.assertTrue(actualResult.hasError());
        Assert.assertEquals(this.notFoundMessage, actualResult.getMessage());
        Assert.assertNull(actualResult.getObject());
    }

    @Test
    public void getAll_returnAllTaskDefinitions() {
        //Arrange
        int expectedJobDefinitionsCount = 2;
        ExecTask firstTask = new ExecTask();
        ExecTask secondTask = new ExecTask();

        this.addTaskToPipeline(firstTask);
        this.addTaskToPipeline(secondTask);

        //Act
        ServiceResult actualResult = this.taskDefinitionService.getAll();
        List<TaskDefinition> actualJobDefinitions = (List<TaskDefinition>) actualResult.getObject();

        //Assert
        Assert.assertNotNull(actualResult.getObject());
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedJobDefinitionsCount, actualJobDefinitions.size());
        Assert.assertEquals(this.retrievalSuccessMessage, actualResult.getMessage());
    }

    @Test
    public void add_execTask_correctObject() {
        //Arrange
        ExecTask expectedTask = new ExecTask();
        expectedTask.setName("myExecTask");
        expectedTask.setWorkingDirectory("D//:myDir");
        expectedTask.setIgnoringErrors(true);
        expectedTask.setCommand("cmd");
        String argumentsList = "/c echo test";
        expectedTask.setArguments(argumentsList);
        expectedTask.setPipelineDefinitionId(this.pipelineDefinition.getId());
        expectedTask.setJobDefinitionId(this.jobDefinition.getId());
        expectedTask.setStageDefinitionId(this.stageDefinition.getId());
        expectedTask.setRunIfCondition(RunIf.PASSED);
        String successMessage = "TaskDefinition " + expectedTask.getId() + " added successfully.";

        //Act
        ServiceResult actualExecResult = this.taskDefinitionService.add(expectedTask);
        ExecTask actualExecResultObject = (ExecTask) actualExecResult.getObject();

        //Assert
        Assert.assertNotNull(actualExecResultObject);
        Assert.assertFalse(actualExecResult.hasError());
        Assert.assertEquals(expectedTask.getId(), actualExecResultObject.getId());
        Assert.assertEquals(expectedTask.getName(), actualExecResultObject.getName());
        Assert.assertEquals(expectedTask.getJobDefinitionId(), actualExecResultObject.getJobDefinitionId());
        Assert.assertEquals(expectedTask.getPipelineDefinitionId(), actualExecResultObject.getPipelineDefinitionId());
        Assert.assertEquals(expectedTask.getStageDefinitionId(), actualExecResultObject.getStageDefinitionId());
        Assert.assertEquals(expectedTask.getArguments(), actualExecResultObject.getArguments());
        Assert.assertEquals(expectedTask.getCommand(), actualExecResultObject.getCommand());
        Assert.assertEquals(expectedTask.getWorkingDirectory(), actualExecResultObject.getWorkingDirectory());
        Assert.assertEquals(expectedTask.getRunIfCondition(), actualExecResultObject.getRunIfCondition());
        Assert.assertEquals(successMessage, actualExecResult.getMessage());
    }

    @Test
    public void add_fetchArtifactTask_correctObject() {
        //Arrange
        FetchArtifactTask expectedTask = new FetchArtifactTask();
        expectedTask.setName("myName");
        expectedTask.setPipelineDefinitionId(this.pipelineDefinition.getId());
        expectedTask.setJobDefinitionId(this.jobDefinition.getId());
        expectedTask.setStageDefinitionId(this.stageDefinition.getId());

        //Act
        ServiceResult actualExecResult = this.taskDefinitionService.add(expectedTask);
        FetchArtifactTask actualExecResultObject = (FetchArtifactTask) actualExecResult.getObject();

        //Assert
        Assert.assertNotNull(actualExecResultObject);
        Assert.assertFalse(actualExecResult.hasError());
        Assert.assertEquals(expectedTask.getId(), actualExecResultObject.getId());
        Assert.assertEquals(expectedTask.getName(), actualExecResultObject.getName());
        Assert.assertEquals(expectedTask.getJobDefinitionId(), actualExecResultObject.getJobDefinitionId());
        Assert.assertEquals(expectedTask.getPipelineDefinitionId(), actualExecResultObject.getPipelineDefinitionId());
        Assert.assertEquals(expectedTask.getStageDefinitionId(), actualExecResultObject.getStageDefinitionId());
    }

    @Test
    public void add_fetchMaterialTask_correctObject() {
        //Arrange
        FetchMaterialTask expectedTask = new FetchMaterialTask();
        expectedTask.setName("myName");
        expectedTask.setPipelineDefinitionId(this.pipelineDefinition.getId());
        expectedTask.setJobDefinitionId(this.jobDefinition.getId());
        expectedTask.setStageDefinitionId(this.stageDefinition.getId());

        //Act
        ServiceResult actualExecResult = this.taskDefinitionService.add(expectedTask);
        FetchMaterialTask actualExecResultObject = (FetchMaterialTask) actualExecResult.getObject();

        //Assert
        Assert.assertNotNull(actualExecResultObject);
        Assert.assertFalse(actualExecResult.hasError());
        Assert.assertEquals(expectedTask.getId(), actualExecResultObject.getId());
        Assert.assertEquals(expectedTask.getName(), actualExecResultObject.getName());
        Assert.assertEquals(expectedTask.getJobDefinitionId(), actualExecResultObject.getJobDefinitionId());
        Assert.assertEquals(expectedTask.getPipelineDefinitionId(), actualExecResultObject.getPipelineDefinitionId());
        Assert.assertEquals(expectedTask.getStageDefinitionId(), actualExecResultObject.getStageDefinitionId());
    }

    @Test
    public void add_uploadArtifactTask_correctObject() {
        //Arrange
        UploadArtifactTask expectedTask = new UploadArtifactTask();
        expectedTask.setName("myName");
        expectedTask.setPipelineDefinitionId(this.pipelineDefinition.getId());
        expectedTask.setJobDefinitionId(this.jobDefinition.getId());
        expectedTask.setStageDefinitionId(this.stageDefinition.getId());

        //Act
        ServiceResult actualExecResult = this.taskDefinitionService.add(expectedTask);
        UploadArtifactTask actualExecResultObject = (UploadArtifactTask) actualExecResult.getObject();

        //Assert
        Assert.assertNotNull(actualExecResultObject);
        Assert.assertFalse(actualExecResult.hasError());
        Assert.assertEquals(expectedTask.getId(), actualExecResultObject.getId());
        Assert.assertEquals(expectedTask.getName(), actualExecResultObject.getName());
        Assert.assertEquals(expectedTask.getJobDefinitionId(), actualExecResultObject.getJobDefinitionId());
        Assert.assertEquals(expectedTask.getPipelineDefinitionId(), actualExecResultObject.getPipelineDefinitionId());
        Assert.assertEquals(expectedTask.getStageDefinitionId(), actualExecResultObject.getStageDefinitionId());
    }

    @Test
    public void add_task_correctErrorMessage() {
        //Arrange
        JobDefinitionService mockedJobDefinitionService = Mockito.mock(JobDefinitionService.class);
        TaskDefinitionService taskDefinitionServiceToTest = new TaskDefinitionService(mockedJobDefinitionService);
        ServiceResult mockedServiceResult = Mockito.mock(ServiceResult.class);
        JobDefinition mockedJobDefinition = Mockito.mock(JobDefinition.class);
        ServiceResult getByIdServiceResult = Mockito.mock(ServiceResult.class);

        Mockito.when(getByIdServiceResult.getObject()).thenReturn(mockedJobDefinition);
        UploadArtifactTask mockedTaskDefinition = Mockito.mock(UploadArtifactTask.class);
        Mockito.when(mockedTaskDefinition.getJobDefinitionId()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(mockedTaskDefinition.getId()).thenReturn(UUID.randomUUID().toString());

        Mockito.when(mockedJobDefinition.getTaskDefinitions()).thenReturn(new ArrayList<>()).thenReturn(new ArrayList<>());
        Mockito.when(mockedJobDefinitionService.getById(Mockito.anyString())).thenReturn(mockedServiceResult);
        Mockito.when(mockedJobDefinitionService.getById(Mockito.anyString()).getObject()).thenReturn(mockedJobDefinition);
        Mockito.when(mockedJobDefinitionService.update(Mockito.any(JobDefinition.class))).thenReturn(mockedServiceResult);

        //Act
        ServiceResult actualExecResult = taskDefinitionServiceToTest.add(mockedTaskDefinition);

        //Assert
        Assert.assertNull(actualExecResult.getObject());
        Assert.assertTrue(actualExecResult.hasError());
        Assert.assertEquals("TaskDefinition not added successfully.", actualExecResult.getMessage());
    }

    @Test
    public void update_validName_validObject() {
        //Arrange
        ExecTask execTask = new ExecTask();
        execTask.setName("myName1");
        execTask.setPipelineDefinitionId(this.pipelineDefinition.getId());
        execTask.setJobDefinitionId(this.jobDefinition.getId());
        execTask.setStageDefinitionId(this.stageDefinition.getId());

        UploadArtifactTask uploadArtifactTask = new UploadArtifactTask();
        uploadArtifactTask.setName("myName2");
        uploadArtifactTask.setPipelineDefinitionId(this.pipelineDefinition.getId());
        uploadArtifactTask.setJobDefinitionId(this.jobDefinition.getId());
        uploadArtifactTask.setStageDefinitionId(this.stageDefinition.getId());

        FetchArtifactTask fetchArtifactTask = new FetchArtifactTask();
        fetchArtifactTask.setName("myName3");
        fetchArtifactTask.setPipelineDefinitionId(this.pipelineDefinition.getId());
        fetchArtifactTask.setJobDefinitionId(this.jobDefinition.getId());
        fetchArtifactTask.setStageDefinitionId(this.stageDefinition.getId());

        FetchMaterialTask fetchMaterialTask = new FetchMaterialTask();
        fetchMaterialTask.setName("myName4");
        fetchMaterialTask.setPipelineDefinitionId(this.pipelineDefinition.getId());
        fetchMaterialTask.setJobDefinitionId(this.jobDefinition.getId());
        fetchMaterialTask.setStageDefinitionId(this.stageDefinition.getId());

        this.addTaskToPipeline(execTask);
        this.addTaskToPipeline(uploadArtifactTask);
        this.addTaskToPipeline(fetchArtifactTask);
        this.addTaskToPipeline(fetchMaterialTask);

        execTask.setName("myUpdatedName");
        uploadArtifactTask.setName("myUpdatedName2");
        fetchArtifactTask.setName("myUpdatedName3");
        fetchMaterialTask.setName("myUpdatedName4");

        String successMessage = "TaskDefinition " + execTask.getId() + " updated successfully.";

        //Act
        ServiceResult actualResult = this.taskDefinitionService.update(execTask);
        ServiceResult actualResult2 = this.taskDefinitionService.update(uploadArtifactTask);
        ServiceResult actualResult3 = this.taskDefinitionService.update(fetchArtifactTask);
        ServiceResult actualResult4 = this.taskDefinitionService.update(fetchMaterialTask);

        ExecTask actualResultObject = (ExecTask) actualResult.getObject();
        UploadArtifactTask actualResultObject2 = (UploadArtifactTask) actualResult2.getObject();
        FetchArtifactTask actualResultObject3 = (FetchArtifactTask) actualResult3.getObject();
        FetchMaterialTask actualResultObject4 = (FetchMaterialTask) actualResult4.getObject();

        //Assert
        //Exec
        Assert.assertFalse(actualResult.hasError());
        Assert.assertNotNull(actualResult.getObject());
        Assert.assertEquals(execTask.getName(), actualResultObject.getName());
        Assert.assertEquals(successMessage, actualResult.getMessage());
        //UploadArtifact
        Assert.assertFalse(actualResult2.hasError());
        Assert.assertNotNull(actualResult2.getObject());
        Assert.assertEquals(uploadArtifactTask.getName(), actualResultObject2.getName());
        //FetchArtifact
        Assert.assertFalse(actualResult3.hasError());
        Assert.assertNotNull(actualResult3.getObject());
        Assert.assertEquals(fetchArtifactTask.getName(), actualResultObject3.getName());
        //FetchMaterial
        Assert.assertFalse(actualResult4.hasError());
        Assert.assertNotNull(actualResult4.getObject());
        Assert.assertEquals(fetchMaterialTask.getName(), actualResultObject4.getName());
    }

    @Test
    public void update_notExisting_notFound() {
        //Arrange
        ExecTask notExistingTask = new ExecTask();
        notExistingTask.setPipelineDefinitionId(this.pipelineDefinition.getId());
        notExistingTask.setJobDefinitionId(this.jobDefinition.getId());
        notExistingTask.setStageDefinitionId(this.stageDefinition.getId());

        //Act
        ServiceResult result = this.taskDefinitionService.update(notExistingTask);

        //Assert
        Assert.assertTrue(result.hasError());
        Assert.assertEquals(this.notFoundMessage, result.getMessage());
        Assert.assertNull(result.getObject());
    }

    @Test
    public void delete_validId_correctObject() {
        //Assert
        UploadArtifactTask expectedTask = new UploadArtifactTask();
        expectedTask.setName("myName");
        expectedTask.setPipelineDefinitionId(this.pipelineDefinition.getId());
        expectedTask.setJobDefinitionId(this.jobDefinition.getId());
        expectedTask.setStageDefinitionId(this.stageDefinition.getId());
        UploadArtifactTask anotherTask = new UploadArtifactTask();
        anotherTask.setName("anotherName");
        anotherTask.setPipelineDefinitionId(this.pipelineDefinition.getId());
        anotherTask.setJobDefinitionId(this.jobDefinition.getId());
        anotherTask.setStageDefinitionId(this.stageDefinition.getId());
        this.addTaskToPipeline(expectedTask);
        this.addTaskToPipeline(anotherTask);

        //Act
        ServiceResult actualResult = this.taskDefinitionService.delete(expectedTask.getId());

        //Assert
        Assert.assertFalse(actualResult.hasError());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals(this.deletionSuccessMessage, actualResult.getMessage());
    }

    @Test
    public void delete_lastTask_correctErrorMessage() {
        //Assert
        UploadArtifactTask expectedTask = new UploadArtifactTask();
        expectedTask.setName("myName");
        expectedTask.setPipelineDefinitionId(this.pipelineDefinition.getId());
        expectedTask.setJobDefinitionId(this.jobDefinition.getId());
        expectedTask.setStageDefinitionId(this.stageDefinition.getId());
        this.addTaskToPipeline(expectedTask);

        //Act
        ServiceResult actualResult = this.taskDefinitionService.delete(expectedTask.getId());

        //Assert
        Assert.assertTrue(actualResult.hasError());
        Assert.assertNotNull(actualResult.getObject());
        Assert.assertEquals("TaskDefinition cannot delete the last task definition.", actualResult.getMessage());
    }

    @Test
    public void delete_invalidId_correctErrorMessage() {
        //Act
        ServiceResult actualResult = this.taskDefinitionService.delete("12345");

        //Assert
        Assert.assertTrue(actualResult.hasError());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals("TaskDefinition does not exists.", actualResult.getMessage());
    }

    private void injectDataForTestingStageDefinitionService() {
        this.pipelineDefinition.setName("mockedPipelineDefinition");
        this.stageDefinition.setName("stageDefinition");
        this.stageDefinition.setPipelineDefinitionId(this.pipelineDefinition.getId());
        this.jobDefinition.setPipelineDefinitionId(this.pipelineDefinition.getId());
        this.jobDefinition.setStageDefinitionId(this.stageDefinition.getId());
        this.jobDefinition.setName("jobDefinition");
        this.stageDefinition.getJobDefinitions().add(this.jobDefinition);
        this.pipelineDefinition.getStageDefinitions().add(this.stageDefinition);
        this.pipelineDefinitionService.add(this.pipelineDefinition);
    }

    private void addTaskToPipeline(TaskDefinition taskToAdd) {
        this.pipelineDefinition
                .getStageDefinitions()
                .get(0)
                .getJobDefinitions()
                .get(0).getTaskDefinitions().add(taskToAdd);
        this.pipelineDefinitionService.update(this.pipelineDefinition);
    }
}