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

    @Test
    public void getAll(){
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
        pipelineDefinition.setName("pipelinedefinition");
        this.pipelineDefinitionService.add(pipelineDefinition);
        this.pipeline = new Pipeline();
        pipeline.setPipelineDefinitionName(pipelineDefinition.getName());
        pipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        this.pipelineService.add(pipeline);
        this.stage = new Stage();
        stage.setPipelineId(pipeline.getId());
        this.stageService.add(stage);

        //Act
        ServiceResult actualResult = this.stageService.getById(stage.getId());
        Stage actualStatus = (Stage)actualResult.getObject();
        final String expectedMessage = actualResult.getObject().getClass().getSimpleName() +
                " "  + actualStatus.getId() + " retrieved successfully.";


        //Assert
        assertFalse(actualResult.hasError());
        assertNotNull(actualResult.getObject());
        assertEquals(expectedMessage,actualResult.getMessage());
        assertEquals(stage.getId(),((Stage) actualResult.getObject()).getId());
    }

    @Test
    public void getById_wrongId(){
        //Arrange
        this.pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("pipelinedefinition");
        this.pipelineDefinitionService.add(pipelineDefinition);
        this.pipeline = new Pipeline();
        pipeline.setPipelineDefinitionName(pipelineDefinition.getName());
        pipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        this.pipelineService.add(pipeline);
        this.stage = new Stage();
        stage.setPipelineId(pipeline.getId());
        this.stageService.add(stage);
        final String expectedMessage = "Stage not found.";

        //Act
        ServiceResult actualResult = this.stageService.getById("unexistingId");

        //Assert
        assertFalse(!actualResult.hasError());
        assertNull(actualResult.getObject());
        assertEquals(expectedMessage,actualResult.getMessage());
    }
    @Test
    public void add(){
        //Arrange
        this.pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("pipelinedefinition");
        this.pipelineDefinitionService.add(pipelineDefinition);
        this.pipeline = new Pipeline();
        pipeline.setPipelineDefinitionName(pipelineDefinition.getName());
        pipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        this.pipelineService.add(pipeline);
        this.stage = new Stage();
        stage.setPipelineId(pipeline.getId());

        //Act
        ServiceResult actualResult = this.stageService.add(stage);
        Stage actualStatus = (Stage)actualResult.getObject();
        final String expectedMessage = actualResult.getObject().getClass().getSimpleName() +
                " "  + actualStatus.getId() + " created successfully.";

        //Assert
        assertFalse(actualResult.hasError());
        assertNotNull(actualResult.getObject());
        assertEquals(expectedMessage,actualResult.getMessage());
        assertEquals(stage.getId(),((Stage) actualResult.getObject()).getId());

    }


    @Test
    public void update(){
        //Arrange
        this.pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("pipelinedefinition");
        this.pipelineDefinitionService.add(pipelineDefinition);
        this.pipeline = new Pipeline();
        pipeline.setPipelineDefinitionName(pipelineDefinition.getName());
        pipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        this.pipelineService.add(pipeline);
        this.stage = new Stage();
        stage.setPipelineId(pipeline.getId());
        stage.setStatus(StageStatus.FAILED);
        this.stageService.add(stage);


        //Act
        stage.setStatus(StageStatus.PASSED);
        ServiceResult actualResult = this.stageService.update(stage);
        Stage actualStatus = (Stage)actualResult.getObject();
        final String expectedMessage = actualResult.getObject().getClass().getSimpleName() +
                " " + actualStatus.getId() + " updated successfully.";




        //Assert
        assertFalse(actualResult.hasError());
        assertNotNull(actualResult.getObject());
        assertEquals(expectedMessage,actualResult.getMessage());
        assertEquals(StageStatus.PASSED,actualStatus.getStatus());


    }

    @Test
    public void delete(){
        //Arrange
        this.pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("pipelinedefinition");
        this.pipelineDefinitionService.add(pipelineDefinition);
        this.pipeline = new Pipeline();
        pipeline.setPipelineDefinitionName(pipelineDefinition.getName());
        pipeline.setPipelineDefinitionId(pipelineDefinition.getId());
        this.pipelineService.add(pipeline);
        this.stage = new Stage();
        stage.setPipelineId(pipeline.getId());
        Stage stage2 = stage;
        this.stageService.add(stage);
        this.stageService.add(stage2);

        //Act
        ServiceResult actualResult = this.stageService.delete(stage.getId());
        Stage actualStatus = (Stage)actualResult.getObject();
        final String expectedMessage = actualResult.getObject().getClass().getSimpleName() +
                " "  + actualStatus.getId() + " deleted successfully.";

        //Assert
        assertFalse(actualResult.hasError());
        assertNotNull(actualResult.getObject());
        assertEquals(expectedMessage,actualResult.getMessage());


    }

}
