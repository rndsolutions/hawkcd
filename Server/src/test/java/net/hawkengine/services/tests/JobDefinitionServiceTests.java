package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.services.JobDefinitionService;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.RevisionService;
import net.hawkengine.services.StageDefinitionService;
import net.hawkengine.services.interfaces.IJobDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IStageDefinitionService;
import org.junit.*;
import org.mockito.Mockito;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.when;

public class JobDefinitionServiceTests {

    private IPipelineDefinitionService pipelineDefinitionService;
    private IStageDefinitionService stageDefinitionService;
    private IJobDefinitionService jobDefinitionService;
    private RevisionService mockedRevisionService;
    private PipelineDefinition pipelineDefinition;
    private Random randomGenerator;
    private int randomMinValue = 0;
    private int randomStageMaxValue = 4;
    private int randomJobMaxValue = 24;
    private int randomStageIndex;
    private int randomJobIndex;
    private String notFoundMessage = "JobDefinition not found.";
    private String existingNameErrorMessage = "JobDefinition with the same name exists.";
    private String retrievalSuccessMessage = "JobDefinitions retrieved successfully.";
    private String deletionSuccessMessage = "JobDefinition deleted successfully.";
    private String notDeletedFailureMessage = "not deleted successfully";
    private ServiceResult count;

