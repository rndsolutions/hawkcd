package net.hawkengine.services.tests.filters.factories;

import net.hawkengine.services.filters.factories.AuthorizationServiceFactory;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AuthorizationServiceFactoryTests {
    private IAuthorizationService authorizationService;
    private AuthorizationServiceFactory authorizationServiceFactory;

    @Before
    public void setUp() {
        this.authorizationServiceFactory = new AuthorizationServiceFactory();
    }


    @Test
    public void passed_pipelineDefinitionService_correctAuthorizationService() {
        //Arrange
        String expectedService = "PipelineDefinitionService";
        String expectedAuthorizationServiceClass = "class net.hawkengine.services.filters.PipelineDefinitionAuthorizationService";

        //Act
        this.authorizationService = this.authorizationServiceFactory.create(expectedService);
        String actualAuthorizationServiceClass = this.authorizationService.getClass().toString();

        //Assert
        Assert.assertNotNull(this.authorizationService);
        Assert.assertEquals(actualAuthorizationServiceClass, expectedAuthorizationServiceClass);
    }

    @Test
    public void passed_agentService_correctAuthorizationService() {
        //Arrange
        String expectedService = "AgentService";
        String expectedAuthorizationServiceClass = "class net.hawkengine.services.filters.AgentAuthorizationService";

        //Act
        this.authorizationService = this.authorizationServiceFactory.create(expectedService);
        String actualAuthorizationServiceClass = this.authorizationService.getClass().toString();

        //Assert
        Assert.assertNotNull(this.authorizationService);
        Assert.assertEquals(actualAuthorizationServiceClass, expectedAuthorizationServiceClass);
    }

    @Test
    public void passed_pipelineService_correctAuthorizationService() {
        //Arrange
        String expectedService = "PipelineService";
        String expectedAuthorizationServiceClass = "class net.hawkengine.services.filters.PipelineAuthorizationService";

        //Act
        this.authorizationService = this.authorizationServiceFactory.create(expectedService);
        String actualAuthorizationServiceClass = this.authorizationService.getClass().toString();

        //Assert
        Assert.assertNotNull(this.authorizationService);
        Assert.assertEquals(actualAuthorizationServiceClass, expectedAuthorizationServiceClass);
    }

    @Test
    public void passed_pipelineGroupService_correctAuthorizationService() {
        //Arrange
        String expectedService = "PipelineGroupService";
        String expectedAuthorizationServiceClass = "class net.hawkengine.services.filters.PipelineGroupAuthorizationService";

        //Act
        this.authorizationService = this.authorizationServiceFactory.create(expectedService);
        String actualAuthorizationServiceClass = this.authorizationService.getClass().toString();

        //Assert
        Assert.assertNotNull(this.authorizationService);
        Assert.assertEquals(actualAuthorizationServiceClass, expectedAuthorizationServiceClass);
    }

    @Test
    public void passed_invalidService_null() {
        //Arrange
        String expectedService = "InvalidService";

        //Act
        this.authorizationService = this.authorizationServiceFactory.create(expectedService);

        //Assert
        Assert.assertNull(this.authorizationService);
    }
}
