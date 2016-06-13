package net.hawkengine.core.components.pipelinescheduler;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.*;
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
    private IPipelineService pipelineService;
    private IPipelineDefinitionService pipelineDefinitionService;
    @SuppressWarnings("InstanceVariableOfConcreteClass")
    private
    PipelinePreparer pipelinePreparer;

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelineService");
        IDbRepository pipelineRepo = new RedisRepository(Pipeline.class, mockedPool);
        IDbRepository pipelineDefinitionRepo = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.pipelineService = new PipelineService(pipelineRepo);
        this.pipelineDefinitionService = new PipelineDefinitionService(pipelineDefinitionRepo);
        this.pipelinePreparer = new PipelinePreparer(this.pipelineService, this.pipelineDefinitionService);
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

        this.pipelineService.add(firstExpectedResult);
        this.pipelineService.add(secondExpectedResult);
        this.pipelineService.add(thirdExpectedResult);
        this.pipelineService.add(fourthExpectedResult);

        int expectedCollectionSize = TestsConstants.PIPELINE_PREPARER_TESTS_THREE_OBJECTS;

        //Act
        List<Pipeline> actualResult = this.pipelinePreparer.getAllUpdatedPipelines().stream()
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
    public void getAllPreparedPipelines_withOneNotUpdatedMaterial_oneObject() throws Exception {
        //Arrange
        Pipeline firstExpectedResult = new Pipeline();
        firstExpectedResult.setAreMaterialsUpdated(true);
        firstExpectedResult.setStatus(Status.IN_PROGRESS);

        Pipeline secondExpectedResult = new Pipeline();
        secondExpectedResult.setAreMaterialsUpdated(false);
        secondExpectedResult.setStatus(Status.IN_PROGRESS);

        this.pipelineService.add(firstExpectedResult);
        this.pipelineService.add(secondExpectedResult);

        int expectedCollectionSize = TestsConstants.PIPELINE_PREPARER_TESTS_ONE_OBJECT;

        //Act
        List<Pipeline> actualResult = this.pipelinePreparer.getAllUpdatedPipelines();

        //Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(firstExpectedResult.getId(), actualResult.get(0).getId());
    }

    @Test
    public void getAllPreparedPipelines_withOneNotUpdatedMaterialAndOneStatusFailed_oneObject() throws Exception {
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

        this.pipelineService.add(firstExpectedResult);
        this.pipelineService.add(secondExpectedResult);
        this.pipelineService.add(thirdExpectedResult);

        int expectedCollectionSize = TestsConstants.PIPELINE_PREPARER_TESTS_ONE_OBJECT;

        //Act
        List<Pipeline> actualResult = this.pipelinePreparer.getAllUpdatedPipelines();

        //Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(firstExpectedResult.getId(), actualResult.get(0).getId());
    }

    @Test
    public void preparePipeline_withValidObject_object() throws Exception {
        //Act
        List<Pipeline> actualResult = this.injectDataForTestingPreparePipeline();

        //Assert
        for (Pipeline actualResultObject : actualResult) {
            Pipeline preparedPipeline = this.pipelinePreparer.preparePipeline(actualResultObject);
            Assert.assertEquals(preparedPipeline.isPrepared(), true);
            Assert.assertNotNull(preparedPipeline.getJobsForExecution());
            Assert.assertNotNull(preparedPipeline.getEnvironmentVariables());
        }
    }

    private List<Pipeline> injectDataForTestingPreparePipeline() throws Exception {
        //Assert
        ArrayList<StageDefinition> stages = new ArrayList<>();
        ArrayList<JobDefinition> jobDefinitions = new ArrayList<>();

        PipelineDefinition pipelineDefinition = new PipelineDefinition();

        stages.add(new StageDefinition());
        jobDefinitions.add(new JobDefinition());
        stages.get(0).setJobDefinitions(jobDefinitions);
        pipelineDefinition.setStageDefinitions(stages);
        this.pipelineDefinitionService.add(pipelineDefinition);

        Pipeline firstPipeline = new Pipeline();
        firstPipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        firstPipeline.setAreMaterialsUpdated(true);
        firstPipeline.setStatus(Status.IN_PROGRESS);

        Pipeline secondPipeline = new Pipeline();
        secondPipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        secondPipeline.setAreMaterialsUpdated(true);
        secondPipeline.setStatus(Status.IN_PROGRESS);

        this.pipelineService.add(firstPipeline);
        this.pipelineService.add(secondPipeline);

        ServiceResult serviceResult = this.pipelineService.getAll();
        List<Pipeline> actualObjects = (List<Pipeline>) serviceResult.getObject();

        return actualObjects.stream()
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());
    }
}
