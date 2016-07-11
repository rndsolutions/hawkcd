package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;

import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Job;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.Stage;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.services.JobService;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.StageService;
import net.hawkengine.services.interfaces.IJobService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import net.hawkengine.services.interfaces.IStageService;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import redis.clients.jedis.JedisPoolConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JobServiceTests {
    private IPipelineService pipelineService;
    private IPipelineDefinitionService pipelineDefinitionService;
    private IStageService stageService;
    private IJobService jobService;
    private PipelineDefinition pipelineDefinition;
    private Pipeline pipeline;
    private Stage stage;
    private Job job;

    @Before
    public void setUp() {
        MockJedisPool mockJedisPool = new MockJedisPool(new JedisPoolConfig(), "testJobService");
        IDbRepository pipelineRepository = new RedisRepository(Pipeline.class, mockJedisPool);
        IDbRepository pipelineDefinitionRepository = new RedisRepository(PipelineDefinition.class, mockJedisPool);
        this.pipelineDefinitionService = new PipelineDefinitionService(pipelineDefinitionRepository);
        this.pipelineService = new PipelineService(pipelineRepository, pipelineDefinitionService);
        this.stageService = new StageService(pipelineService);
        this.jobService = new JobService(stageService);
    }

    //TODO: Rename tests to fit test naming conventions

    @Test
    public void getAll_emptyList() {
        //Arrange
        String expectedMessage = "Jobs retrieved successfully.";
        boolean expectedError = false;

        //Act
        ServiceResult actualResult = this.jobService.getAll();
        List<Job> actualObject = (List<Job>) actualResult.getObject();

        //Assert
        assertNotNull(actualResult.getObject());
        //TODO: booleans are checked with assertTrue and assertFalse in all tests
        assertEquals(expectedError, actualResult.hasError());
        assertTrue(actualObject.isEmpty());
        assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getAll_addOneObject_successMessage() {
        //Arrange
        this.insertIntoDb();
        String expectedMessage = "Jobs retrieved successfully.";
        boolean expectedError = false;

        //Act
        ServiceResult actualResult = this.jobService.getAll();
        //TODO: Rename expectedObject to actualObject in all tests
        List<Job> expectedObject = (List<Job>) actualResult.getObject();

        //Assert
        assertNotNull(actualResult.getObject());
        assertEquals(expectedError, actualResult.hasError());
        assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, expectedObject.size());
        assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getById_successMessage() {
        //Arrange
        this.insertIntoDb();
        String expectedMessage = "Job " + this.job.getId() + " retrieved successfully.";
        boolean expectedError = false;

        //Act
        ServiceResult actualResult = this.jobService.getById(this.job.getId());
        Job expectedJob = (Job) actualResult.getObject();

        //Assert
        assertNotNull(actualResult.getObject());
        assertEquals(expectedError, actualResult.hasError());
        assertEquals(this.job.getId(), expectedJob.getId());
        assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getById_wrongId_errorMessage() {
        //Arrange
        UUID randomId = UUID.randomUUID();
        String expectedMessage = "Job not found.";
        boolean expectedError = true;

        //Act
        ServiceResult actualResult = this.jobService.getById(randomId.toString());

        //Assert
        assertNull(actualResult.getObject());
        assertEquals(expectedError, actualResult.hasError());
        assertEquals(expectedMessage, actualResult.getMessage());

    }

    //TODO: Fix test and add happy path
    @Test
    public void add_oneObject_successMessage() {
        //Arrange
        this.insertIntoDb();
        boolean expectedError = false;
        String expectedMessage = "Job " + this.job.getId() + " created successfully.";

        //Act
        ServiceResult actualResult = this.jobService.add(this.job);
        Job job = (Job) actualResult.getObject();

        //Assert
        assertNotNull(actualResult.getObject());
        assertEquals(expectedError, actualResult.hasError());
        assertEquals(job, this.job);
        assertEquals(expectedMessage, actualResult.getMessage());
    }

    //TODO: Add delete non existing object method
    @Test
    public void delete_oneJob_successMessage() {
        //Arrange
        this.insertIntoDb();
        Job newJob = this.job;
        this.jobService.add(newJob);
        String expectedMessage = "Job " + this.job.getId() + " deleted successfully.";

        //Act
        ServiceResult actualResult = this.jobService.delete(newJob.getId());

        //Assert
        assertFalse(actualResult.hasError());
        assertNotNull(actualResult.getObject());
        assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void delete_lastJob_errorMessage() {
        //Arrange
        this.insertIntoDb();
        String expectedMessage = "Job is the last Job and cannot be deleted.";
        boolean expectedError = true;

        //Act
        ServiceResult actualResult = this.jobService.delete(this.job.getId());

        //Assert
        assertNotNull(actualResult.getObject());
        assertEquals(expectedMessage, actualResult.getMessage());
        assertEquals(expectedError, actualResult.hasError());
    }

    @Test
    public void update_successMessage() {
        //Act
        this.insertIntoDb();
        this.job.setStatus(JobStatus.PASSED);
        boolean expectedError = false;
        String expectedMessage = "Job " + job.getId() + " " + "updated successfully.";

        //Act
        ServiceResult actualResult = this.jobService.update(this.job);
        Job job = (Job) actualResult.getObject();

        //Assert
        assertNotNull(actualResult.getObject());
        assertEquals(expectedError, actualResult.hasError());
        assertEquals(expectedMessage, actualResult.getMessage());
        assertEquals(this.job.getStatus(), job.getStatus());
    }

    @Test
    public void update_wrongId_errorMessage() {
        //Arrange
        this.pipelineDefinition = new PipelineDefinition();
        this.pipelineDefinition.setName("pipelinedefinition");
        this.pipelineDefinitionService.add(this.pipelineDefinition);
        this.pipeline = new Pipeline();
        this.pipeline.setPipelineDefinitionName(this.pipelineDefinition.getName());
        this.pipeline.setPipelineDefinitionId(this.pipelineDefinition.getId());
        this.pipelineService.add(this.pipeline);
        this.stage = new Stage();
        this.stage.setPipelineId(this.pipeline.getId());
        this.stageService.add(this.stage);
        this.job = new Job();
        this.job.setStageId(this.stage.getId());
        String expectedMessage = "Job not found.";
        boolean expectedError = true;

        //Act
        ServiceResult actualResult = this.jobService.update(this.job);

        //Assert
        assertNull(actualResult.getObject());
        assertEquals(expectedMessage, actualResult.getMessage());
        assertEquals(expectedError, actualResult.hasError());
    }

    //TODO: Avoid reliance on this.jobService.add()
    private void insertIntoDb() {
        this.pipelineDefinition = new PipelineDefinition();
        this.pipelineDefinition.setName("pipelinedefinition");
        this.pipelineDefinitionService.add(this.pipelineDefinition);
        this.pipeline = new Pipeline();
        this.pipeline.setPipelineDefinitionName(this.pipelineDefinition.getName());
        this.pipeline.setPipelineDefinitionId(this.pipelineDefinition.getId());
        this.pipelineService.add(this.pipeline);
        this.stage = new Stage();
        this.stage.setPipelineId(this.pipeline.getId());
        this.stageService.add(this.stage);
        this.job = new Job();
        this.job.setStageId(this.stage.getId());
        this.jobService.add(this.job);
    }
}
