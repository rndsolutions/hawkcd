package io.hawkcd.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;

import io.hawkcd.core.config.Config;
import io.hawkcd.utilities.constants.TestsConstants;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.db.redis.RedisRepository;
import io.hawkcd.model.dto.PipelineDto;
import io.hawkcd.model.enums.JobStatus;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.RunIf;
import io.hawkcd.model.enums.PipelineStatus;
import io.hawkcd.services.MaterialDefinitionService;
import io.hawkcd.services.PipelineDefinitionService;
import io.hawkcd.services.PipelineService;
import io.hawkcd.services.interfaces.IMaterialDefinitionService;
import io.hawkcd.model.ExecTask;
import io.hawkcd.model.Stage;
import io.hawkcd.services.interfaces.IPipelineDefinitionService;
import io.hawkcd.services.interfaces.IPipelineService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.hawkcd.model.Job;
import io.hawkcd.model.JobDefinition;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.Pipeline;
import io.hawkcd.model.PipelineDefinition;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.StageDefinition;
import io.hawkcd.model.TaskDefinition;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

public class PipelineServiceTests {
    private IDbRepository<Pipeline> pipelineRepo;
    private IDbRepository<PipelineDefinition> pipelineDefinitionRepository;
    private IDbRepository<MaterialDefinition> materialDefinitionIDbRepository;
    private IPipelineService pipelineService;
    private IPipelineDefinitionService pipelineDefinitionService;
    private IMaterialDefinitionService materialDefinitionService;
    private PipelineDefinition expectedPipelineDefinition;

    @BeforeClass
    public static void setUpClass() {
        Config.configure();
    }

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelineService");
        this.pipelineRepo = new RedisRepository(Pipeline.class, mockedPool);
        this.pipelineDefinitionRepository = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.materialDefinitionIDbRepository = new RedisRepository(MaterialDefinition.class, mockedPool);
        this.pipelineDefinitionService = new PipelineDefinitionService(this.pipelineDefinitionRepository);
        this.materialDefinitionService = new MaterialDefinitionService(this.materialDefinitionIDbRepository, this.pipelineDefinitionService);
        this.pipelineService = new PipelineService(this.pipelineRepo, this.pipelineDefinitionService, this.materialDefinitionService);
        this.expectedPipelineDefinition = new PipelineDefinition();
        this.expectedPipelineDefinition.setName("TestPipeline");
        this.pipelineDefinitionService.add(this.expectedPipelineDefinition);
    }

    @Test
    public void getById_withValidId_oneObject() {
        Pipeline expectedPipeline = new Pipeline();
        expectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        this.pipelineService.add(expectedPipeline);

        ServiceResult actualResult = this.pipelineService.getById(expectedPipeline.getId());
        Pipeline actualPipeline = (Pipeline) actualResult.getEntity();

        Assert.assertEquals(expectedPipeline.getId(), actualPipeline.getId());
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
    }

    @Test
    public void getById_withInvalidId_noObject() {
        Pipeline expectedPipeline = new Pipeline();
        expectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        this.pipelineService.add(expectedPipeline);

        ServiceResult actualResult = this.pipelineService.getById("someInvalidId");

        Assert.assertEquals(actualResult.getEntity(), null);
        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
    }

    @Test
    public void getAll_withExistingObjects_twoObjects() {
        Pipeline firstExpectedPipeline = new Pipeline();
        firstExpectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        Pipeline secondExpectedPipeline = new Pipeline();
        secondExpectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        this.pipelineService.add(firstExpectedPipeline);
        this.pipelineService.add(secondExpectedPipeline);

        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_TWO_OBJECTS;

        ServiceResult actualResult = this.pipelineService.getAll();
        List<Pipeline> actualPipelines = (List<Pipeline>) actualResult.getEntity();

        Assert.assertEquals(expectedCollectionSize, actualPipelines.size());
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
    }

    @Test
    public void getAll_withNonexistentObjects_noObjects() {
        ServiceResult actualResult = this.pipelineService.getAll();
        List<Pipeline> actualPipelines = (List<Pipeline>) actualResult.getEntity();

        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS, actualPipelines.size());
    }

    @Test
    public void add_validObject_oneObject() {
        Pipeline expectedPipeline = new Pipeline();
        expectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        ServiceResult actualResult = this.pipelineService.add(expectedPipeline);
        Pipeline actualPipeline = (Pipeline) actualResult.getEntity();
        int actualCollectionSize = ((List<Pipeline>) this.pipelineService.getAll().getEntity()).size();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualCollectionSize);
        Assert.assertNotNull(actualPipeline);
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertEquals(expectedPipeline.getId(), actualPipeline.getId());
    }

    @Test
    public void add_validObjectWithStage_oneObject() {
        List<StageDefinition> expectedStageDefinitions = new ArrayList<>();
        expectedStageDefinitions.add(new StageDefinition());
        this.expectedPipelineDefinition.setStageDefinitions(expectedStageDefinitions);
        this.pipelineDefinitionRepository.update(this.expectedPipelineDefinition);
        Pipeline expectedPipeline = new Pipeline();
        expectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        ServiceResult actualResult = this.pipelineService.add(expectedPipeline);
        Pipeline actualPipeline = (Pipeline) actualResult.getEntity();
        int actualCollectionSize = actualPipeline.getStages().size();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualCollectionSize);
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
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
        this.pipelineDefinitionRepository.update(this.expectedPipelineDefinition);
        Pipeline expectedPipeline = new Pipeline();
        expectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        ServiceResult actualResult = this.pipelineService.add(expectedPipeline);
        Pipeline actualPipeline = (Pipeline) actualResult.getEntity();
        int actualCollectionSize = actualPipeline.getStages().get(0).getJobs().size();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualCollectionSize);
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
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
        this.pipelineDefinitionService.update(this.expectedPipelineDefinition);
        Pipeline expectedPipeline = new Pipeline();
        expectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        ServiceResult actualResult = this.pipelineService.add(expectedPipeline);
        Pipeline actualPipeline = (Pipeline) actualResult.getEntity();
        int actualCollectionSize = actualPipeline.getStages().get(0).getJobs().get(0).getTasks().size();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualCollectionSize);
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
    }

    @Test
    public void add_existingObject_noObject() {
        Pipeline expectedPipeline = new Pipeline();
        expectedPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        this.pipelineService.add(expectedPipeline);
        ServiceResult actualResult = this.pipelineService.add(expectedPipeline);

        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
        Assert.assertNull(actualResult.getEntity());
    }

    @Test
    public void update_existingObject_oneObject() {
        Pipeline pipelineToAdd = new Pipeline();
        pipelineToAdd.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        this.pipelineService.add(pipelineToAdd);

        Pipeline expectedPipeline = (Pipeline) this.pipelineService.getById(pipelineToAdd.getId()).getEntity();

        expectedPipeline.setMaterialsUpdated(true);

        ServiceResult actualResult = this.pipelineService.update(expectedPipeline);
        Pipeline actualPipeline = (Pipeline) actualResult.getEntity();

        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertTrue(actualPipeline.areMaterialsUpdated());

    }

