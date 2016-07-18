package net.hawkengine.http.tests;


import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.JobDefinitionController;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.services.PipelineDefinitionService;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class PipelineDefinitionControllerTest extends JerseyTest {
    private static PipelineDefinitionService pipelineDefinitionService;
    private PipelineDefinition pipelineDefinition;

    public Application configure() {
        return new ResourceConfig(PipelineDefinition.class);
    }

    @BeforeClass
     public static void login() throws IOException, URISyntaxException {
        RedisManager.initializeEmbededDb(6379);
        RedisManager.connect("redis");
        pipelineDefinitionService = new PipelineDefinitionService();
    }

    @AfterClass
    public static void logout() {
        RedisManager.release();
    }

    @Test
    public void getAllPipelineDefinition(){
        //Arrange
        List<PipelineDefinition> expectedResult = new ArrayList<>();

        //Act
        Response response = target("/pipeline-definitions").request().get();
        List<PipelineDefinition> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(expectedResult,actualResult);
    }

    private void preparePipelineDefinition() {

    }

    private void removePipelineDefinition() {
        this.pipelineDefinitionService.delete(this.pipelineDefinition.getId());
    }


}
