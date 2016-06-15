package net.hawkengine.core.components.pipelinescheduler;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.Status;
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
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "PackageVisibleField", "InstanceMethodNamingConvention"})
public class PipelinePreparerTest {
    private IPipelineService mockedPipelineService;
    private IPipelineDefinitionService mockedPipelineDefinitionService;
    @SuppressWarnings("InstanceVariableOfConcreteClass")
    private PipelinePreparer mockedPipelinePreparer;

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelineService");
        IDbRepository mockedPipelineRepo = new RedisRepository(Pipeline.class, mockedPool);
        IDbRepository mockedPipelineDefinitionRepo = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.mockedPipelineService = new PipelineService(mockedPipelineRepo);
        this.mockedPipelineDefinitionService = new PipelineDefinitionService(mockedPipelineDefinitionRepo);
        this.mockedPipelinePreparer = new PipelinePreparer(this.mockedPipelineService, this.mockedPipelineDefinitionService);
    }

    @Test
    public void getAllPreparedPipelines_withOneStatusFailed_threeObjects() {
        //Arrange
        Pipeline firstExpectedResult = new Pipeline();
        firstExpectedResult.setAreMaterialsUpdated(true);
        firstExpectedResult.setStatus(Status.IN_PROGRESS);

        Pipeline secondExpectedResult = new Pipeline();
        secondExpectedResult.setAreMaterialsUpdated(true);
        secondExpectedResult.setStatus(Status.IN_PROGRESS);

        Pipeline thirdExpectedResult = new Pipeline();
        thirdExpectedResult.setAreMaterialsUpdated(true);
        thirdExpectedResult.setStatus(Status.FAILED);

        Pipeline fourthExpectedResult = new Pipeline();
        fourthExpectedResult.setAreMaterialsUpdated(true);
        fourthExpectedResult.setStatus(Status.IN_PROGRESS);

        this.mockedPipelineService.add(firstExpectedResult);
        this.mockedPipelineService.add(secondExpectedResult);
        this.mockedPipelineService.add(thirdExpectedResult);
        this.mockedPipelineService.add(fourthExpectedResult);

        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_THREE_OBJECTS;

        //Act
        List<Pipeline> actualResult = this.mockedPipelinePreparer.getAllUpdatedPipelines().stream()
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        String firstActualResultObjectId = actualResult
                .stream()
                .filter(p -> p.getId().equals(firstExpectedResult.getId()))
                .collect(Collectors.toList())
                .get(0)
                .getId();

        String secondActualResultObjectId = actualResult
                .stream()
                .filter(p -> p.getId().equals(secondExpectedResult.getId()))
                .collect(Collectors.toList())
                .get(0)
                .getId();

        String thirdActualResultObjectId = actualResult
                .stream()
                .filter(p -> p.getId().equals(fourthExpectedResult.getId()))
                .collect(Collectors.toList())
                .get(0)
                .getId();

        //Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(firstExpectedResult.getId(), firstActualResultObjectId);
        Assert.assertEquals(secondExpectedResult.getId(), secondActualResultObjectId);
        Assert.assertEquals(fourthExpectedResult.getId(), thirdActualResultObjectId);
    }

    @Test
    public void getAllPreparedPipelines_withOneNotUpdatedMaterial_oneObject() {
        //Arrange
        Pipeline firstExpectedResult = new Pipeline();
        firstExpectedResult.setAreMaterialsUpdated(true);
        firstExpectedResult.setStatus(Status.IN_PROGRESS);

        Pipeline secondExpectedResult = new Pipeline();
        secondExpectedResult.setAreMaterialsUpdated(false);
        secondExpectedResult.setStatus(Status.IN_PROGRESS);

        this.mockedPipelineService.add(firstExpectedResult);
        this.mockedPipelineService.add(secondExpectedResult);

        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        //Act
        List<Pipeline> actualResult = this.mockedPipelinePreparer.getAllUpdatedPipelines();

        //Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(firstExpectedResult.getId(), actualResult.get(0).getId());
    }

    @Test
    public void getAllPreparedPipelines_withOneIsPrepared_oneObject() {
        //Arrange
        Pipeline firstExpectedResult = new Pipeline();
        firstExpectedResult.setAreMaterialsUpdated(true);
        firstExpectedResult.setStatus(Status.IN_PROGRESS);

        Pipeline secondExpectedResult = new Pipeline();
        secondExpectedResult.setAreMaterialsUpdated(true);
        secondExpectedResult.setPrepared(true);
        secondExpectedResult.setStatus(Status.IN_PROGRESS);

        this.mockedPipelineService.add(firstExpectedResult);
        this.mockedPipelineService.add(secondExpectedResult);

        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        //Act
        List<Pipeline> actualResult = this.mockedPipelinePreparer.getAllUpdatedPipelines();

        //Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(firstExpectedResult.getId(), actualResult.get(0).getId());
    }

    @Test
    public void getAllPreparedPipelines_withOneNotUpdatedMaterialAndOneStatusFailed_oneObject() {
        //Arrange
        Pipeline firstExpectedResult = new Pipeline();
        firstExpectedResult.setAreMaterialsUpdated(true);
        firstExpectedResult.setStatus(Status.IN_PROGRESS);

        Pipeline secondExpectedResult = new Pipeline();
        secondExpectedResult.setAreMaterialsUpdated(false);
        secondExpectedResult.setStatus(Status.IN_PROGRESS);

        Pipeline thirdExpectedResult = new Pipeline();
        thirdExpectedResult.setAreMaterialsUpdated(true);
        thirdExpectedResult.setStatus(Status.FAILED);

        this.mockedPipelineService.add(firstExpectedResult);
        this.mockedPipelineService.add(secondExpectedResult);
        this.mockedPipelineService.add(thirdExpectedResult);

        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        //Act
        List<Pipeline> actualResult = this.mockedPipelinePreparer.getAllUpdatedPipelines();

        //Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(firstExpectedResult.getId(), actualResult.get(0).getId());
    }

    @Test
    public void preparePipeline_withValidObject_object() {
        //Act
        List<Pipeline> actualResult = this.injectDataForTestingPreparePipeline();

        //Assert
        for (Pipeline actualResultObject : actualResult) {
            Pipeline preparedPipeline = this.mockedPipelinePreparer.preparePipeline(actualResultObject);
            Assert.assertEquals(preparedPipeline.isPrepared(), true);
            Assert.assertNotNull(preparedPipeline.getJobsForExecution());
            Assert.assertNotNull(preparedPipeline.getEnvironmentVariables());
        }
    }

    private List<Pipeline> injectDataForTestingPreparePipeline() {
        //Assert
        List<StageDefinition> stages = new ArrayList<>();
        List<JobDefinition> jobDefinitions = new ArrayList<>();

        PipelineDefinition pipelineDefinition = new PipelineDefinition();

        stages.add(new StageDefinition());
        jobDefinitions.add(new JobDefinition());
        stages.get(0).setJobDefinitions(jobDefinitions);
        pipelineDefinition.setStageDefinitions(stages);
        this.mockedPipelineDefinitionService.add(pipelineDefinition);

        Pipeline firstPipeline = new Pipeline();
        firstPipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        firstPipeline.setAreMaterialsUpdated(true);
        firstPipeline.setStatus(Status.IN_PROGRESS);

        Pipeline secondPipeline = new Pipeline();
        secondPipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        secondPipeline.setAreMaterialsUpdated(true);
        secondPipeline.setStatus(Status.IN_PROGRESS);

        this.mockedPipelineService.add(firstPipeline);
        this.mockedPipelineService.add(secondPipeline);

        ServiceResult serviceResult = this.mockedPipelineService.getAll();
        List<Pipeline> actualObjects = (List<Pipeline>) serviceResult.getObject();

        actualObjects
                .stream()
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        return actualObjects;
    }
}
