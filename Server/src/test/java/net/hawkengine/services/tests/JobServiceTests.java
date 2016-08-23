package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.services.*;
import net.hawkengine.services.interfaces.*;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class JobServiceTests {
    private IPipelineService pipelineService;
    private IPipelineDefinitionService pipelineDefinitionService;
    private IMaterialDefinitionService materialDefinitionService;
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
        IDbRepository materialDefinitionRepo = new RedisRepository(MaterialDefinition.class, mockJedisPool);
        this.pipelineDefinitionService = new PipelineDefinitionService(pipelineDefinitionRepository);
        this.materialDefinitionService = new MaterialDefinitionService(materialDefinitionRepo, this.pipelineDefinitionService);
        this.pipelineService = new PipelineService(pipelineRepository, this.pipelineDefinitionService, this.materialDefinitionService);
        this.stageService = new StageService(pipelineService);
        this.jobService = new JobService(stageService);
    }

    @Test
    public void getById_validId_successMessage() {
        //Arrange
        this.insertIntoDb();
        this.jobService.add(this.job);
        String expectedMessage = "Job " + this.job.getId() + " retrieved successfully.";

        //Act
        ServiceResult actualResult = this.jobService.getById(this.job.getId());
        Job actualJob = (Job) actualResult.getObject();

        //Assert
        assertNotNull(actualResult.getObject());
        assertFalse(actualResult.hasError());
        assertEquals(this.job.getId(), actualJob.getId());
        assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getById_invalidId_errorMessage() {
        //Arrange
        UUID randomId = UUID.randomUUID();
        String expectedMessage = "Job not found.";

        //Act
        ServiceResult actualResult = this.jobService.getById(randomId.toString());

        //Assert
        assertNull(actualResult.getObject());
        assertTrue(actualResult.hasError());
        assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getAll_oneObject_successMessage() {
        //Arrange
        this.insertIntoDb();
        this.jobService.add(this.job);
        String expectedMessage = "Jobs retrieved successfully.";

        //Act
        ServiceResult actualResult = this.jobService.getAll();
        List<Job> actualObject = (List<Job>) actualResult.getObject();

        //Assert
        assertNotNull(actualResult.getObject());
        assertFalse(actualResult.hasError());
        assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualObject.size());
        assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getAll_emptyList_noErrors() {
        //Arrange
        String expectedMessage = "Jobs retrieved successfully.";

        //Act
        ServiceResult actualResult = this.jobService.getAll();
        List<Job> actualObject = (List<Job>) actualResult.getObject();

        //Assert
        assertNotNull(actualResult.getObject());
        assertFalse(actualResult.hasError());
        assertTrue(actualObject.isEmpty());
        assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void add_nonExistingObject_successMessage() {
        //Arrange
        this.insertIntoDb();
        String expectedMessage = "Job " + this.job.getId() + " created successfully.";

        //Act
        ServiceResult actualResult = this.jobService.add(this.job);
        Job actualObject = (Job) actualResult.getObject();

        //Assert
        assertNotNull(actualObject);
        assertFalse(actualResult.hasError());
        assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void add_existingObject_errorMessage() {
        //Arrange
        this.insertIntoDb();
        this.jobService.add(this.job);
        String expectedMessage = "Job already exists.";

        //Act
        ServiceResult actualResult = this.jobService.add(this.job);

        //Assert
        assertNull(actualResult.getObject());
        assertTrue(actualResult.hasError());
        assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void update_existingObject_successMessage() {
        //Act
        this.insertIntoDb();
        this.jobService.add(this.job);
        this.job.setStatus(JobStatus.PASSED);
        String expectedMessage = "Job " + job.getId() + " " + "updated successfully.";

        //Act
        ServiceResult actualResult = this.jobService.update(this.job);
        Job job = (Job) actualResult.getObject();

        //Assert
        assertNotNull(actualResult.getObject());
        assertFalse(actualResult.hasError());
        assertEquals(expectedMessage, actualResult.getMessage());
        assertEquals(this.job.getStatus(), job.getStatus());
    }

    @Test
    public void update_nonExistingObject_errorMessage() {
        //Arrange
        this.insertIntoDb();
        String expectedMessage = "Job not found.";

        //Act
        ServiceResult actualResult = this.jobService.update(this.job);

        //Assert
        assertNull(actualResult.getObject());
        assertEquals(expectedMessage, actualResult.getMessage());
        assertTrue(actualResult.hasError());
    }

    @Test
    public void delete_existingJob_successMessage() {
        //Arrange
        this.insertIntoDb();
        this.jobService.add(this.job);
        String expectedMessage = "Job " + this.job.getId() + " deleted successfully.";

        //Act
        ServiceResult actualResult = this.jobService.delete(this.job.getId());

        //Assert
        assertFalse(actualResult.hasError());
        assertNotNull(actualResult.getObject());
        assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void delete_nonExistingJob_errorMessage() {
        //Arrange
        this.insertIntoDb();
        String expectedMessage = "Job not found.";

        //Act
        ServiceResult result = this.jobService.delete(this.job.getId());

        //Assert
        assertTrue(result.hasError());
        assertNull(result.getObject());
        assertEquals(expectedMessage, result.getMessage());
    }

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
    }
}
