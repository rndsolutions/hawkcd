package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.RunIf;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

public class PipelineServiceTests {
    private IDbRepository<Pipeline> mockedPipelineRepository;
    private IDbRepository<PipelineDefinition> mockedPipelineDefinitionRepository;
    private IPipelineService mockedPipelineService;
    private IPipelineDefinitionService mockedPipelineDefinitionService;
    private PipelineDefinition expectedPipelineDefinition;

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelineService");
        this.mockedPipelineRepository = new RedisRepository(Pipeline.class, mockedPool);
        this.mockedPipelineDefinitionRepository = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.mockedPipelineDefinitionService = new PipelineDefinitionService(this.mockedPipelineDefinitionRepository);
        this.mockedPipelineService = new PipelineService(this.mockedPipelineRepository, this.mockedPipelineDefinitionService);
        this.expectedPipelineDefinition = new PipelineDefinition();
        this.mockedPipelineDefinitionService.add(this.expectedPipelineDefinition);
    }

    @Test
    public void getById_withValidId_oneObject() {
        Pipeline expectedPipeline = new Pipeline();
        expectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        this.mockedPipelineService.add(expectedPipeline);

        ServiceResult actualResult = this.mockedPipelineService.getById(expectedPipeline.getId());
        Pipeline actualPipeline = (Pipeline) actualResult.getObject();

        Assert.assertEquals(expectedPipeline.getId(), actualPipeline.getId());
        Assert.assertFalse(actualResult.hasError());
    }

    @Test
    public void getById_withInvalidId_noObject() {
        Pipeline expectedPipeline = new Pipeline();
        expectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        this.mockedPipelineService.add(expectedPipeline);

        ServiceResult actualResult = this.mockedPipelineService.getById("someInvalidId");

        Assert.assertEquals(actualResult.getObject(), null);
        Assert.assertTrue(actualResult.hasError());
    }

    @Test
    public void getAll_withExistingObjects_twoObjects() {
        Pipeline firstExpectedPipeline = new Pipeline();
        firstExpectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        Pipeline secondExpectedPipeline = new Pipeline();
        secondExpectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        this.mockedPipelineService.add(firstExpectedPipeline);
        this.mockedPipelineService.add(secondExpectedPipeline);

        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_TWO_OBJECTS;

        ServiceResult actualResult = this.mockedPipelineService.getAll();
        List<Pipeline> actualPipelines = (List<Pipeline>) actualResult.getObject();

        Assert.assertEquals(expectedCollectionSize, actualPipelines.size());
        Assert.assertFalse(actualResult.hasError());
    }

    @Test
    public void getAll_withNonexistentObjects_noObjects() {
        ServiceResult actualResult = this.mockedPipelineService.getAll();
        List<Pipeline> actualPipelines = (List<Pipeline>) actualResult.getObject();

        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS, actualPipelines.size());
    }

    @Test
    public void add_validObject_oneObject() {
        Pipeline expectedPipeline = new Pipeline();
        expectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        ServiceResult actualResult = this.mockedPipelineService.add(expectedPipeline);
        Pipeline actualPipeline = (Pipeline) actualResult.getObject();
        int actualCollectionSize = ((List<Pipeline>) this.mockedPipelineService.getAll().getObject()).size();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualCollectionSize);
        Assert.assertNotNull(actualPipeline);
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedPipeline.getId(), actualPipeline.getId());
    }

    @Test
    public void add_validObjectWithStage_oneObject() {
        List<StageDefinition> expectedStageDefinitions = new ArrayList<>();
        expectedStageDefinitions.add(new StageDefinition());
        this.expectedPipelineDefinition.setStageDefinitions(expectedStageDefinitions);
        this.mockedPipelineDefinitionRepository.update(this.expectedPipelineDefinition);
        Pipeline expectedPipeline = new Pipeline();
        expectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        ServiceResult actualResult = this.mockedPipelineService.add(expectedPipeline);
        Pipeline actualPipeline = (Pipeline) actualResult.getObject();
        int actualCollectionSize = actualPipeline.getStages().size();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualCollectionSize);
        Assert.assertFalse(actualResult.hasError());
    }
    @Test
    public void add_validObjectWithJob_oneObject() {
        List<StageDefinition> expectedStageDefinitions = new ArrayList<>();
        StageDefinition expectedStageDefinition = new StageDefinition();
        List<JobDefinition> expectedJobDefinitions = new ArrayList<>();
        expectedJobDefinitions.add(new JobDefinition());
        expectedStageDefinition.setJobDefinitions(expectedJobDefinitions);
        expectedStageDefinitions.add(expectedStageDefinition);
        this.expectedPipelineDefinition.setStageDefinitions(expectedStageDefinitions);
        this.mockedPipelineDefinitionRepository.update(this.expectedPipelineDefinition);
        Pipeline expectedPipeline = new Pipeline();
        expectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        ServiceResult actualResult = this.mockedPipelineService.add(expectedPipeline);
        Pipeline actualPipeline = (Pipeline) actualResult.getObject();
        int actualCollectionSize = actualPipeline.getStages().get(0).getJobs().size();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualCollectionSize);
        Assert.assertFalse(actualResult.hasError());
    }

    @Test
    public void add_validObjectWithTask_oneObject() {
        List<StageDefinition> expectedStageDefinitions = new ArrayList<>();
        StageDefinition expectedStageDefinition = new StageDefinition();
        List<JobDefinition> expectedJobDefinitions = new ArrayList<>();
        JobDefinition expectedJobDefinition = new JobDefinition();
        List<TaskDefinition> expectedTaskDefinitions = new ArrayList<>();

        ExecTask expectedTaskDefinition = new ExecTask();
        expectedTaskDefinition.setName("someName");
        expectedTaskDefinition.setRunIfCondition(RunIf.ANY);
        expectedTaskDefinition.setCommand("someCommand");
        expectedTaskDefinitions.add(expectedTaskDefinition);
        expectedJobDefinition.setTaskDefinitions(expectedTaskDefinitions);

        expectedJobDefinitions.add(expectedJobDefinition);
        expectedStageDefinition.setJobDefinitions(expectedJobDefinitions);
        expectedStageDefinitions.add(expectedStageDefinition);
        this.expectedPipelineDefinition.setStageDefinitions(expectedStageDefinitions);
        this.mockedPipelineDefinitionService.update(this.expectedPipelineDefinition);
        Pipeline expectedPipeline = new Pipeline();
        expectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        ServiceResult actualResult = this.mockedPipelineService.add(expectedPipeline);
        Pipeline actualPipeline = (Pipeline) actualResult.getObject();
        int actualCollectionSize = actualPipeline.getStages().get(0).getJobs().get(0).getTasks().size();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualCollectionSize);
        Assert.assertFalse(actualResult.hasError());
    }

    @Test
    public void add_existingObject_noObject() {
        Pipeline expectedPipeline = new Pipeline();
        expectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        this.mockedPipelineService.add(expectedPipeline);
        ServiceResult actualResult = this.mockedPipelineService.add(expectedPipeline);

        Assert.assertTrue(actualResult.hasError());
        Assert.assertNull(actualResult.getObject());
    }

    @Test
    public void update_existingObject_oneObject() {
        Pipeline pipelineToAdd = new Pipeline();
        pipelineToAdd.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        this.mockedPipelineService.add(pipelineToAdd);

        Pipeline expectedPipeline = (Pipeline) this.mockedPipelineService.getById(pipelineToAdd.getId()).getObject();

        expectedPipeline.setAreMaterialsUpdated(true);

        ServiceResult actualResult = this.mockedPipelineService.update(expectedPipeline);
        Pipeline actualPipeline = (Pipeline) actualResult.getObject();

        Assert.assertFalse(actualResult.hasError());
        Assert.assertTrue(actualPipeline.areMaterialsUpdated());

    }

    @Test
    public void update_nonexistentObject_noObject() {
        Pipeline expectedPipeline = new Pipeline();

        ServiceResult actualResult = this.mockedPipelineService.update(expectedPipeline);

        Assert.assertTrue(actualResult.hasError());
        Assert.assertNull(actualResult.getObject());
    }

    @Test
    public void delete_existingObject_true() {
        Pipeline pipelineToAdd = new Pipeline();
        pipelineToAdd.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        this.mockedPipelineService.add(pipelineToAdd);

        ServiceResult actualResult = this.mockedPipelineService.delete(pipelineToAdd.getId());

        Assert.assertFalse(actualResult.hasError());
    }

    @Test
    public void delete_nonexistentObject_false() {
        ServiceResult actualResult = this.mockedPipelineService.delete("someInvalidId");

        Assert.assertTrue(actualResult.hasError());
    }
}
