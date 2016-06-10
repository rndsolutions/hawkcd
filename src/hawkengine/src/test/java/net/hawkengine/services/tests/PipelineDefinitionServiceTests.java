package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.IPipelineDefinitionService;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.interfaces.ICrudService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import redis.clients.jedis.JedisPoolConfig;

public class PipelineDefinitionServiceTests {

    private IDbRepository<PipelineDefinition> pipelineRepo;
    private IPipelineDefinitionService pipeLineDefinitionService;

    @Before
    public void setUp() throws Exception {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelineService");
        this.pipelineRepo = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.pipeLineDefinitionService = new PipelineDefinitionService(this.pipelineRepo);
    }

    @Test
    public void getById_withInvalidId_shouldReturnInvalidModelState() throws Exception {
        UUID fakeID = UUID.randomUUID();
        ServiceResult actual = this.pipeLineDefinitionService.getById(fakeID.toString());
        String expectedMessage = "Object with id " + fakeID + " not found.";

        Assert.assertTrue(actual.hasError());
        Assert.assertNull(actual.getObject());
//        Assert.assertEquals(expectedMessage, actual.getMessage());
    }

    @Test
    public void getById_withValidId_shouldReturnValidModelState() throws Exception {
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        this.pipelineRepo.add(pipelineDefinition);

        ServiceResult actual = this.pipeLineDefinitionService.getById(pipelineDefinition.getId());
        PipelineDefinition actualObject = (PipelineDefinition) actual.getObject();
        String successMessage = actualObject.getClass().getSimpleName() + " with id " + actualObject.getId() + " retrieved successfully.";

        Assert.assertNotNull(actualObject);
        Assert.assertEquals(pipelineDefinition.getId(), actualObject.getId());
        Assert.assertFalse(actual.hasError());
//        Assert.assertEquals(successMessage, actual.getMessage());
    }

