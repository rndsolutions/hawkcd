package net.hawkengine.core.pipelinescheduler;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.PipelineStatus;
import net.hawkengine.services.MaterialDefinitionService;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IMaterialDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PipelinePreparerTests {
    private IPipelineService pipelineService;
    private IPipelineDefinitionService pipelineDefinitionService;
    private IMaterialDefinitionService materialDefinitionService;
    private PipelinePreparer pipelinePreparer;
    private PipelineDefinition expectedPipelineDefinition;

    @BeforeClass
    public static void setUpClass() {
        ServerConfiguration.configure();
    }

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelinePreparer");
        IDbRepository pipelineRepo = new RedisRepository(Pipeline.class, mockedPool);
        IDbRepository pipelineDefinitionRepo = new RedisRepository(PipelineDefinition.class, mockedPool);
        IDbRepository materialDefinitionRepo = new RedisRepository(MaterialDefinition.class, mockedPool);
        this.pipelineDefinitionService = new PipelineDefinitionService(pipelineDefinitionRepo);
        this.materialDefinitionService = new MaterialDefinitionService(materialDefinitionRepo, this.pipelineDefinitionService);
        this.pipelineService = new PipelineService(pipelineRepo, this.pipelineDefinitionService, this.materialDefinitionService);
        this.pipelinePreparer = new PipelinePreparer(this.pipelineService, this.pipelineDefinitionService);
        this.expectedPipelineDefinition = new PipelineDefinition();
        this.pipelineDefinitionService.add(this.expectedPipelineDefinition);
    }

    @Test
    public void getAllPreparedPipelines_withOneStatusFailed_threeObjects() {
        //Arrange
        Pipeline firstExpectedResult = new Pipeline();
        firstExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        firstExpectedResult.setMaterialsUpdated(true);
        firstExpectedResult.setStatus(PipelineStatus.IN_PROGRESS);

        Pipeline secondExpectedResult = new Pipeline();
        secondExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        secondExpectedResult.setMaterialsUpdated(true);
        secondExpectedResult.setStatus(PipelineStatus.IN_PROGRESS);

        Pipeline thirdExpectedResult = new Pipeline();
        thirdExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        thirdExpectedResult.setMaterialsUpdated(true);
        thirdExpectedResult.setStatus(PipelineStatus.FAILED);

        Pipeline fourthExpectedResult = new Pipeline();
        fourthExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        fourthExpectedResult.setMaterialsUpdated(true);
        fourthExpectedResult.setStatus(PipelineStatus.IN_PROGRESS);

        this.pipelineService.add(firstExpectedResult);
        this.pipelineService.add(secondExpectedResult);
        this.pipelineService.add(thirdExpectedResult);
        this.pipelineService.add(fourthExpectedResult);

        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_THREE_OBJECTS;

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
    public void getAllPreparedPipelines_withOneNotUpdatedMaterial_oneObject() {
        //Arrange
        Pipeline firstExpectedResult = new Pipeline();
        firstExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        firstExpectedResult.setMaterialsUpdated(true);
        firstExpectedResult.setStatus(PipelineStatus.IN_PROGRESS);

        Pipeline secondExpectedResult = new Pipeline();
        secondExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        secondExpectedResult.setMaterialsUpdated(false);
        secondExpectedResult.setStatus(PipelineStatus.IN_PROGRESS);

        this.pipelineService.add(firstExpectedResult);
        this.pipelineService.add(secondExpectedResult);

        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        //Act
        List<Pipeline> actualResult = this.pipelinePreparer.getAllUpdatedPipelines();

        //Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(firstExpectedResult.getId(), actualResult.get(0).getId());
    }

    @Test
    public void getAllPreparedPipelines_withOneIsPrepared_oneObject() {
        //Arrange
        Pipeline firstExpectedResult = new Pipeline();
        firstExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        firstExpectedResult.setMaterialsUpdated(true);
        firstExpectedResult.setStatus(PipelineStatus.IN_PROGRESS);

        Pipeline secondExpectedResult = new Pipeline();
        secondExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        secondExpectedResult.setMaterialsUpdated(true);
        secondExpectedResult.setPrepared(true);
        secondExpectedResult.setStatus(PipelineStatus.IN_PROGRESS);

        this.pipelineService.add(firstExpectedResult);
        this.pipelineService.add(secondExpectedResult);

        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        //Act
        List<Pipeline> actualResult = this.pipelinePreparer.getAllUpdatedPipelines();

        //Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(firstExpectedResult.getId(), actualResult.get(0).getId());
    }

    @Test
    public void getAllPreparedPipelines_withOneNotUpdatedMaterialAndOneStatusFailed_oneObject() {
        //Arrange
        Pipeline firstExpectedResult = new Pipeline();
        firstExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        firstExpectedResult.setMaterialsUpdated(true);
        firstExpectedResult.setStatus(PipelineStatus.IN_PROGRESS);

        Pipeline secondExpectedResult = new Pipeline();
        secondExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        secondExpectedResult.setMaterialsUpdated(false);
        secondExpectedResult.setStatus(PipelineStatus.IN_PROGRESS);

        Pipeline thirdExpectedResult = new Pipeline();
        thirdExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        thirdExpectedResult.setMaterialsUpdated(true);
        thirdExpectedResult.setStatus(PipelineStatus.FAILED);

        this.pipelineService.add(firstExpectedResult);
        this.pipelineService.add(secondExpectedResult);
        this.pipelineService.add(thirdExpectedResult);

        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        //Act
        List<Pipeline> actualResult = this.pipelinePreparer.getAllUpdatedPipelines();

        //Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(firstExpectedResult.getId(), actualResult.get(0).getId());
    }

    @Test
    public void preparePipeline_withValidObject_object() {
        //Act
        PipelineDefinition actualPipelineDefinition = this.preparePipelineDefinition();
        List<Pipeline> actualResult = this.injectDataForTestingPreparePipeline(actualPipelineDefinition);

        //Assert
        for (Pipeline actualResultObject : actualResult) {
            Pipeline preparedPipeline = this.pipelinePreparer.preparePipeline(actualResultObject);
            Assert.assertEquals(preparedPipeline.isPrepared(), true);
            Assert.assertNotNull(preparedPipeline.getEnvironmentVariables());
        }
    }

    @Test
    public void preparePipelineStages_withValidObjects_listWithOneObject(){
        //Act
        PipelineDefinition actualPipelineDefinition = this.preparePipelineDefinition();
        Pipeline pipeline = new Pipeline();
        pipeline.setPipelineDefinitionId(actualPipelineDefinition.getId());
        this.pipelineService.add(pipeline);

        Pipeline actualPipeline = (Pipeline) this.pipelineService.getById(pipeline.getId()).getObject();
        List<StageDefinition> actualStageDefinitions = actualPipelineDefinition.getStageDefinitions();
        List<Stage> preparedStages = this.pipelinePreparer.preparePipelineStages(actualStageDefinitions, actualPipeline);

        //Assert
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, preparedStages.size());
        Assert.assertNotNull(preparedStages.get(0).getEnvironmentVariables());
        Assert.assertNotNull(preparedStages.get(0).getPipelineId());
        Assert.assertNotNull(preparedStages.get(0).getStageDefinitionId());
    }

    @Test
    public void preparePipelineJobs_withValidObjects_listWithOneObject(){
        //Act
        PipelineDefinition actualPipelineDefinition = this.preparePipelineDefinition();
        Pipeline pipeline = new Pipeline();
        pipeline.setPipelineDefinitionId(actualPipelineDefinition.getId());
        this.pipelineService.add(pipeline);

        Pipeline actualPipeline = (Pipeline) this.pipelineService.getById(pipeline.getId()).getObject();

        List<StageDefinition> actualStageDefinitions = actualPipelineDefinition.getStageDefinitions();
        List<Stage> preparedStages = this.pipelinePreparer.preparePipelineStages(actualStageDefinitions, actualPipeline);

        for (StageDefinition actualStageDefinitionObject: actualStageDefinitions) {
            List<JobDefinition> actualJobDefintions = actualStageDefinitionObject.getJobDefinitions();

            List<Job> preparedJobs = this.pipelinePreparer.preparePipelineJobs(actualJobDefintions, preparedStages.get(0));

            //Assert
            Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, preparedJobs.size());
            Assert.assertNotNull(preparedJobs.get(0).getEnvironmentVariables());
            Assert.assertNotNull(preparedJobs.get(0).getPipelineId());
            Assert.assertEquals(pipeline.getId(),preparedJobs.get(0).getPipelineId());
            Assert.assertNotNull(preparedJobs.get(0).getStageId());
            Assert.assertEquals(preparedStages.get(0).getId(), preparedJobs.get(0).getStageId());
        }
    }

    //TODO: Add when json deserializer for Task is ready
    @Test
    public void preparePipelineJobsTasks_withValidObjects_listWithOneObject(){
        //Act
        PipelineDefinition actualPipelineDefinition = this.preparePipelineDefinition();
        Pipeline pipeline = new Pipeline();
        pipeline.setPipelineDefinitionId(actualPipelineDefinition.getId());

        List<Stage> pipelineStages = new ArrayList<>();
        Stage pipelineStage = new Stage();
        pipelineStages.add(pipelineStage);

        List<Job> pipelineJobs = new ArrayList<>();
        Job pipelineJob = new Job();
        pipelineJobs.add(pipelineJob);

        pipeline.setStages(pipelineStages);
        pipeline.getStages().get(0).setJobs(pipelineJobs);

        this.pipelineService.add(pipeline);

        Pipeline actualPipeline = (Pipeline) this.pipelineService.getById(pipeline.getId()).getObject();

        for (StageDefinition stageDefinition: actualPipelineDefinition.getStageDefinitions()) {
            for (JobDefinition jobDefinition: stageDefinition.getJobDefinitions()) {
                List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();

                List<Task> preparedTasks = this.pipelinePreparer.prepareTasks(taskDefinitions, pipelineJob);

                //Assert
                Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, preparedTasks.size());
            }
        }
    }

    @Test
    public void runPipelinePreparer_interruptedThread_throwInterruptedException() {
        InterruptedException interrupt = new InterruptedException();
        try {
            Thread.currentThread().interrupt();
            this.pipelinePreparer.run();
        } catch (IllegalStateException e) {
            Assert.assertEquals(interrupt, e.getCause());
        }
    }

    private List<Pipeline> injectDataForTestingPreparePipeline(PipelineDefinition pipelineDefinition) {
        //Assert
        Pipeline firstPipeline = new Pipeline();
        firstPipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        firstPipeline.setMaterialsUpdated(true);
        firstPipeline.setStatus(PipelineStatus.IN_PROGRESS);

        Pipeline secondPipeline = new Pipeline();
        secondPipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        secondPipeline.setMaterialsUpdated(true);
        secondPipeline.setStatus(PipelineStatus.IN_PROGRESS);

        this.pipelineService.add(firstPipeline);
        this.pipelineService.add(secondPipeline);

        ServiceResult serviceResult = this.pipelineService.getAll();
        List<Pipeline> actualObjects = (List<Pipeline>) serviceResult.getObject();

        actualObjects
                .stream()
                .sorted((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime()))
                .collect(Collectors.toList());

        return actualObjects;
    }

    private PipelineDefinition preparePipelineDefinition(){
        //Arrange
        List<StageDefinition> stages = new ArrayList<>();
        List<JobDefinition> jobDefinitions = new ArrayList<>();
        List<TaskDefinition> taskDefinitions = new ArrayList<>();
        List<EnvironmentVariable> stageDefinitionEvironmentVariables = this.prepareEnvironmentVariables();
        List<EnvironmentVariable> jobDefinitionEnvironmentVariables = this.prepareEnvironmentVariables();

        PipelineDefinition pipelineDefinition = new PipelineDefinition();

        StageDefinition stageDefinitionToAdd = new StageDefinition();
        stageDefinitionToAdd.setName("stageDefinitionToAddName");
        stageDefinitionToAdd.setPipelineDefinitionId(pipelineDefinition.getId());
        stageDefinitionToAdd.setEnvironmentVariables(stageDefinitionEvironmentVariables);

        JobDefinition jobDefinitionToAdd = new JobDefinition();
        jobDefinitionToAdd.setName("jobDefinitionToAdd");
        jobDefinitionToAdd.setStageDefinitionId(stageDefinitionToAdd.getId());
        jobDefinitionToAdd.setPipelineDefinitionId(pipelineDefinition.getId());
        jobDefinitionToAdd.setEnvironmentVariables(jobDefinitionEnvironmentVariables);

        //TODO: Add when json deserializer is ready
        ExecTask taskDefinition = new ExecTask();
        taskDefinition.setCommand("echo");
        taskDefinition.setJobDefinitionId(jobDefinitionToAdd.getId());
        taskDefinition.setPipelineDefinitionId(pipelineDefinition.getId());
        taskDefinition.setName("taskDefinitionToAdd");
        taskDefinition.setStageDefinitionId(stageDefinitionToAdd.getId());

        stages.add(stageDefinitionToAdd);
        jobDefinitions.add(jobDefinitionToAdd);
        //TODO: Add when json deserializer is ready
        taskDefinitions.add(taskDefinition);

        jobDefinitions.get(0).setTaskDefinitions(taskDefinitions);
        stages.get(0).setJobDefinitions(jobDefinitions);
        pipelineDefinition.setStageDefinitions(stages);

        this.pipelineDefinitionService.add(pipelineDefinition);

        return pipelineDefinition;
    }

    private List<EnvironmentVariable> prepareEnvironmentVariables(){
        List<EnvironmentVariable> environmentVariables = new ArrayList<>();

        EnvironmentVariable environmentVariable = new EnvironmentVariable();
        environmentVariable.setKey(UUID.randomUUID().toString().replaceAll("-", ""));
        environmentVariable.setValue(UUID.randomUUID().toString().replaceAll("-", ""));

        environmentVariables.add(environmentVariable);

        return environmentVariables;
    }
}
