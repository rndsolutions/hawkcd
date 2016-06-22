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
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "PackageVisibleField", "InstanceMethodNamingConvention"})
public class PipelinePreparerTests {
    private IPipelineService mockedPipelineService;
    private IPipelineDefinitionService mockedPipelineDefinitionService;
    @SuppressWarnings("InstanceVariableOfConcreteClass")
    private PipelinePreparer mockedPipelinePreparer;
    private PipelineDefinition expectedPipelineDefinition;

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelinePreparer");
        IDbRepository mockedPipelineRepo = new RedisRepository(Pipeline.class, mockedPool);
        IDbRepository mockedPipelineDefinitionRepo = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.mockedPipelineDefinitionService = new PipelineDefinitionService(mockedPipelineDefinitionRepo);
        this.mockedPipelineService = new PipelineService(mockedPipelineRepo, this.mockedPipelineDefinitionService);
        this.mockedPipelinePreparer = new PipelinePreparer(this.mockedPipelineService, this.mockedPipelineDefinitionService);
        this.expectedPipelineDefinition = new PipelineDefinition();
        this.mockedPipelineDefinitionService.add(this.expectedPipelineDefinition);
    }

    @Test
    public void getAllPreparedPipelines_withOneStatusFailed_threeObjects() {
        //Arrange
        Pipeline firstExpectedResult = new Pipeline();
        firstExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        firstExpectedResult.setAreMaterialsUpdated(true);
        firstExpectedResult.setStatus(Status.IN_PROGRESS);

        Pipeline secondExpectedResult = new Pipeline();
        secondExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        secondExpectedResult.setAreMaterialsUpdated(true);
        secondExpectedResult.setStatus(Status.IN_PROGRESS);

        Pipeline thirdExpectedResult = new Pipeline();
        thirdExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        thirdExpectedResult.setAreMaterialsUpdated(true);
        thirdExpectedResult.setStatus(Status.FAILED);

        Pipeline fourthExpectedResult = new Pipeline();
        fourthExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
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
        firstExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        firstExpectedResult.setAreMaterialsUpdated(true);
        firstExpectedResult.setStatus(Status.IN_PROGRESS);

        Pipeline secondExpectedResult = new Pipeline();
        secondExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
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
        firstExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        firstExpectedResult.setAreMaterialsUpdated(true);
        firstExpectedResult.setStatus(Status.IN_PROGRESS);

        Pipeline secondExpectedResult = new Pipeline();
        secondExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
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
        firstExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        firstExpectedResult.setAreMaterialsUpdated(true);
        firstExpectedResult.setStatus(Status.IN_PROGRESS);

        Pipeline secondExpectedResult = new Pipeline();
        secondExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        secondExpectedResult.setAreMaterialsUpdated(false);
        secondExpectedResult.setStatus(Status.IN_PROGRESS);

        Pipeline thirdExpectedResult = new Pipeline();
        thirdExpectedResult.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
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
        PipelineDefinition actualPipelineDefinition = this.preparePipelineDefinition();
        List<Pipeline> actualResult = this.injectDataForTestingPreparePipeline(actualPipelineDefinition);

        //Assert
        for (Pipeline actualResultObject : actualResult) {
            Pipeline preparedPipeline = this.mockedPipelinePreparer.preparePipeline(actualResultObject);
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
        this.mockedPipelineService.add(pipeline);

        Pipeline actualPipeline = (Pipeline) this.mockedPipelineService.getById(pipeline.getId()).getObject();
        List<StageDefinition> actualStageDefinitions = actualPipelineDefinition.getStageDefinitions();
        List<Stage> preparedStages = this.mockedPipelinePreparer.preparePipelineStages(actualStageDefinitions, actualPipeline);

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
        this.mockedPipelineService.add(pipeline);

        Pipeline actualPipeline = (Pipeline) this.mockedPipelineService.getById(pipeline.getId()).getObject();

        List<StageDefinition> actualStageDefinitions = actualPipelineDefinition.getStageDefinitions();
        List<Stage> preparedStages = this.mockedPipelinePreparer.preparePipelineStages(actualStageDefinitions, actualPipeline);

        for (StageDefinition actualStageDefinitionObject: actualStageDefinitions) {
            List<JobDefinition> actualJobDefintions = actualStageDefinitionObject.getJobDefinitions();

            List<Job> preparedJobs = this.mockedPipelinePreparer.preparePipelineJobs(actualJobDefintions, preparedStages.get(0));

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
//    @Test
//    public void preparePipelineJobsTasks_withValidObjects_listWithOneObject(){
//        //Act
//        PipelineDefinition actualPipelineDefinition = this.preparePipelineDefinition();
//        Pipeline pipeline = new Pipeline();
//        pipeline.setPipelineDefinitionId(actualPipelineDefinition.getId());
//
//        List<Stage> pipelineStages = new ArrayList<>();
//        Stage pipelineStage = new Stage();
//        pipelineStages.add(pipelineStage);
//
//        List<Job> pipelineJobs = new ArrayList<>();
//        Job pipelineJob = new Job();
//        pipelineJobs.add(pipelineJob);
//
//        pipeline.setStages(pipelineStages);
//        pipeline.getStages().get(0).setJobs(pipelineJobs);
//
//        this.mockedPipelineService.add(pipeline);
//
//        Pipeline actualPipeline = (Pipeline) this.mockedPipelineService.getById(pipeline.getId()).getObject();
//
//        for (StageDefinition stageDefinition: actualPipelineDefinition.getStageDefinitions()) {
//            for (JobDefinition jobDefinition: stageDefinition.getJobDefinitions()) {
//                List<TaskDefinition> taskDefinitions = jobDefinition.getTaskDefinitions();
//
//                List<Task> preparedTasks = this.mockedPipelinePreparer.prepareTasks(taskDefinitions, pipelineJob);
//
//                //Assert
//                Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, preparedTasks.size());
//                Assert.assertNotNull(preparedTasks.get(0).getPipelineId());
//                Assert.assertEquals(actualPipeline.getId(),preparedTasks.get(0).getPipelineId());
//                Assert.assertNotNull(preparedTasks.get(0).getJobId());
//                Assert.assertEquals(pipelineJob.getId(), preparedTasks.get(0).getJobId());
//                Assert.assertNotNull(preparedTasks.get(0).getStageId());
//                Assert.assertEquals(pipelineStage.getId(), preparedTasks.get(0).getStageId());
//
//            }
//        }
//    }

    @Test
    public void runPipelinePreparer_interruptedThread_throwInterruptedException() {
        InterruptedException interrupt = new InterruptedException();
        try {
            Thread.currentThread().interrupt();
            this.mockedPipelinePreparer.start();
        } catch (IllegalStateException e) {
            Assert.assertEquals(interrupt, e.getCause());
        }
    }

    private List<Pipeline> injectDataForTestingPreparePipeline(PipelineDefinition pipelineDefinition) {
        //Assert
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
//        ExecTask taskDefinition = new ExecTask();
//        taskDefinition.setCommand("echo");
//        taskDefinition.setArguments(new String[]{"fdfdfd"});
//        taskDefinition.setJobDefinitionId(jobDefinitionToAdd.getId());
//        taskDefinition.setPipelineDefinitionId(pipelineDefinition.getId());
//        taskDefinition.setName("taskDefinitionToAdd");
//        taskDefinition.setStageDefinitionId(stageDefinitionToAdd.getId());

        stages.add(stageDefinitionToAdd);
        jobDefinitions.add(jobDefinitionToAdd);
        //TODO: Add when json deserializer is ready
        //taskDefinitions.add(taskDefinition);

        jobDefinitions.get(0).setTaskDefinitions(taskDefinitions);
        stages.get(0).setJobDefinitions(jobDefinitions);
        pipelineDefinition.setStageDefinitions(stages);

        this.mockedPipelineDefinitionService.add(pipelineDefinition);

        return pipelineDefinition;
    }

    private List<EnvironmentVariable> prepareEnvironmentVariables(){
        List<EnvironmentVariable> environmentVariables = new ArrayList<>();

        EnvironmentVariable environmentVariable = new EnvironmentVariable();
        environmentVariable.setName(UUID.randomUUID().toString().replaceAll("-", ""));
        environmentVariable.setValue(UUID.randomUUID().toString().replaceAll("-", ""));

        environmentVariables.add(environmentVariable);

        return environmentVariables;
    }
}
