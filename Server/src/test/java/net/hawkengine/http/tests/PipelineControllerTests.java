package net.hawkengine.http.tests;

import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.PipelineController;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.IOP.ENCODING_CDR_ENCAPS;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class PipelineControllerTests extends JerseyTest {
    private static PipelineService pipelineService;
    private Pipeline pipeline;
    private PipelineDefinition pipelineDefinition;
    private PipelineDefinitionService pipelineDefinitionService;

    public Application configure() {
        return new ResourceConfig(PipelineController.class);
    }

    @BeforeClass
    public static void login() throws IOException, URISyntaxException {
        RedisManager.initializeEmbededDb(6379);
        RedisManager.connect("redis");
        pipelineService = new PipelineService();
    }

    @AfterClass
    public static void logout() {
        RedisManager.release();
    }

    @Test
    public void getAllPipelines_request_emptyList(){
        //Arrange
        List<PipelineService> expectedResult = new ArrayList<>();

        //Act
        Response response = target("/pipelines").request().get();
        List<PipelineService> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200,response.getStatus());
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void getPipelineById_request_PipelineObject(){
        //Arrange
        this.prepearePipeline();
        pipelineService.add(this.pipeline);

        //Act
        Response response = target("/pipelines/" + this.pipeline.getId()).request().get();

        //Assert
        assertEquals(200,response.getStatus());
        this.removePipeline();
    }

    @Test
    public void getPipelineById_wrongIdRequest_errorMessage(){
        //Arrange
        this.prepearePipeline();
        String expectedResult = "Pipeline not found.";

        //Act
        Response response = target("/pipelines/wrongId").request().get();
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(404,response.getStatus());
        assertEquals(expectedResult,actualResult);
        this.removePipeline();
    }
/*
    @Test
    public void addPipeline_pipelineRun_successMessage(){
        //Arrange
        this.prepearePipeline();
        Entity entity = Entity.entity(this.pipeline,"application/json");

        //Act
        Response response = target("/pipelines/").request().post(entity);
        System.out.println(response.readEntity(String.class));


        //Assert
        assertEquals(200,response.getStatus());
        this.removePipeline();


    }
*/

    private void prepearePipeline(){
        this.pipelineDefinition = new PipelineDefinition();
        this.pipelineDefinition.setName("pipelineDefinition");
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.pipelineDefinitionService.add(this.pipelineDefinition);
        this.pipeline = new Pipeline();
        this.pipeline.setPipelineDefinitionName(this.pipelineDefinition.getName());
        this.pipeline.setPipelineDefinitionId(this.pipelineDefinition.getId());
    }

    private void removePipeline(){
        pipelineService.delete(this.pipeline.getId());
        pipelineDefinitionService.delete(this.pipelineDefinition.getId());
    }




}
