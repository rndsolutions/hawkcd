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
import java.util.stream.Collectors;

import redis.clients.jedis.JedisPoolConfig;

public class PipelineDefinitionServiceTests {

    private IDbRepository<PipelineDefinition> mockedRepository;
    private IPipelineDefinitionService mockedPipeLineDefinitionService;

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelineService");
        this.mockedRepository = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.mockedPipeLineDefinitionService = new PipelineDefinitionService(this.mockedRepository);
    }

    @Test
    public void getById_withValidId_validObject() {
        PipelineDefinition expectedResult = new PipelineDefinition();
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " " + expectedResult.getId() + " retrieved successfully.";
        this.mockedRepository.add(expectedResult);

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.getById(expectedResult.getId());
        PipelineDefinition actualResultObject = (PipelineDefinition) actualResult.getObject();

        Assert.assertNotNull(actualResultObject);
        Assert.assertEquals(expectedResult.getId(), actualResultObject.getId());
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getById_withInvalidId_null() {
        UUID invalidId = UUID.randomUUID();
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " " + invalidId + " not found.";

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.getById(invalidId.toString());

        Assert.assertTrue(actualResult.hasError());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getAll_withExistingObjects_validObjects() {
        PipelineDefinition firstExpectedResult = new PipelineDefinition();
        PipelineDefinition secondExpectedResult = new PipelineDefinition();
        String expectedMessage = PipelineDefinition.class.getSimpleName() + "s retrieved successfully.";
        int expectedCollectionSize = 2;

        this.mockedPipeLineDefinitionService.add(firstExpectedResult);
        this.mockedPipeLineDefinitionService.add(secondExpectedResult);
        ServiceResult actualResult = this.mockedPipeLineDefinitionService.getAll();
        List<PipelineDefinition> actualResultObject = (List<PipelineDefinition>) actualResult.getObject();
        PipelineDefinition firstActualResult = actualResultObject
                .stream()
                .filter(p -> p.getId().equals(firstExpectedResult.getId()))
                .collect(Collectors.toList())
                .get(0);

        PipelineDefinition secondActualResult = actualResultObject
                .stream()
                .filter(p -> p.getId().equals(secondExpectedResult.getId()))
                .collect(Collectors.toList())
                .get(0);

        Assert.assertEquals(expectedCollectionSize, actualResultObject.size());
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(firstExpectedResult.getId(), firstActualResult.getId());
        Assert.assertEquals(secondExpectedResult.getId(), secondActualResult.getId());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getAll_withNonexistentObjects_null() {
        String expectedMessage = PipelineDefinition.class.getSimpleName() + "s retrieved successfully.";
        int expectedCollectionSize = 0;

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.getAll();
        List<PipelineDefinition> actualResultObject = (List<PipelineDefinition>) actualResult.getObject();

        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertEquals(expectedCollectionSize, actualResultObject.size());
    }

    @Test
    public void add_newObject_validObject() {
        PipelineDefinition expectedResult = new PipelineDefinition();
        String expectedMessage = expectedResult.getClass().getSimpleName() + " " + expectedResult.getId() + " created successfully.";
        int expectedCollectionSize = 1;

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.add(expectedResult);
        PipelineDefinition actualResultObject = (PipelineDefinition) actualResult.getObject();
        int actualCollectionSize = this.mockedRepository.getAll().size();

        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
        Assert.assertNotNull(actualResultObject);
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedResult.getId(), actualResultObject.getId());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void add_existingObject_null() throws Exception {
        PipelineDefinition expectedResult = new PipelineDefinition();
        String expectedMessage = expectedResult.getClass().getSimpleName() + " " + expectedResult.getId() + " already exists.";

        this.mockedPipeLineDefinitionService.add(expectedResult);
        ServiceResult actualResult = this.mockedPipeLineDefinitionService.add(expectedResult);

        Assert.assertTrue(actualResult.hasError());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void update_existingObject_validObject() {
        PipelineDefinition expectedResult = new PipelineDefinition();
        expectedResult.setName("BeforeUpdateName");
        this.mockedPipeLineDefinitionService.add(expectedResult);
        String expectedName = "AfterUpdateName";
        expectedResult.setName(expectedName);
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " " + expectedResult.getId() + " updated successfully.";

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.update(expectedResult);
        PipelineDefinition actualResultObject = (PipelineDefinition) actualResult.getObject();

        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedName, actualResultObject.getName());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void update_nonexistentObject_null() {
        PipelineDefinition expectedResult = new PipelineDefinition();
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " " + expectedResult.getId() + " not found.";

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.update(expectedResult);

        Assert.assertTrue(actualResult.hasError());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void delete_existingObject_true() {
        PipelineDefinition pipelineToDelete = new PipelineDefinition();
        this.mockedPipeLineDefinitionService.add(pipelineToDelete);
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " deleted successfully.";

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.delete(pipelineToDelete.getId());

        Assert.assertFalse(actualResult.hasError());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void delete_nonexistentObject_false() throws Exception {
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        String expectedMessage = PipelineDefinition.class.getSimpleName() + " not found.";

        ServiceResult actualResult = this.mockedPipeLineDefinitionService.delete(pipelineDefinition.getId());

        Assert.assertTrue(actualResult.hasError());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }
}
