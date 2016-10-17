package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.RevisionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PipelineDefinitionServiceTests {
    private IDbRepository<PipelineDefinition> mockedRepository;
    private IPipelineDefinitionService mockedPipeLineDefinitionService;
    private IPipelineService mockedPipelineService;
    private RevisionService mockedRevisionService;

    @BeforeClass
    public static void setUpClass() {
        ServerConfiguration.configure();
    }

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelineDefinitionService");
        this.mockedRepository = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.mockedPipelineService = Mockito.mock(PipelineService.class);
        this.mockedRevisionService = Mockito.mock(RevisionService.class);
        this.mockedPipeLineDefinitionService = new PipelineDefinitionService(this.mockedRepository, this.mockedPipelineService, this.mockedRevisionService);
    }

    @Test
    public void getById_withValidId_oneObject() {
        PipelineDefinition expectedPipelineDefinition = new PipelineDefinition();
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " " + expectedPipelineDefinition.getId() + " retrieved successfully.";
        this.mockedRepository.add(expectedPipelineDefinition);

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.getById(expectedPipelineDefinition.getId());
        PipelineDefinition actualPipelineDefinition = (PipelineDefinition) actualResult.getObject();

        Assert.assertNotNull(actualPipelineDefinition);
        Assert.assertEquals(expectedPipelineDefinition.getId(), actualPipelineDefinition.getId());
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getById_withInvalidId_noObject() {
        UUID invalidId = UUID.randomUUID();
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " " + "not found.";

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.getById(invalidId.toString());

        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getAll_withExistingObjects_twoObjects() {

        PipelineDefinition firstExpectedPipelineDefinition = new PipelineDefinition();
        PipelineDefinition secondExpectedPipelineDefinition = new PipelineDefinition();
        String expectedMessage = PipelineDefinition.class.getSimpleName() + "s retrieved successfully.";

        this.mockedPipeLineDefinitionService.add(firstExpectedPipelineDefinition);
        this.mockedPipeLineDefinitionService.add(secondExpectedPipelineDefinition);
        ServiceResult actualResult = this.mockedPipeLineDefinitionService.getAll();
        List<PipelineDefinition> actualResultObject = (List<PipelineDefinition>) actualResult.getObject();
        PipelineDefinition firstActualPipelineDefinition = actualResultObject
                .stream()
                .filter(p -> p.getId().equals(firstExpectedPipelineDefinition.getId()))
                .collect(Collectors.toList())
                .get(0);

        PipelineDefinition secondActualPipelineDefinition = actualResultObject
                .stream()
                .filter(p -> p.getId().equals(secondExpectedPipelineDefinition.getId()))
                .collect(Collectors.toList())
                .get(0);

        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertEquals(firstExpectedPipelineDefinition.getId(), firstActualPipelineDefinition.getId());
        Assert.assertEquals(secondExpectedPipelineDefinition.getId(), secondActualPipelineDefinition.getId());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_TWO_OBJECTS, actualResultObject.size());
    }

    @Test
    public void getAll_withNonexistentObjects_noObjects() {
        String expectedMessage = PipelineDefinition.class.getSimpleName() + "s retrieved successfully.";

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.getAll();
        List<PipelineDefinition> actualResultObject = (List<PipelineDefinition>) actualResult.getObject();

        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS, actualResultObject.size());
    }

    @Test
    public void add_validObject_oneObject() {
        PipelineDefinition expectedPipelineDefinition = new PipelineDefinition();
        StageDefinition expectedStageDefinition = new StageDefinition();
        JobDefinition expectedJobDefinition = new JobDefinition();
        ExecTask expectedExecutionTask = new ExecTask();

        List<TaskDefinition> expectedTaskDefinitions = new ArrayList<>();
        expectedTaskDefinitions.add(expectedExecutionTask);
        expectedJobDefinition.setTaskDefinitions(expectedTaskDefinitions);

        List<JobDefinition> expectedJobDefinitions = new ArrayList<>();
        expectedJobDefinitions.add(expectedJobDefinition);
        expectedStageDefinition.setJobDefinitions(expectedJobDefinitions);

        List<StageDefinition> expectedStageDefinitions = new ArrayList<>();
        expectedStageDefinitions.add(expectedStageDefinition);

        expectedPipelineDefinition.setStageDefinitions(expectedStageDefinitions);

        String expectedMessage = expectedPipelineDefinition.getClass().getSimpleName() + " " + expectedPipelineDefinition.getId() + " created successfully.";

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.add(expectedPipelineDefinition);
        PipelineDefinition actualPipelineDefinition = (PipelineDefinition) actualResult.getObject();
        int actualCollectionSize = this.mockedRepository.getAll().size();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualCollectionSize);
        Assert.assertNotNull(actualPipelineDefinition);
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertEquals(expectedPipelineDefinition.getId(), actualPipelineDefinition.getId());
        Assert.assertEquals(expectedStageDefinition.getId(), actualPipelineDefinition.getStageDefinitions().get(0).getId());
        Assert.assertEquals(expectedJobDefinition.getId(), actualPipelineDefinition.getStageDefinitions().get(0).getJobDefinitions().get(0).getId());
        Assert.assertEquals(expectedExecutionTask.getId(), actualPipelineDefinition.getStageDefinitions().get(0).getJobDefinitions().get(0).getTaskDefinitions().get(0).getId());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void add_existingObject_noObject() {
        PipelineDefinition expectedPipelineDefinition = new PipelineDefinition();
        String expectedMessage = expectedPipelineDefinition.getClass().getSimpleName() + " " + "already exists.";

        this.mockedPipeLineDefinitionService.add(expectedPipelineDefinition);
        ServiceResult actualResult = this.mockedPipeLineDefinitionService.add(expectedPipelineDefinition);

        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void update_existingObject_oneObject() {
        PipelineDefinition expectedResult = new PipelineDefinition();
        expectedResult.setName("BeforeUpdateName");
        this.mockedPipeLineDefinitionService.add(expectedResult);
        String expectedName = "AfterUpdateName";
        expectedResult.setName(expectedName);
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " " + expectedResult.getId() + " updated successfully.";

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.update(expectedResult);
        PipelineDefinition actualResultObject = (PipelineDefinition) actualResult.getObject();

        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertEquals(expectedName, actualResultObject.getName());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void update_nonexistentObject_noObject() {
        PipelineDefinition expectedResult = new PipelineDefinition();
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " " + "not found.";

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.update(expectedResult);

        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void delete_existingObject_true() {
        PipelineDefinition pipelineToDelete = new PipelineDefinition();
        this.mockedPipeLineDefinitionService.add(pipelineToDelete);
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " deleted successfully.";

        ServiceResult mockedGetAllPipelinesServiceResult = new ServiceResult();
        mockedGetAllPipelinesServiceResult.setMessage("Pipelines retrieved successfully");
        mockedGetAllPipelinesServiceResult.setNotificationType(NotificationType.SUCCESS);
        mockedGetAllPipelinesServiceResult.setObject(null);

        Mockito.when(this.mockedPipelineService.getAll()).thenReturn(mockedGetAllPipelinesServiceResult);

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.delete(pipelineToDelete.getId());

        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertNotNull(actualResult.getObject());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void delete_nonexistentObject_false() {
        //Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " not found.";

        ServiceResult mockedGetAllPipelinesServiceResult = new ServiceResult();
        mockedGetAllPipelinesServiceResult.setMessage("Pipelines retrieved successfully");
        mockedGetAllPipelinesServiceResult.setNotificationType(NotificationType.SUCCESS);
        mockedGetAllPipelinesServiceResult.setObject(null);

        Mockito.when(this.mockedPipelineService.getAll()).thenReturn(mockedGetAllPipelinesServiceResult);

        //Act
        ServiceResult actualResult = this.mockedPipeLineDefinitionService.delete(pipelineDefinition.getId());

        //Assert
        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }
}