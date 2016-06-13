package net.hawkengine.core.components.pipelinescheduler;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.*;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelinePreparerTest {
    IDbRepository pipelineRepo;
    IDbRepository pipelineDefinitionRepo;
    PipelineService pipelineService;
    PipelineDefinitionService pipelineDefinitionService;
    PipelinePreparer pipelinePreparer;

    @Before
    public void setUp() throws Exception {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelineService");
        this.pipelineRepo = new RedisRepository(Pipeline.class, mockedPool);
        this.pipelineDefinitionRepo = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.pipelineService = new PipelineService(this.pipelineRepo);
        this.pipelineDefinitionService = new PipelineDefinitionService(this.pipelineDefinitionRepo);
        this.pipelinePreparer = new PipelinePreparer(pipelineService, pipelineDefinitionService);
    }

    @Test
    public void getAllPreparedPipelines_withOneInvalidStatus_threeObjects() {
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

        int expectedCollectionSize = 3;

        List<Pipeline> actualResult = pipelinePreparer.getAllUpdatedPipelines().stream()
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

        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(firstExpectedResult.getId(), firstActualResultObjectId);
        Assert.assertEquals(secondExpectedResult.getId(), secondActualResultObjectId);
        Assert.assertEquals(fourthExpectedResult.getId(), thirdActualResultObjectId);
    }

    @Test
    public void getAllPreparedPipelines_withOneNotUpdatedMaterial_oneObject() throws Exception {
        Pipeline firstExpectedResult = new Pipeline();
        firstExpectedResult.setAreMaterialsUpdated(true);
        firstExpectedResult.setStatus(Status.IN_PROGRESS);

        Pipeline secondExpectedResult = new Pipeline();
        secondExpectedResult.setAreMaterialsUpdated(false);
        secondExpectedResult.setStatus(Status.IN_PROGRESS);

        this.pipelineService.add(firstExpectedResult);
        this.pipelineService.add(secondExpectedResult);

        int expectedCollectionSize = 1;

        List<Pipeline> actualResult = pipelinePreparer.getAllUpdatedPipelines();

        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(firstExpectedResult.getId(), actualResult.get(0).getId());
    }

    @Test
    public void preparePipeline_withValidObject_validModelState() throws Exception {
        List<Pipeline> actualResult = this.injectDataForTestingPreparePipeline();

        for (Pipeline actualResultObject : actualResult) {
            Pipeline preparedPipeline = pipelinePreparer.preparePipeline(actualResultObject);
            Assert.assertEquals(preparedPipeline.isPrepared(), true);
        }
    }

    @Test
    public void preparePipeline_withValidInput_addedJobs() throws Exception {
        List<Pipeline> filteredPipelines = this.injectDataForTestingPreparePipeline();

        for (Pipeline filteredPipeline : filteredPipelines) {
            Pipeline preparedPipeline = pipelinePreparer.preparePipeline(filteredPipeline);
            Assert.assertNotNull(preparedPipeline.getJobsForExecution());
        }
    }

    @Test
    public void preparePipeline_withValidInput_addedEnvironmentVariables() throws Exception {
        List<Pipeline> filteredPipelines = this.injectDataForTestingPreparePipeline();

        for (Pipeline filteredPipeline : filteredPipelines) {
            Pipeline preparedPipeline = pipelinePreparer.preparePipeline(filteredPipeline);
            Assert.assertNotNull(preparedPipeline.getEnvironmentVariables());
        }
    }

    private List<Pipeline> injectDataForTestingPreparePipeline() throws Exception {
        ArrayList<Stage> stages = new ArrayList<>();
        ArrayList<JobDefinition> jobDefinitions = new ArrayList<>();

        PipelineDefinition pipelineDefinition = new PipelineDefinition();

        for (int i = 0; i < 5; i++) {
            Stage stage = new Stage();
            JobDefinition jobDefinition = new JobDefinition();

            stages.add(stage);
            jobDefinitions.add(jobDefinition);
        }

        for (Stage stage : stages) {
            stage.setJobs(jobDefinitions);
        }
        pipelineDefinition.setStages(stages);

        this.pipelineDefinitionService.add(pipelineDefinition);

        Pipeline firstPipeline = new Pipeline();
        firstPipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        firstPipeline.setAreMaterialsUpdated(true);
        firstPipeline.setStatus(Status.IN_PROGRESS);


        Pipeline secondPipeline = new Pipeline();
        secondPipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        secondPipeline.setAreMaterialsUpdated(true);
        secondPipeline.setStatus(Status.IN_PROGRESS);

        int expectedCollectionSize = 2;

        this.pipelineService.add(firstPipeline);
        this.pipelineService.add(secondPipeline);

        ServiceResult serviceResult = this.pipelineService.getAll();
        List<Pipeline> actualObjects = (List<Pipeline>) serviceResult.getObject();

        List<Pipeline> filteredPipelines = actualObjects.stream()
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        return filteredPipelines;
    }
}
