package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import redis.clients.jedis.JedisPoolConfig;

public class PipelineDefinitionServiceTests {

    private IDbRepository<PipelineDefinition> mockedPipelineRepo;
    private IPipelineDefinitionService mockedPipeLineDefinitionService;

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelineService");
        this.mockedPipelineRepo = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.mockedPipeLineDefinitionService = new PipelineDefinitionService(this.mockedPipelineRepo);
    }

    @Test
    public void getById_withValidId_shouldReturnValidModelState() {
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        this.mockedPipelineRepo.add(pipelineDefinition);

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.getById(pipelineDefinition.getId());
        PipelineDefinition actualResultObject = (PipelineDefinition) actualResult.getObject();
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " " + actualResultObject.getId() + " retrieved successfully.";

        Assert.assertNotNull(actualResultObject);
        Assert.assertEquals(pipelineDefinition.getId(), actualResultObject.getId());
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getById_withInvalidId_shouldReturnInvalidModelState() {
        UUID fakeID = UUID.randomUUID();
        ServiceResult actualResult = this.mockedPipeLineDefinitionService.getById(fakeID.toString());
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " " + fakeID + " not found.";

        Assert.assertTrue(actualResult.hasError());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void add_nonExistingObject_shouldReturnValidObject() throws Exception {
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("Test PipelineDefinition");

        ServiceResult actualResultObject = this.mockedPipeLineDefinitionService.add(pipelineDefinition);
        PipelineDefinition actualObjectResult = (PipelineDefinition) actualResultObject.getObject();
        String expectedMessage = actualObjectResult.getClass().getSimpleName() + " " + actualObjectResult.getId() + " created successfully.";
        int actualCollectionSize = this.mockedPipelineRepo.getAll().size();

        Assert.assertEquals(1, actualCollectionSize);
        Assert.assertNotNull(actualObjectResult);
        Assert.assertFalse(actualResultObject.hasError());
        Assert.assertEquals(pipelineDefinition.getId(), actualObjectResult.getId());
        Assert.assertEquals(pipelineDefinition.getName(), actualObjectResult.getName());
        Assert.assertEquals(expectedMessage, actualResultObject.getMessage());
    }

    @Test
    public void add_existingObject_shouldReturnInvalidObject() throws Exception {
        PipelineDefinition pipelineDefinition = new PipelineDefinition();

        this.mockedPipeLineDefinitionService.add(pipelineDefinition);
        ServiceResult actualResultObject = this.mockedPipeLineDefinitionService.add(pipelineDefinition);
        String expectedMessage = pipelineDefinition.getClass().getSimpleName() + " " + pipelineDefinition.getId() + " already exists.";

        Assert.assertTrue(actualResultObject.hasError());
        Assert.assertNull(actualResultObject.getObject());
        Assert.assertEquals(expectedMessage, actualResultObject.getMessage());
    }

    @Test
    public void getAll_shouldReturnValidObject() throws Exception {
        int expectedCollectionSize = 2;
        PipelineDefinition firstExpectedPipeline = new PipelineDefinition();
        firstExpectedPipeline.setName("firstPipeLineName");
        PipelineDefinition secondExpectedPipeline = new PipelineDefinition();
        secondExpectedPipeline.setName("secondPipeLineName");

        this.mockedPipeLineDefinitionService.add(firstExpectedPipeline);
        this.mockedPipeLineDefinitionService.add(secondExpectedPipeline);
        ServiceResult actualResultObject = this.mockedPipeLineDefinitionService.getAll();
        List<PipelineDefinition> actualCollection = (List<PipelineDefinition>) actualResultObject.getObject();
        String expectedMessage = PipelineDefinition.class.getSimpleName() + "s retrieved successfully.";

        PipelineDefinition firstActualPipeline = (PipelineDefinition) this.mockedPipeLineDefinitionService
                .getById(firstExpectedPipeline.getId())
                .getObject();

        PipelineDefinition secondActualPipeline = (PipelineDefinition) this.mockedPipeLineDefinitionService
                .getById(secondExpectedPipeline.getId())
                .getObject();

        Assert.assertEquals(expectedCollectionSize, actualCollection.size());
        Assert.assertFalse(actualResultObject.hasError());
        Assert.assertEquals(firstExpectedPipeline.getName(), firstActualPipeline.getName());
        Assert.assertEquals(secondExpectedPipeline.getName(), secondActualPipeline.getName());
        Assert.assertEquals(expectedMessage, actualResultObject.getMessage());
    }

    @Test
    public void getAll_shouldReturnInvalidObject() {
        int expectedCollectionSize = 0;

        ServiceResult result = this.mockedPipeLineDefinitionService.getAll();
        List<PipelineDefinition> actual = (List<PipelineDefinition>) result.getObject();
        String expectedMessage = PipelineDefinition.class.getSimpleName() + "s retrieved successfully.";

        Assert.assertEquals(actual.size(), expectedCollectionSize);
        Assert.assertFalse(result.hasError());
        Assert.assertEquals(expectedMessage, result.getMessage());
    }

    @Test
    public void update_withValidInput_shouldReturnValidObject() throws Exception {
        String expectedName = "AfterUpdateName";
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("BeforeUpdateName");

        this.mockedPipeLineDefinitionService.add(pipelineDefinition);
        PipelineDefinition fromDB = (PipelineDefinition) this.mockedPipeLineDefinitionService
                .getById(pipelineDefinition.getId())
                .getObject();

        fromDB.setName(expectedName);
        ServiceResult actualResultObject = this.mockedPipeLineDefinitionService.update(fromDB);
        PipelineDefinition actualObject = (PipelineDefinition) actualResultObject.getObject();
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " " + actualObject.getId() + " updated successfully.";

        Assert.assertFalse(actualResultObject.hasError());
        Assert.assertEquals(expectedName, actualObject.getName());
        Assert.assertEquals(expectedMessage, actualResultObject.getMessage());
    }

    @Test
    public void update_withInvalidInput_shouldReturnInvalidObject() throws Exception {
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        ServiceResult actualResultObject = this.mockedPipeLineDefinitionService.update(pipelineDefinition);
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " " + pipelineDefinition.getId() + " not found.";

        Assert.assertTrue(actualResultObject.hasError());
        Assert.assertNull(actualResultObject.getObject());
        Assert.assertEquals(expectedMessage, actualResultObject.getMessage());
    }

    @Test
    public void delete_withValidInput_shouldReturnValidObject() throws Exception {
        PipelineDefinition pipelineToDelete = new PipelineDefinition();

        this.mockedPipeLineDefinitionService.add(pipelineToDelete);
        ServiceResult actual = this.mockedPipeLineDefinitionService.delete(pipelineToDelete.getId());
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " deleted successfully.";

        Assert.assertFalse(actual.hasError());
        Assert.assertNull(actual.getObject());
        Assert.assertEquals(expectedMessage, actual.getMessage());
    }

    @Test
    public void delete_withInvalidInput_shouldReturnInvalidObject() throws Exception {
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        ServiceResult actual = this.mockedPipeLineDefinitionService.delete(pipelineDefinition.getId());
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " not found.";

        Assert.assertTrue(actual.hasError());
        Assert.assertNull(actual.getObject());
        Assert.assertEquals(expectedMessage, actual.getMessage());
    }
}
