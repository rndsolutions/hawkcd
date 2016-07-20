package net.hawkengine.http.tests;

import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.JobDefinitionController;
import net.hawkengine.model.ExecTask;
import net.hawkengine.model.Job;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.services.JobDefinitionService;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.StageDefinitionService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import static org.junit.Assert.assertEquals;

public class JobDefinitionControllerTests extends JerseyTest {
    private static PipelineDefinitionService pipelineDefinitionService;
    private static StageDefinitionService stageDefinitionService;
    private static JobDefinitionService jobDefinitionService;
    private JobDefinition jobDefinition ;
    private PipelineDefinition pipelineDefinition;
    private StageDefinition stageDefinition;

    public Application configure() {
        return new ResourceConfig(JobDefinitionController.class);
    }

    @BeforeClass
    public static void login() throws IOException, URISyntaxException {
        RedisManager.initializeEmbededDb(6379);
        RedisManager.connect("redis");
        pipelineDefinitionService = new PipelineDefinitionService();
        stageDefinitionService = new StageDefinitionService();
        jobDefinitionService = new JobDefinitionService();
    }

    @AfterClass
    public static void logout() {
        RedisManager.release();
    }


    @Test
    public void getAllJobDefinitions_request_emptyList() {
        //Arrange
        List<JobDefinition> expectedResult = new ArrayList<>();

        //Act
        Response response = target("/job-definitions").request().get();
        List<JobDefinition> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getById_request_JobDefinitionObject(){
        //Arrange
        this.prepareJobDefinition();
        jobDefinitionService.add(this.jobDefinition);

        //Act
        Response response = target("/job-definitions/"+this.jobDefinition.getId()).request().get();
        JobDefinition actualResult = response.readEntity(JobDefinition.class);

        //Assert
        assertEquals(200,response.getStatus());
        assertEquals(this.jobDefinition.getId(), actualResult.getId());
        this.removeJobDefinition();
    }

    @Test
    public void getById_getJobDefinitionByWrongId_errorMessage(){
        //Arrange
        String expectedResult = "JobDefinition not found.";

        //Act
        Response response = target("/job-definitions/wrongId").request().get();

        //Assert
        assertEquals(404,response.getStatus());
        assertEquals(expectedResult,response.readEntity(String.class));
    }

    @Test
    public void addJobDefinition_oneJobDefinition_successMessage(){
        //Arrange
        this.prepareJobDefinition();
        Entity entity = Entity.entity(this.jobDefinition,"application/json");

        //Act
        Response response = target("/job-definitions").request().post(entity);
        JobDefinition actualResult = response.readEntity(JobDefinition.class);

        //Assert
        assertEquals(201, response.getStatus());
        assertEquals(this.jobDefinition.getId(), actualResult.getId());
        this.removeJobDefinition();
    }

    /*
    @Test
    public void addJobDefinition_invalidField_errorMessage(){
        //Arrange
        this.prepareJobDefinition();
        Entity entity = Entity.entity(this.jobDefinition, "application/json");

        //Act
        Response response = target().request("/pipeline-definitions).post(entity);

        //Assert
        assertEquals(400,response.getStatus());
    }
*/

    @Test
    public void updateJobDefinition_existingJobDefinition_updatedJobDefinition(){
        //Arrange
        this.prepareJobDefinition();
        jobDefinitionService.add(this.jobDefinition);
        this.jobDefinition.setName("name-updated");
        Entity entity = Entity.entity(this.jobDefinition,"application/json");

        //Act
        Response response = target("/job-definitions").request().put(entity);
        JobDefinition actualResult = response.readEntity(JobDefinition.class);

        assertEquals(200,response.getStatus());
        assertEquals(this.jobDefinition.getName(), actualResult.getName());
        this.removeJobDefinition();
    }

    /*

    //Да се прегледа JobDefinitionService delete метод
    @Test
    public void deleteJobDefinition_jobDefinition_successMessage(){
        //Arrange
        this.prepareJobDefinition();
        jobDefinitionService.add(this.jobDefinition);


        //Act
        Response response = target("/job-definitions/" + this.jobDefinition.getId()).request().delete();
        System.out.println(response.readEntity(String.class));


        //Assert
        assertEquals(204,response.getStatus());
        this.removeJobDefinition();
    }
*/

    @Test
    public void deleteJobDefinition_nonExistingJobDefinition_errorMessage(){
        //Arrange
        this.prepareJobDefinition();
        String expectedMessage = "JobDefinition does not exists.";

        //Act
        Response response = target("/job-definitions/wrongId").request().delete();

        //Assert
        assertEquals(404,response.getStatus());
        assertEquals(expectedMessage, response.readEntity(String.class));
    }



    private void prepareJobDefinition() {
        this.pipelineDefinition = new PipelineDefinition();
        this.stageDefinition = new StageDefinition();
        this.stageDefinition.setPipelineDefinitionId(this.pipelineDefinition.getId());
        this.jobDefinition = new JobDefinition();
        this.jobDefinition.setStageDefinitionId(this.stageDefinition.getId());
        pipelineDefinitionService.add(this.pipelineDefinition);
        stageDefinitionService.add(this.stageDefinition);

    }

    private void removeJobDefinition(){
        pipelineDefinitionService.delete(this.pipelineDefinition.getId());
    }
}