//    @Test
//    public void delete_existingObject_true() {
//        Pipeline pipelineToAdd = new Pipeline();
//        pipelineToAdd.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
//
//        this.pipelineService.add(pipelineToAdd);
//
//        ServiceResult actualResult = this.pipelineService.delete(pipelineToAdd.getId());
//
//        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
//    }

//    @Test
//    public void delete_nonexistentObject_false() {
//        ServiceResult actualResult = this.pipelineService.delete("someInvalidId");
//
//        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
//    }

    @Test
    public void getAllPipelinesInProgress_onePipelinePassed_twoObjects() {
        List<Pipeline> expectedPipelines = this.injectDataForTestingStatusUpdater();
        Pipeline firstExpectedPipeline = expectedPipelines.get(1);
        Pipeline secondExpectedPipeline = expectedPipelines.get(2);
        firstExpectedPipeline.setStatus(PipelineStatus.IN_PROGRESS);
        firstExpectedPipeline.setMaterialsUpdated(true);
        secondExpectedPipeline.setMaterialsUpdated(true);
        this.pipelineService.update(firstExpectedPipeline);
        this.pipelineService.update(secondExpectedPipeline);

        List<Pipeline> actualPipelines = (List<Pipeline>) this.pipelineService.getAllUpdatedUnpreparedPipelinesInProgress().getEntity();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_TWO_OBJECTS, actualPipelines.size());
    }

    @Test
    public void getAllUpdatedPipelines_onePipelineUpdated_oneObject() {
        List<Pipeline> expectedPipelines = this.injectDataForTestingStatusUpdater();
        Pipeline pipelineToChange = expectedPipelines.get(1);
        pipelineToChange.setMaterialsUpdated(true);
        this.pipelineService.update(pipelineToChange);

        List<Pipeline> actualPipelines = (List<Pipeline>) this.pipelineService.getAllUpdatedUnpreparedPipelinesInProgress().getEntity();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualPipelines.size());
    }

    @Test
    public void getAllPreparedPipelines() {
        List<Pipeline> expectedPipelines = this.injectDataForTestingStatusUpdater();

        List<Pipeline> actualPipelines = (List<Pipeline>) this.pipelineService.getAllPreparedPipelinesInProgress().getEntity();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualPipelines.size());
    }

    @Test
    public void getPipelineArtifactDTOs_sameNumberOfPipelines_correctCollection() {
        // Arrange
        PipelineDefinition firstPipelineDefinition = this.insertPipelineDefinitionIntoDatabase("FirstPipeline");
        List<Pipeline> firstPipelines = this.insertPipelinesIntoDatabase(1, firstPipelineDefinition.getId());

        int expectedCollectionSize = 1;
        String expectedId = firstPipelines.get(0).getId();

        // Act
        List<PipelineDto> actualResult = (List<PipelineDto>) this.pipelineService.getAllPipelineArtifactDTOs("", 1).getEntity();

        // Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(expectedId, actualResult.get(0).getId());
    }

    @Test
    public void getPipelineArtifactDTOs_lessPipelinesThanActual_correctCollection() {
        // Arrange
        PipelineDefinition firstPipelineDefinition = this.insertPipelineDefinitionIntoDatabase("FirstPipeline");
        List<Pipeline> firstPipelines = this.insertPipelinesIntoDatabase(2, firstPipelineDefinition.getId());

        int expectedCollectionSize = 1;
        String expectedId = firstPipelines.get(1).getId();

        // Act
        List<PipelineDto> actualResult = (List<PipelineDto>) this.pipelineService.getAllPipelineArtifactDTOs("", 1).getEntity();

        // Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(expectedId, actualResult.get(0).getId());
    }

    @Test
    public void getPipelineArtifactDTOs_morePipelinesThanActual_correctCollection() {
        // Arrange
        PipelineDefinition firstPipelineDefinition = this.insertPipelineDefinitionIntoDatabase("FirstPipeline");
        List<Pipeline> firstPipelines = this.insertPipelinesIntoDatabase(1, firstPipelineDefinition.getId());

        int expectedCollectionSize = 1;
        String expectedId = firstPipelines.get(0).getId();

        // Act
        List<PipelineDto> actualResult = (List<PipelineDto>) this.pipelineService.getAllPipelineArtifactDTOs("", 2).getEntity();

        // Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(expectedId, actualResult.get(0).getId());
    }

    @Test
    public void getPipelineArtifactDTOs_withSearchCriteriaExisting_correctCollection() {
        // Arrange
        PipelineDefinition firstPipelineDefinition = this.insertPipelineDefinitionIntoDatabase("FirstPipeline");
        List<Pipeline> firstPipelines = this.insertPipelinesIntoDatabase(1, firstPipelineDefinition.getId());
        PipelineDefinition secondPipelineDefinition = this.insertPipelineDefinitionIntoDatabase("SecondPipeline");
        this.insertPipelinesIntoDatabase(1, secondPipelineDefinition.getId());

        int expectedCollectionSize = 1;
        String expectedId = firstPipelines.get(0).getId();

        // Act
        List<PipelineDto> actualResult = (List<PipelineDto>) this.pipelineService.getAllPipelineArtifactDTOs("First", 1).getEntity();

        // Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(expectedId, actualResult.get(0).getId());
    }

    @Test
    public void getPipelineArtifactDTOs_withSearchCriteriaNotExisting_correctCollection() {
        // Arrange
        PipelineDefinition firstPipelineDefinition = this.insertPipelineDefinitionIntoDatabase("FirstPipeline");
        this.insertPipelinesIntoDatabase(1, firstPipelineDefinition.getId());

        int expectedCollectionSize = 0;

        // Act
        List<PipelineDto> actualResult = (List<PipelineDto>) this.pipelineService.getAllPipelineArtifactDTOs("Second", 1).getEntity();

        // Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
    }

    @Test
    public void getPipelineArtifactDTOs_fromSpecificPipelineIdExisting_correctCollection() {
        // Arrange
        PipelineDefinition firstPipelineDefinition = this.insertPipelineDefinitionIntoDatabase("FirstPipeline");
        List<Pipeline> firstPipelines = this.insertPipelinesIntoDatabase(2, firstPipelineDefinition.getId());
        String idToStartFrom = firstPipelines.get(1).getId();

        int expectedCollectionSize = 1;
        String expectedId = firstPipelines.get(0).getId();

        // Act
        List<PipelineDto> actualResult = (List<PipelineDto>) this.pipelineService.getAllPipelineArtifactDTOs("", 1, idToStartFrom).getEntity();

        // Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(expectedId, actualResult.get(0).getId());
    }

    @Test
    public void getPipelineArtifactDTOs_fromSpecificPipelineIdNotExisting_correctCollection() {
        // Arrange
        PipelineDefinition firstPipelineDefinition = this.insertPipelineDefinitionIntoDatabase("FirstPipeline");
        List<Pipeline> firstPipelines = this.insertPipelinesIntoDatabase(1, firstPipelineDefinition.getId());
        String idToStartFrom = firstPipelines.get(0).getId();

        int expectedCollectionSize = 1;
        String expectedId = firstPipelines.get(0).getId();

        // Act
        List<PipelineDto> actualResult = (List<PipelineDto>) this.pipelineService.getAllPipelineArtifactDTOs("", 1, "fakeId").getEntity();

        // Assert
        Assert.assertEquals(expectedCollectionSize, actualResult.size());
        Assert.assertEquals(expectedId, actualResult.get(0).getId());
    }

    private List<Pipeline> injectDataForTestingStatusUpdater() {
        List<Pipeline> pipelines = new ArrayList<>();
        List<Job> jobsToAdd = new ArrayList<>();
        List<Stage> stagesToAdd = new ArrayList<>();

        Pipeline firstPipeline = new Pipeline();
        firstPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        Stage stage = new Stage();

        Job firstJob = new Job();
        firstJob.setStatus(JobStatus.UNASSIGNED);

        Job secondJob = new Job();
        secondJob.setStatus(JobStatus.PASSED);

        jobsToAdd.add(firstJob);
        jobsToAdd.add(secondJob);
        stagesToAdd.add(stage);

        stage.setJobs(jobsToAdd);
        firstPipeline.setStages(stagesToAdd);
        firstPipeline.setMaterialsUpdated(true);
        firstPipeline.setPrepared(true);
        pipelines.add(firstPipeline);
        this.pipelineService.add(firstPipeline);

        Pipeline secondPipeline = new Pipeline();
        secondPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
        jobsToAdd = new ArrayList<>();
        stagesToAdd = new ArrayList<>();

        firstJob.setStatus(JobStatus.FAILED);

        secondJob.setStatus(JobStatus.RUNNING);

        jobsToAdd.add(firstJob);
        jobsToAdd.add(secondJob);
        stagesToAdd.add(stage);

        stage.setJobs(jobsToAdd);
        secondPipeline.setStages(stagesToAdd);
        pipelines.add(secondPipeline);
        this.pipelineService.add(secondPipeline);

        Pipeline thirdPipeline = new Pipeline();
        thirdPipeline.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        firstJob.setStatus(JobStatus.PASSED);

        secondJob.setStatus(JobStatus.PASSED);

        jobsToAdd = new ArrayList<>();
        stagesToAdd = new ArrayList<>();
        jobsToAdd.add(firstJob);
        jobsToAdd.add(secondJob);
        stagesToAdd.add(stage);

        stage.setJobs(jobsToAdd);
        thirdPipeline.setStages(stagesToAdd);
        pipelines.add(thirdPipeline);
        this.pipelineService.add(thirdPipeline);

        return pipelines;
    }

    private PipelineDefinition insertPipelineDefinitionIntoDatabase(String pipelineDefinitionName) {
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName(pipelineDefinitionName);
        this.pipelineDefinitionService.add(pipelineDefinition);

        return pipelineDefinition;
    }

    private List<Pipeline> insertPipelinesIntoDatabase(int numberOfPipelines, String pipelineDefinitionId) {
        List<Pipeline> pipelines = new ArrayList<>();
        for (int i = 1; i <= numberOfPipelines; i++) {
            Pipeline pipeline = new Pipeline();
            pipeline.setPipelineDefinitionId(pipelineDefinitionId);
            this.pipelineService.add(pipeline);
            pipelines.add(pipeline);
        }

        return pipelines;
    }
}
