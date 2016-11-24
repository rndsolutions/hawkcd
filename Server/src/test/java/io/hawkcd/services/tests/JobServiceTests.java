package io.hawkcd.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;

import io.hawkcd.Config;
import io.hawkcd.utilities.constants.TestsConstants;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.db.redis.RedisRepository;
import io.hawkcd.model.Job;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.Pipeline;
import io.hawkcd.model.PipelineDefinition;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.Stage;
import io.hawkcd.model.enums.JobStatus;
import io.hawkcd.model.enums.NotificationType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.hawkcd.services.JobService;
import io.hawkcd.services.MaterialDefinitionService;
import io.hawkcd.services.PipelineDefinitionService;
import io.hawkcd.services.PipelineService;
import io.hawkcd.services.StageService;
import io.hawkcd.services.interfaces.IJobService;
import io.hawkcd.services.interfaces.IMaterialDefinitionService;
import io.hawkcd.services.interfaces.IPipelineDefinitionService;
import io.hawkcd.services.interfaces.IPipelineService;
import io.hawkcd.services.interfaces.IStageService;
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

    @BeforeClass
    public static void setUpClass() {
        Config.configure();
    }

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
        Job actualJob = (Job) actualResult.getEntity();

        //Assert
        assertNotNull(actualResult.getEntity());
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
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
        assertNull(actualResult.getEntity());
        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
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
        List<Job> actualObject = (List<Job>) actualResult.getEntity();

        //Assert
        assertNotNull(actualResult.getEntity());
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualObject.size());
        assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getAll_emptyList_noErrors() {
        //Arrange
        String expectedMessage = "Jobs retrieved successfully.";

        //Act
        ServiceResult actualResult = this.jobService.getAll();
        List<Job> actualObject = (List<Job>) actualResult.getEntity();

        //Assert
        assertNotNull(actualResult.getEntity());
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
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
        Job actualObject = (Job) actualResult.getEntity();

        //Assert
        assertNotNull(actualObject);
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
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
        assertNull(actualResult.getEntity());
        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
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
        Job job = (Job) actualResult.getEntity();

        //Assert
        assertNotNull(actualResult.getEntity());
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
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
        assertNull(actualResult.getEntity());
        assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
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
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        assertNotNull(actualResult.getEntity());
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
        assertEquals(NotificationType.ERROR, result.getNotificationType());
        assertNull(result.getEntity());
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
