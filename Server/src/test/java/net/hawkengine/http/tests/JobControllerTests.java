package net.hawkengine.http.tests;
import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.JobController;
import net.hawkengine.model.Job;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.Stage;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.JobService;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.StageService;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class JobControllerTests  extends JerseyTest {
    private static PipelineService pipelineService;
    private static PipelineDefinitionService pipelineDefinitionService;
    private static StageService stageService;
    private static JobService jobService;
    private Pipeline pipeline;
    private PipelineDefinition pipelineDefinition;
    private Stage stage;
    private Job job;

    public Application configure() {
        return new ResourceConfig(JobController.class);
    }

    @BeforeClass
    public static void login() throws IOException, URISyntaxException {
        RedisManager.initializeEmbededDb(6379);
        RedisManager.connect("redis");
        pipelineDefinitionService = new PipelineDefinitionService();
        pipelineService = new PipelineService();
        stageService = new StageService();
        jobService = new JobService();
    }

    @Test
    public void getAllJobs_request_emptyList(){
        //Arrange
        List<Job> expectedResult = new ArrayList<>();

        //Act
        Response response = target("/jobs").request().get();
        List<Job> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getJobById_request_JobObject() {
        //Arrange
        this.prepareJob();
        jobService.add(this.job);

        //Act
        Response response = target("/jobs/" + this.job.getId()).request().get();
        Job actualResult = response.readEntity(Job.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.job.getId(), actualResult.getId());
        this.removeJob();
    }

    @Test
    public void getJobById_wrongIdRequest_errorMessage() {
        //Arrange
        String expectedResult = "Job not found.";

        //Act
        Response response = target("/jobs/wrongId").request().get();
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedResult, actualResult);

    }

    @AfterClass
    public static void logout() {
        RedisManager.release();
    }

    private void prepareJob(){
        this.pipelineDefinition = new PipelineDefinition();
        this.pipeline = new Pipeline();
        this.stage = new Stage();
        this.pipelineDefinition.setName("pipelineDefinition");
        pipelineDefinitionService.add(this.pipelineDefinition);
        this.stage.setPipelineId(this.pipeline.getId());
        this.pipeline.setPipelineDefinitionName(this.pipelineDefinition.getName());
        this.pipeline.setPipelineDefinitionId(this.pipelineDefinition.getId());
        pipelineDefinitionService.update(this.pipelineDefinition);
        pipelineService.add(this.pipeline);
        this.job = new Job();
        this.job.setStageId(this.stage.getId());
        stageService.add(this.stage);
    }

    private void removeJob() {
        pipelineService.delete(this.pipeline.getId());
        pipelineDefinitionService.delete(this.pipelineDefinition.getId());
    }
}
