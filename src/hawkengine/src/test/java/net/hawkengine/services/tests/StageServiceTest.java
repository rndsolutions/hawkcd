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
import net.hawkengine.services.Service;
import net.hawkengine.services.StageService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import net.hawkengine.services.interfaces.IStageService;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.JedisPoolConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class StageServiceTest  {
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

    //TODO: naming conventions
    @Test
    public void getAll(){
        //TODO: add simple stage, and check whether getAll() returns list with one object inside
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
    public void getById(){
        //Arrange
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
    public void getById_wrongId(){
        //Arrange
        //TODO: Extract in method add stage() there is repetition
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
        String expectedMessage = "Stage not found.";

        //Act
        ServiceResult actualResult = this.stageService.getById("unexistingId");

        //Assert
        assertFalse(!actualResult.hasError());
        assertNull(actualResult.getObject());
        assertEquals(expectedMessage,actualResult.getMessage());
    }

    @Test
    //TODO: more tests; check add with same name
    public void add(){
        //Arrange
        this.pipelineDefinition = new PipelineDefinition();
        this.pipelineDefinition.setName("pipelinedefinition");
        this.pipelineDefinitionService.add(this.pipelineDefinition);
        this.pipeline = new Pipeline();
        this.pipeline.setPipelineDefinitionName(this.pipelineDefinition.getName());
        this.pipeline.setPipelineDefinitionId(this.pipelineDefinition.getId());
        this.pipelineService.add(this.pipeline);
        this.stage = new Stage();
        this.stage.setPipelineId(this.pipeline.getId());

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

    //TODO: more tests; check add with same name
    @Test
    public void update(){
        //Arrange
        this.pipelineDefinition = new PipelineDefinition();
        this.pipelineDefinition.setName("pipelinedefinition");
        this.pipelineDefinitionService.add(this.pipelineDefinition);
        this.pipeline = new Pipeline();
        this.pipeline.setPipelineDefinitionName(this.pipelineDefinition.getName());
        this.pipeline.setPipelineDefinitionId(this.pipelineDefinition.getId());
        this.pipelineService.add(this.pipeline);
        this.stage = new Stage();
        this.stage.setPipelineId(this.pipeline.getId());
        this.stage.setStatus(StageStatus.FAILED);
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

    //TODO: add test for delete of invalid object
    @Test
    public void delete(){
        //Arrange
        this.pipelineDefinition = new PipelineDefinition();
        this.pipelineDefinition.setName("pipelinedefinition");
        this.pipelineDefinitionService.add(this.pipelineDefinition);
        this.pipeline = new Pipeline();
        this.pipeline.setPipelineDefinitionName(this.pipelineDefinition.getName());
        this.pipeline.setPipelineDefinitionId(this.pipelineDefinition.getId());
        this.pipelineService.add(this.pipeline);
        this.stage = new Stage();
        this.stage.setPipelineId(this.pipeline.getId());
        //TODO: naming?
        Stage stage2 = this.stage;
        this.stageService.add(this.stage);
        this.stageService.add(stage2);

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
}
