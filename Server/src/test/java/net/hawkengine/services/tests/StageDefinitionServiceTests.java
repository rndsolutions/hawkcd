package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.RevisionService;
import net.hawkengine.services.StageDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IStageDefinitionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

public class StageDefinitionServiceTests {
    private IStageDefinitionService mockedStageDefinitionService;
    private IPipelineDefinitionService mockedPipelineDefinitionService;
    private RevisionService mockedRevisionService;
    private StageDefinition expectedStageDefinition;
    private PipelineDefinition expectedPipelineDefinition;

    @BeforeClass
    public static void setUpClass() {
        ServerConfiguration.configure();
    }

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testStageDefinitionService");
        IDbRepository mockedPipelineRepo = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.mockedRevisionService = Mockito.mock(RevisionService.class);
        this.mockedPipelineDefinitionService = new PipelineDefinitionService(mockedPipelineRepo, this.mockedRevisionService);
        this.mockedStageDefinitionService = new StageDefinitionService(this.mockedPipelineDefinitionService);
        this.expectedPipelineDefinition = new PipelineDefinition();
        this.expectedStageDefinition = new StageDefinition();
        this.expectedStageDefinition.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
    }

    @Test
    public void stageDefinitionService_getByIdWithValidInput_oneObject() {
        this.injectDataForTestingStageDefinitionService(3);

        StageDefinition actualStageDefinition = (StageDefinition) this.mockedStageDefinitionService.getById(this.expectedStageDefinition.getId()).getObject();

        Assert.assertNotNull(actualStageDefinition);
        Assert.assertEquals(this.expectedStageDefinition.getId(), actualStageDefinition.getId());
    }

    @Test
    public void stageDefinitionService_getByIdWithInvalidInput_noObject() {
        this.injectDataForTestingStageDefinitionService(1);

        ServiceResult actualServiceResult = this.mockedStageDefinitionService.getById("invalidId");

        Assert.assertNull(actualServiceResult.getObject());
        Assert.assertEquals(NotificationType.ERROR, actualServiceResult.getNotificationType());
    }

    @Test
    public void stageDefinitionService_getByIdInPipelineWithValidInput_oneObject() {
        this.injectDataForTestingStageDefinitionService(3);

        StageDefinition actualStageDefinition = (StageDefinition) this.mockedStageDefinitionService.getByIdInPipeline(this.expectedStageDefinition.getId(), this.expectedPipelineDefinition.getId()).getObject();

        Assert.assertNotNull(actualStageDefinition);
        Assert.assertEquals(this.expectedStageDefinition.getId(), actualStageDefinition.getId());
    }

    @Test
    public void stageDefinitionService_getByIdInPipelineWithInvalidInput_noObject() {
        this.injectDataForTestingStageDefinitionService(3);
        String expectedServiceResultMessage = String.format(TestsConstants.SERVICE_RESULT_NOT_FOUND_MESSAGE, "StageDefinition");

        ServiceResult actualServiceResult = this.mockedStageDefinitionService.getByIdInPipeline("invalidId", this.expectedPipelineDefinition.getId());

        Assert.assertNull(actualServiceResult.getObject());
        Assert.assertEquals(NotificationType.ERROR, actualServiceResult.getNotificationType());
        Assert.assertEquals(expectedServiceResultMessage, actualServiceResult.getMessage());
    }

    @Test
    public void stageDefinitionService_getAll_threeObjects() {
        this.injectDataForTestingStageDefinitionService(1);

        ServiceResult serviceResult = this.mockedStageDefinitionService.getAll();
        List<StageDefinition> actualStageDefinitions = (List<StageDefinition>) serviceResult.getObject();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_THREE_OBJECTS, actualStageDefinitions.size());
    }

    @Test
    public void stageDefinitionService_getAllInPipeline_twoObjects() {
        this.injectDataForTestingStageDefinitionService(1);

        ServiceResult serviceResult = this.mockedStageDefinitionService.getAllInPipeline(this.expectedPipelineDefinition.getId());
        List<StageDefinition> actualStageDefinitions = (List<StageDefinition>) serviceResult.getObject();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_TWO_OBJECTS, actualStageDefinitions.size());
    }

    @Test
    public void stageDefinitionService_addWithValidInput_oneObject() {
        this.injectDataForTestingStageDefinitionService(3);
        StageDefinition stageDefinitionToAdd = new StageDefinition();
        stageDefinitionToAdd.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        StageDefinition actualStageDefinition = (StageDefinition) this.mockedStageDefinitionService.add(stageDefinitionToAdd).getObject();

        Assert.assertNotNull(actualStageDefinition);
        Assert.assertEquals(stageDefinitionToAdd.getId(), actualStageDefinition.getId());
    }

    @Test
    public void stageDefinitionService_addWithInvalidInputId_noObject() {
        this.injectDataForTestingStageDefinitionService(3);

        ServiceResult actualServiceResult = this.mockedStageDefinitionService.add(this.expectedStageDefinition);

        Assert.assertNull(actualServiceResult.getObject());
        Assert.assertEquals(NotificationType.ERROR, actualServiceResult.getNotificationType());
    }

    @Test
    public void stageDefinitionService_addWithInvalidInputName_noObject() {
        this.injectDataForTestingStageDefinitionService(3);
        StageDefinition stageDefinitionToAdd = new StageDefinition();
        stageDefinitionToAdd.setName("someName");
        stageDefinitionToAdd.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        ServiceResult actualServiceResult = this.mockedStageDefinitionService.add(stageDefinitionToAdd);

        Assert.assertNull(actualServiceResult.getObject());
        Assert.assertEquals(NotificationType.ERROR, actualServiceResult.getNotificationType());
    }

    @Test
    public void stageDefinitionService_updateWithValidInput_oneObject() {
        this.injectDataForTestingStageDefinitionService(1);

        this.expectedStageDefinition.setName("changedName");

        StageDefinition actualStageDefinition = (StageDefinition) this.mockedStageDefinitionService.update(this.expectedStageDefinition).getObject();

        Assert.assertEquals(this.expectedStageDefinition.getName(), actualStageDefinition.getName());
    }

    @Test
    public void stageDefinitionService_updateWithInvalidId_noObject() {
        this.injectDataForTestingStageDefinitionService(1);

        StageDefinition stageDefinitionToUpdate = new StageDefinition();
        stageDefinitionToUpdate.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());

        ServiceResult actualServiceResult = this.mockedStageDefinitionService.update(stageDefinitionToUpdate);

        Assert.assertNull(actualServiceResult.getObject());
        Assert.assertEquals(NotificationType.ERROR, actualServiceResult.getNotificationType());
    }

    @Test
    public void stageDefinitionService_updateWithInvalidName_noObject() {
        this.injectDataForTestingStageDefinitionService(1);

        StageDefinition stageDefinitionToUpdate = new StageDefinition();
        List<StageDefinition> expectedStageDefinitions = this.expectedPipelineDefinition.getStageDefinitions();

        for (StageDefinition stDefinition : expectedStageDefinitions) {
            if (stDefinition.getName() == null) {
                stDefinition.setName("someName");
                stageDefinitionToUpdate = stDefinition;
            }
        }

        ServiceResult actualServiceResult = this.mockedStageDefinitionService.update(stageDefinitionToUpdate);

        Assert.assertNull(actualServiceResult.getObject());
        Assert.assertEquals(NotificationType.ERROR, actualServiceResult.getNotificationType());
    }

    @Test
    public void stageDefinitionService_deleteWithValidId_noError() {
        this.injectDataForTestingStageDefinitionService(1);

        ServiceResult actualServiceResult = this.mockedStageDefinitionService.delete(this.expectedStageDefinition.getId());

        Assert.assertEquals(NotificationType.SUCCESS, actualServiceResult.getNotificationType());
    }

    @Test
    public void stageDefinitionService_deleteWithInvalidId_noError() {
        this.injectDataForTestingStageDefinitionService(1);

        ServiceResult actualServiceResult = this.mockedStageDefinitionService.delete("someInvalidName");

        Assert.assertEquals(NotificationType.ERROR, actualServiceResult.getNotificationType());
    }

    private void injectDataForTestingStageDefinitionService(int numberOfStagesToAdd) {
        List<StageDefinition> expectedStageDefinitions = new ArrayList<>();
        for (int i = 0; i < numberOfStagesToAdd; i++) {
            StageDefinition stageDefinitionToAdd = new StageDefinition();
            stageDefinitionToAdd.setPipelineDefinitionId(this.expectedPipelineDefinition.getId());
            expectedStageDefinitions.add(stageDefinitionToAdd);
        }
        this.expectedStageDefinition.setName("someName");
        expectedStageDefinitions.add(this.expectedStageDefinition);
        this.expectedPipelineDefinition.setStageDefinitions(expectedStageDefinitions);
        this.mockedPipelineDefinitionService.add(this.expectedPipelineDefinition);

        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        expectedStageDefinitions = new ArrayList<>();
        for (int i = 0; i < numberOfStagesToAdd; i++) {
            StageDefinition stageDefinitionToAdd = new StageDefinition();
            stageDefinitionToAdd.setPipelineDefinitionId(pipelineDefinition.getId());
            expectedStageDefinitions.add(stageDefinitionToAdd);
        }
        pipelineDefinition.setStageDefinitions(expectedStageDefinitions);
        this.mockedPipelineDefinitionService.add(pipelineDefinition);

    }
}
