package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.Stage;
import net.hawkengine.model.enums.StageStatus;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.StageService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import net.hawkengine.services.interfaces.IStageService;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import redis.clients.jedis.JedisPoolConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class StageServiceTests {
    private IPipelineService pipelineService;
    private IStageService stageService;
    private IPipelineDefinitionService pipelineDefinitionService;
    private Pipeline pipeline;
    private PipelineDefinition pipelineDefinition;
    private Stage stage;

    @Before
    public void setUp(){
        MockJedisPool mockJedisPool = new MockJedisPool(new JedisPoolConfig(),"testStageService");
        IDbRepository pipelineRepo = new RedisRepository(Pipeline.class,mockJedisPool);
        IDbRepository pipelinDefeRepo = new RedisRepository(PipelineDefinition.class,mockJedisPool);
        this.pipelineDefinitionService = new PipelineDefinitionService(pipelinDefeRepo);
        this.pipelineService = new PipelineService(pipelineRepo, pipelineDefinitionService);
        this.stageService = new StageService(this.pipelineService);
    }

    @Test
    public void getAll_receiveEmptyList(){
        //Arrange
        final String expectedMessage = "Stages retrieved successfully.";
        List<Stage> stageList = new ArrayList<>();

        //Act
        ServiceResult actualResult = this.stageService.getAll();

        //Assert
        assertFalse(actualResult.hasError());
        assertNotNull(actualResult.getObject());
        assertEquals(actualResult.getMessage(), expectedMessage);
        assertEquals(actualResult.getObject(), stageList);
    }

    @Test
    public void getAll_setOneObject_getOneObject(){
        //Arrange
        final String expectedMessage = "Stages retrieved successfully.";
        List<Stage> stageList = new ArrayList<>();
        this.insertIntoDb();
        stageList.add(this.stage);

        //Act
        ServiceResult actualResult = this.stageService.getAll();
        List<Stage> resultList = (List<Stage>)actualResult.getObject();
        Stage actualObject = resultList.stream().findFirst().get();

        //Assert
        assertFalse(actualResult.hasError());
        assertNotNull(actualResult.getObject());
        assertEquals(actualResult.getMessage(), expectedMessage);
        assertEquals(1, resultList.size());
        assertEquals(this.stage.getId(),actualObject.getId());
    }

    @Test
    public void getById_returnsOneObject(){

        //Arrange
        this.insertIntoDb();

        //Act
        ServiceResult actualResult = this.stageService.getById(this.stage.getId());
        Stage actualStage = (Stage)actualResult.getObject();
        String expectedMessage = actualResult.getObject().getClass().getSimpleName() +
                " "  + actualStage.getId() + " retrieved successfully.";

        //Assert
        assertFalse(actualResult.hasError());
        assertNotNull(actualResult.getObject());
        assertEquals(expectedMessage,actualResult.getMessage());
        assertEquals(this.stage.getId(),((Stage) actualResult.getObject()).getId());
    }

    @Test
    public void getById_wrongId_returnsError(){
        //Arrange
        UUID randomId = UUID.randomUUID();
        String expectedMessage = "Stage not found.";

        //Act
        ServiceResult actualResult = this.stageService.getById(randomId.toString());

        //Assert
        assertFalse(!actualResult.hasError());
        assertNull(actualResult.getObject());
        assertEquals(expectedMessage,actualResult.getMessage());
    }

    @Test
    public void addOneObject_returnsSuccessResult(){
        //Arrange
        this.insertIntoDb();

        //Act
        ServiceResult actualResult = this.stageService.add(this.stage);
        Stage actualStatus = (Stage)actualResult.getObject();
        String expectedMessage = actualResult.getObject().getClass().getSimpleName() +
                " "  + actualStatus.getId() + " created successfully.";

        //Assert
        assertFalse(actualResult.hasError());
        assertNotNull(actualResult.getObject());
        assertEquals(expectedMessage,actualResult.getMessage());
        assertEquals(this.stage.getId(),((Stage) actualResult.getObject()).getId());

    }

    @Test
    public void updateSingleObject_returnsSuccessResult(){
        //Arrange
        this.insertIntoDb();
        this.stageService.add(this.stage);

        //Act
        this.stage.setStatus(StageStatus.PASSED);
        ServiceResult actualResult = this.stageService.update(this.stage);
        Stage actualStatus = (Stage)actualResult.getObject();
        String expectedMessage = actualResult.getObject().getClass().getSimpleName() +
                " " + actualStatus.getId() + " updated successfully.";

        //Assert
        assertFalse(actualResult.hasError());
        assertNotNull(actualResult.getObject());
        assertEquals(expectedMessage,actualResult.getMessage());
        assertEquals(StageStatus.PASSED,actualStatus.getStatus());
    }

    @Test
    public void delete_oneStage_nullObject(){
        //Arrange
        this.insertIntoDb();
        Stage secondStage= this.stage;
        this.stageService.add(secondStage);

        //Act
        ServiceResult actualResult = this.stageService.delete(this.stage.getId());
        Stage actualStatus = (Stage)actualResult.getObject();
        String expectedMessage = actualResult.getObject().getClass().getSimpleName() +
                " "  + actualStatus.getId() + " deleted successfully.";

        //Assert
        assertFalse(actualResult.hasError());
        assertNotNull(actualResult.getObject());
        assertEquals(expectedMessage,actualResult.getMessage());
    }

    private void  insertIntoDb(){
        this.pipelineDefinition = new PipelineDefinition();
        this.pipelineDefinition.setName("pipelinedefinition");
        this.pipelineDefinitionService.add(this.pipelineDefinition);
        this.pipeline = new Pipeline();
        this.pipeline.setPipelineDefinitionName(this.pipelineDefinition.getName());
        this.pipeline.setPipelineDefinitionId(this.pipelineDefinition.getId());
        this.pipelineService.add(this.pipeline);
        this.stage = new Stage();
        this.stage.setPipelineId(this.pipeline.getId());
        this.stageService.add(this.stage);
    }
}
