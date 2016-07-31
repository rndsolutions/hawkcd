package net.hawkengine.http;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.UserGroupService;
import net.hawkengine.services.interfaces.IUserGroupService;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Application;

import static org.junit.Assert.assertNotNull;

public class UserGroupControllerTests extends JerseyTest {
    private IUserGroupService userGroupService;
    private UserGroupController userGroupController;
    private ServiceResult serviceResult;

    @BeforeClass
    public static void setUpClass(){
        ServerConfiguration.configure();
    }

    public Application configure(){
        this.userGroupService = Mockito.mock(UserGroupService.class);
        this.userGroupController = new UserGroupController(this.userGroupService);
        this.serviceResult = new ServiceResult();

        return new ResourceConfig().register(this.userGroupController);
    }

    @Test
    public void userGroupController_constructorTest_notNull(){

        UserGroupController userGroupController = new UserGroupController();

        assertNotNull(userGroupController);
    }


}