    @Test
    public void add_validValue_shouldReturnValidModelState() throws Exception {
        PipelineDefinition expected = new PipelineDefinition();
        expected.setName("Peshos Pipe");

        ServiceResult fromRepo = this.pipeLineDefinitionService.add(expected);
        PipelineDefinition actual = (PipelineDefinition) fromRepo.getObject();
        String expectedMessage = actual.getClass().getSimpleName() + " with id " + actual.getId() + " retrieved successfully.";
        int repoResultsCount = this.pipelineRepo.getAll().size();

        Assert.assertEquals(1, repoResultsCount);
        Assert.assertNotNull(actual);
        Assert.assertFalse(fromRepo.hasError());
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getName(), actual.getName());
//        Assert.assertEquals(expectedMessage, fromRepo.getMessage());
    }

    @Test
    public void add_existingValue_shouldReturnInvalidModelState() throws Exception {
        PipelineDefinition modelToAdd = new PipelineDefinition();

        this.pipeLineDefinitionService.add(modelToAdd);
        ServiceResult actual = this.pipeLineDefinitionService.add(modelToAdd);
        String expectedMessage = modelToAdd.getClass().getSimpleName() + " with id " + modelToAdd.getId() + " already exist.";

        Assert.assertTrue(actual.hasError());
        Assert.assertNull(actual.getObject());
//        Assert.assertEquals(expectedMessage, actual.getMessage());
    }

    @Test
    public void get_all_should_return_empty_list_when_empty_repo_is_provided() {
        int expected = 0;

        ServiceResult result = this.pipeLineDefinitionService.getAll();
        List<PipelineDefinition> actual = (List<PipelineDefinition>) result.getObject();

        Assert.assertEquals(actual.size(), expected);
        Assert.assertEquals(false, result.hasError());
//        Assert.assertEquals(result.getMessage(), null);
    }

    @Test
    public void getAll_shouldReturnValidModelState() throws Exception {
        int expectedSize = 2;
        PipelineDefinition firstPipeline = new PipelineDefinition();
        firstPipeline.setName("firstPipeLineName");
        PipelineDefinition secondPipeline = new PipelineDefinition();
        secondPipeline.setName("secondPipeLineName");

        this.pipeLineDefinitionService.add(firstPipeline);
        this.pipeLineDefinitionService.add(secondPipeline);
        ServiceResult actual = this.pipeLineDefinitionService.getAll();
        List<PipelineDefinition> actualCollection = (List<PipelineDefinition>) actual.getObject();
        String expectedMessage = actualCollection.get(0).getClass().getSimpleName() + "s retrieved successfully.";

        PipelineDefinition firstExpectedPipeline = (PipelineDefinition) this.pipeLineDefinitionService
                .getById(firstPipeline.getId())
                .getObject();

        PipelineDefinition secondExpectedPipeline = (PipelineDefinition) this.pipeLineDefinitionService
                .getById(secondPipeline.getId())
                .getObject();

        Assert.assertEquals(expectedSize, actualCollection.size());
        Assert.assertFalse(actual.hasError());
        Assert.assertEquals(firstPipeline.getName(), firstExpectedPipeline.getName());
        Assert.assertEquals(secondPipeline.getName(), secondExpectedPipeline.getName());
//        Assert.assertEquals(expectedMessage, actual.getMessage());
    }

    @Test
    public void update_withInvalidInput_shouldReturnInvalidModelState() throws Exception {
        PipelineDefinition pipelineToUpdate = new PipelineDefinition();
        ServiceResult result = this.pipeLineDefinitionService.update(pipelineToUpdate);
        String expectedMessage = pipelineToUpdate.getClass().getSimpleName() + " " + pipelineToUpdate.getId() + " not found.";

        Assert.assertTrue(result.hasError());
        Assert.assertNull(result.getObject());
        Assert.assertEquals(expectedMessage, result.getMessage());
    }

    @Test
    public void update_withValidInput_shouldReturnValidModelState() throws Exception {
        String expectedName = "AfterUpdateName";
        PipelineDefinition pipelineToUpdate = new PipelineDefinition();
        pipelineToUpdate.setName("BeforeUpdateName");

        this.pipeLineDefinitionService.add(pipelineToUpdate);
        PipelineDefinition fromDB = (PipelineDefinition) this.pipeLineDefinitionService
                .getById(pipelineToUpdate.getId())
                .getObject();

        fromDB.setName(expectedName);
        ServiceResult updated = this.pipeLineDefinitionService.update(fromDB);
        PipelineDefinition actualObject = (PipelineDefinition) updated.getObject();
        String expectedMessage = actualObject.getClass().getSimpleName() + " " + actualObject.getId() + " updated successfully.";

        Assert.assertFalse(updated.hasError());
        Assert.assertEquals(expectedName, actualObject.getName());
        Assert.assertEquals(expectedMessage, updated.getMessage());
    }

    @Test
    public void delete_withInvalidInput_shouldReturnInvalidModelState() throws Exception {
        PipelineDefinition pipelineToDelete = new PipelineDefinition();
        ServiceResult actual = this.pipeLineDefinitionService.delete(pipelineToDelete.getId());
        String expectedMessage = "Object with id " + pipelineToDelete.getId() + " not found.";


        Assert.assertTrue(actual.hasError());
        Assert.assertNull(actual.getObject());
//        Assert.assertEquals(expectedMessage, actual.getMessage());
    }

    @Test
    public void delete_withValidInput_shouldReturnValidModelState() throws Exception {
        PipelineDefinition pipelineToDelete = new PipelineDefinition();
        this.pipeLineDefinitionService.add(pipelineToDelete);
        ServiceResult actual = this.pipeLineDefinitionService.delete(pipelineToDelete.getId());
        String expectedMessage = "Object with id " + pipelineToDelete.getId() + " deleted successfully.";

        Assert.assertFalse(actual.hasError());
        Assert.assertNotNull(actual.getObject());
//        Assert.assertEquals(expectedMessage, actual.getMessage());
    }
}
