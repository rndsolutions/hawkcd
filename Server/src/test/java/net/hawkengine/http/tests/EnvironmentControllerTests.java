package net.hawkengine.http.tests;

import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.EnvironmentController;
import net.hawkengine.model.Environment;
import net.hawkengine.services.EnvironmentService;
import net.hawkengine.services.interfaces.IEnvironmentService;

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

public class EnvironmentControllerTests extends JerseyTest {
    private IEnvironmentService environmentService;
    private Environment environment;

    @BeforeClass
    public static void login() throws IOException, URISyntaxException {
        RedisManager.initializeEmbededDb(6379);
        RedisManager.connect("redis");
    }

    @AfterClass
    public static void logout() {
        RedisManager.release();
    }

    public Application configure() {
        return new ResourceConfig(EnvironmentController.class);
    }

    @Test
    public void getAllEnvironments_request_emptyList() {
        //Arrange
        List<Environment> expectedResult = new ArrayList<>();

        //Act
        Response response = target("/environments").request().get();
        List<Environment> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void addEnvironment_oneEnvironment_successMessage() {
        //Arrange
        this.prepareEnvironmentsForDb();
        Entity entity = Entity.entity(this.environment, "application/json");

        //Act
        Response response = target("/environments").request().post(entity);
        Environment actualResult = response.readEntity(Environment.class);

        //Assert
        assertEquals(201, response.getStatus());
        assertEquals(this.environment.getId(), actualResult.getId());
        this.removeEnvironmentFromDb();
    }

    @Test
    public void deleteEnvironment_environmentId_successMessage() {
        //Arrange
        this.prepareEnvironmentsForDb();
        this.environmentService.add(this.environment);

        //Act
        Response response = target("/environments/" + this.environment.getId()).request().delete();

        //Assert
        assertEquals(204, response.getStatus());
    }


    private void prepareEnvironmentsForDb() {
        this.environmentService = new EnvironmentService();
        this.environment = new Environment();
    }

    private void removeEnvironmentFromDb() {
        if (environmentService != null) {
            this.environmentService.delete(environment.getId());
        }
    }

}