    @BeforeClass
    public static void setUpClass() {
        ServerConfiguration.configure();
    }

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testJobDefinitionService");
        IDbRepository pipelineRepo = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.mockedRevisionService = Mockito.mock(RevisionService.class);
        this.pipelineDefinitionService = new PipelineDefinitionService(pipelineRepo, this.mockedRevisionService);
        this.stageDefinitionService = new StageDefinitionService(pipelineDefinitionService);
        this.jobDefinitionService = new JobDefinitionService(stageDefinitionService);
        this.pipelineDefinition = new PipelineDefinition();
        this.randomGenerator = new Random();
        injectDataForTestingStageDefinitionService(5, 5);
        this.randomStageIndex = randomGenerator.nextInt((this.randomStageMaxValue - this.randomMinValue) + 1) + this.randomMinValue;
        this.randomJobIndex = randomGenerator.nextInt((this.randomJobMaxValue - this.randomMinValue) + 1) + this.randomMinValue;
        this.count = this.jobDefinitionService.getAllInPipeline(pipelineDefinition.getId());
    }

    private void injectDataForTestingStageDefinitionService(int numberOfStagesToAdd, int numberOfJobsToAdd) {
        pipelineDefinition.setName("mockedPipelineDefinition");
        List<StageDefinition> stageDefinitions = new ArrayList<>();
        List<JobDefinition> jobDefinitions = new ArrayList<>();

        for (int i = 0; i < numberOfStagesToAdd; i++) {
            StageDefinition stageDefinition = new StageDefinition();
            stageDefinition.setName("stage" + i);
            stageDefinition.setPipelineDefinitionId(pipelineDefinition.getId());
            for (int j = 0; j < numberOfJobsToAdd; j++) {
                JobDefinition currentJobDefinition = new JobDefinition();
                currentJobDefinition.setName("job" + j);
                currentJobDefinition.setPipelineDefinitionId(pipelineDefinition.getId());
                currentJobDefinition.setStageDefinitionId(stageDefinition.getId());
                jobDefinitions.add(currentJobDefinition);
            }
            stageDefinition.setJobDefinitions(jobDefinitions);
            stageDefinitions.add(stageDefinition);
        }
        pipelineDefinition.setStageDefinitions(stageDefinitions);
        pipelineDefinitionService.add(pipelineDefinition);
    }


    @Test
    public void getById_validId_correctObject() {
        //Arrange
        StageDefinition expectedStageDefinition = this.getStageDefinitionAtIndex(this.randomStageIndex);
        JobDefinition expectedJobDefinition = this.getJobDefinitionAtIndex(this.randomStageIndex, this.randomJobIndex);
        String successMessage = "JobDefinition " + expectedJobDefinition.getId() + " retrieved successfully.";

        //Act
        ServiceResult actualResult = jobDefinitionService.getById(expectedJobDefinition.getId());
        JobDefinition actualJobDefinition = (JobDefinition) actualResult.getObject();

        //Assert
        Assert.assertNotNull(actualResult);
        Assert.assertEquals(expectedJobDefinition.getId(), actualJobDefinition.getId());
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertEquals(expectedJobDefinition.getPipelineDefinitionId(), actualJobDefinition.getPipelineDefinitionId());
        Assert.assertEquals(expectedJobDefinition.getStageDefinitionId(), actualJobDefinition.getStageDefinitionId());
        Assert.assertEquals(expectedJobDefinition.getName(), actualJobDefinition.getName());
        Assert.assertEquals(expectedStageDefinition.getPipelineDefinitionId(), actualJobDefinition.getPipelineDefinitionId());
        Assert.assertEquals(successMessage, actualResult.getMessage());
    }

    @Test
    public void getById_invalidId_null() {
        //Arrange
        String invalidId = "12345";

        //Act
        ServiceResult actualResult = jobDefinitionService.getById(invalidId);

        //Assert
        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
        Assert.assertEquals(this.notFoundMessage, actualResult.getMessage());
        Assert.assertNull(actualResult.getObject());
    }

    @Test
    public void getAll_returnAllJobDefinitions() {
        //Assert
        int expectedJobDefinitionsCount = 125;

        //Act
        ServiceResult actualResult = jobDefinitionService.getAll();
        List<JobDefinition> actualJobDefinitions = (List<JobDefinition>) actualResult.getObject();

        //Assert
        Assert.assertNotNull(actualResult.getObject());
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertEquals(expectedJobDefinitionsCount, actualJobDefinitions.size());
        Assert.assertEquals(this.retrievalSuccessMessage, actualResult.getMessage());
    }

    @Test
    public void add_validObject_addedCorrectly() {
        //Arrange
        JobDefinition expectedObject = this.getJobDefinitionAtIndex(this.randomStageIndex, this.randomJobIndex);
        JobDefinition jobDefinitionToAdd = new JobDefinition();
        jobDefinitionToAdd.setPipelineDefinitionId(expectedObject.getPipelineDefinitionId());
        jobDefinitionToAdd.setStageDefinitionId(expectedObject.getStageDefinitionId());
        jobDefinitionToAdd.setName("fakeObject");
        String successMessage = "JobDefinition " + jobDefinitionToAdd.getId() + " added successfully.";

        //Act
        ServiceResult actualResult = jobDefinitionService.add(jobDefinitionToAdd);
        JobDefinition actualResultObject = (JobDefinition) actualResult.getObject();

        //Assert
        Assert.assertNotNull(actualResult.getObject());
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertEquals(successMessage, actualResult.getMessage());
        Assert.assertEquals(actualResultObject.getId(), jobDefinitionToAdd.getId());
    }

    @Test
    public void add_existingObject_returnsProperErrorMessage() {
        //Arrange
        JobDefinition jobDefinitionToAdd = this.getJobDefinitionAtIndex(this.randomStageIndex, this.randomJobIndex);

        //Act
        ServiceResult actualResult = jobDefinitionService.add(jobDefinitionToAdd);
        JobDefinition actualResultObject = (JobDefinition) actualResult.getObject();

        //Assert
        Assert.assertNotNull(actualResult.getObject());
        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
        Assert.assertEquals(jobDefinitionToAdd, actualResultObject);
        Assert.assertEquals(this.existingNameErrorMessage, actualResult.getMessage());
    }

    @Test
    public void update_withValidName_correctObject() {
        //Arrange
        JobDefinition expectedObject = this.getJobDefinitionAtIndex(this.randomStageIndex, this.randomJobIndex);
        expectedObject.setName("NameNotPresent");

        //Act
        ServiceResult actualResult = jobDefinitionService.update(expectedObject);
        JobDefinition actualResultObject = (JobDefinition) actualResult.getObject();
        String successMessage = "JobDefinition " + expectedObject.getId() + " updated successfully.";

        //Assert
        Assert.assertNotNull(actualResult.getObject());
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertEquals(expectedObject, actualResultObject);
        Assert.assertEquals(successMessage, actualResult.getMessage());
    }

    @Test
    public void update_withInValidName_properErrorMessage() {
        //Arrange
        JobDefinition expectedObject = this.getJobDefinitionAtIndex(this.randomStageIndex, this.randomJobIndex);

        //Act
        ServiceResult actualResult = jobDefinitionService.update(expectedObject);
        JobDefinition actualResultObject = (JobDefinition) actualResult.getObject();

        //Assert
        Assert.assertNotNull(actualResult.getObject());
        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
        Assert.assertEquals(expectedObject, actualResultObject);
        Assert.assertEquals(this.existingNameErrorMessage, actualResult.getMessage());
    }

    @Test
    public void update_withOtherChangesThanName_correctUpdate() {
        //Arrange
        JobDefinition expectedObject = this.getJobDefinitionAtIndex(this.randomStageIndex, this.randomJobIndex);
        List<EnvironmentVariable> environmentVariables = new ArrayList<>();
        EnvironmentVariable environmentVariable = new EnvironmentVariable();
        EnvironmentVariable environmentVariable1 = new EnvironmentVariable();
        environmentVariables.add(environmentVariable);
        environmentVariables.add(environmentVariable1);
        expectedObject.setEnvironmentVariables(environmentVariables);

        //Act
        ServiceResult actualResult = jobDefinitionService.update(expectedObject);
        JobDefinition actualResultObject = (JobDefinition) actualResult.getObject();

        //Assert
        Assert.assertNotNull(actualResult.getObject());
        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
        Assert.assertEquals(expectedObject, actualResultObject);
        Assert.assertEquals(actualResultObject.getEnvironmentVariables(),environmentVariables);
        Assert.assertEquals(this.existingNameErrorMessage, actualResult.getMessage());
    }

    @Test
    public void delete_validObject_removesSuccessfully() {
        //Arrange
        JobDefinition expectedObject = this.getJobDefinitionAtIndex(this.randomStageIndex, this.randomJobIndex);

        //Act
        ServiceResult actualResult = jobDefinitionService.delete(expectedObject.getId());
        JobDefinition actualResultObject = (JobDefinition) actualResult.getObject();

        //Assert
        Assert.assertNull(actualResultObject);
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertEquals(this.deletionSuccessMessage, actualResult.getMessage());
    }

    @Test
    public void delete_invalidObject_returnsTheObject() {
        //Arrange
        JobDefinitionService mockedJobDefinitionService = Mockito.mock(JobDefinitionService.class);
        JobDefinition jobDefinitionFromDB = this.getJobDefinitionAtIndex(this.randomStageIndex, this.randomJobIndex);
        JobDefinition jobDefinitionToReturn = new JobDefinition();
        jobDefinitionToReturn.setStageDefinitionId(jobDefinitionFromDB.getStageDefinitionId());
        jobDefinitionToReturn.setPipelineDefinitionId(jobDefinitionFromDB.getPipelineDefinitionId());
        jobDefinitionToReturn.setName(jobDefinitionFromDB.getName());
        ServiceResult mockedResult = new ServiceResult();
        mockedResult.setNotificationType(NotificationType.ERROR);
        mockedResult.setMessage(this.notDeletedFailureMessage);
        mockedResult.setObject(jobDefinitionToReturn);
        when(mockedJobDefinitionService.delete("id")).thenReturn(mockedResult);

        //Act
        ServiceResult actualResult = mockedJobDefinitionService.delete("id");
        JobDefinition actualResultObject = (JobDefinition) actualResult.getObject();

        //Assert
        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
        Assert.assertEquals(jobDefinitionToReturn.getId(), actualResultObject.getId());
        Assert.assertEquals(jobDefinitionToReturn.getStageDefinitionId(), actualResultObject.getStageDefinitionId());
        Assert.assertEquals(jobDefinitionToReturn.getPipelineDefinitionId(), actualResultObject.getPipelineDefinitionId());
        Assert.assertEquals(this.notDeletedFailureMessage, actualResult.getMessage());
    }

    @Test
    public void delete_notFoundId_returnActualNotDeletedObject() {
        //Arrange
        List<JobDefinition> allJobDefinitionsInPipeline = (List<JobDefinition>) this.jobDefinitionService.getAllInPipeline(pipelineDefinition.getId()).getObject();
        int allJobDefinitionsCount = allJobDefinitionsInPipeline.size();
        ServiceResult actualResult = null;
        JobDefinition notDeletedJobDefinition = null;

        //Act
        for (int i = 0; i < allJobDefinitionsCount; i++) {
            JobDefinition currentJobDefinition = allJobDefinitionsInPipeline.get(i);
            if (i == allJobDefinitionsCount - 1) {
                notDeletedJobDefinition = currentJobDefinition;
                actualResult = this.jobDefinitionService.delete(currentJobDefinition.getId());
            } else {
                this.jobDefinitionService.delete(currentJobDefinition.getId());
            }
        }
        JobDefinition actualResultObject = (JobDefinition) actualResult.getObject();

        //Assert
        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
        Assert.assertEquals(notDeletedJobDefinition.getStageDefinitionId(), actualResultObject.getStageDefinitionId());
        Assert.assertEquals(notDeletedJobDefinition.getName(), actualResultObject.getName());
        Assert.assertEquals(notDeletedJobDefinition.getPipelineDefinitionId(), actualResultObject.getPipelineDefinitionId());
        Assert.assertEquals("JobDefinition cannot delete the last job definition.", actualResult.getMessage());
    }

    @Test
    public void delete_invalidId_returnsNullObjectAndProperErrorMessage() {
        //Arrange
        String invalidId = "invalidID";

        //Act
        ServiceResult actualResult = this.jobDefinitionService.delete(invalidId);

        //Assert
        Assert.assertEquals(NotificationType.ERROR, actualResult.getNotificationType());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals("JobDefinition does not exists.", actualResult.getMessage());
    }

    @Test
    public void getAllInStage_returnsAllJobDefinitionsInStage() {
        //Arrange
        StageDefinition stageFromMockedDb = this.getStageDefinitionAtIndex(this.randomStageIndex);
        int expectedNumberOfJobDefinitions = 25;

        //Act
        ServiceResult actualResult = jobDefinitionService.getAllInStage(stageFromMockedDb.getId());
        List<JobDefinition> actualResultObject = (List<JobDefinition>) actualResult.getObject();

        //Assert
        Assert.assertNotNull(actualResultObject);
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertEquals(expectedNumberOfJobDefinitions, actualResultObject.size());
        Assert.assertEquals(this.retrievalSuccessMessage, actualResult.getMessage());
    }

    @Test
    public void getAllInPipeline_returnsAllJobDefinitionsInAllStagesInPipeline() {
        //Arrange
        int expectedNumberOfJobDefinitions = 125;

        //Act
        ServiceResult actualResult = jobDefinitionService.getAllInPipeline(pipelineDefinition.getId());
        List<JobDefinition> actualResultObject = (List<JobDefinition>) actualResult.getObject();
        int actualNumberOfJobDefinitions = actualResultObject.size();

        //Assert
        Assert.assertNotNull(actualResultObject);
        Assert.assertEquals(NotificationType.SUCCESS, actualResult.getNotificationType());
        Assert.assertEquals(expectedNumberOfJobDefinitions, actualNumberOfJobDefinitions);
        Assert.assertEquals(this.retrievalSuccessMessage, actualResult.getMessage());
    }

    private JobDefinition getJobDefinitionAtIndex(int stageIndex, int jobIndex) {
        return pipelineDefinition.getStageDefinitions().get(stageIndex).getJobDefinitions().get(jobIndex);
    }

    private StageDefinition getStageDefinitionAtIndex(int stageIndex) {
        return pipelineDefinition.getStageDefinitions().get(stageIndex);
    }

    @Ignore
    public void troubleTest() {
        /**
         * Flow updates everything as it should be, but when it comes back to the test, all the data is not updated!
         * */
        StageDefinition stageDefinition = this.getStageDefinitionAtIndex(this.randomStageIndex);
        List<JobDefinition> allJobDefinitionsInStage = (List<JobDefinition>) this.jobDefinitionService.getAllInStage(stageDefinition.getId()).getObject();
        int allJobDefinitionsCount = allJobDefinitionsInStage.size();
        ServiceResult actualResult = null;
        JobDefinition notDeletedJobDefinition = null;

        for (int i = 0; i < allJobDefinitionsCount; i++) {
            JobDefinition currentJobDefinition = allJobDefinitionsInStage.get(i);
            if(i == allJobDefinitionsCount - 1){
                actualResult = this.jobDefinitionService.delete(currentJobDefinition.getId());

            } else {
                actualResult = this.jobDefinitionService.delete(currentJobDefinition.getId());
            }
        }
    }
}