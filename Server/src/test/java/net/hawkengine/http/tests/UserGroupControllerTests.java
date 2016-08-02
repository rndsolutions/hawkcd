package net.hawkengine.http.tests;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.UserGroupController;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.UserGroup;
import net.hawkengine.services.UserGroupService;
import net.hawkengine.services.UserService;
import net.hawkengine.services.interfaces.IUserGroupService;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.omg.IOP.ENCODING_CDR_ENCAPS;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserGroupControllerTests extends JerseyTest {
    private IUserGroupService userGroupService;
    private UserGroupController userGroupController;
    private UserGroup userGroup;
    private ServiceResult serviceResult;

    @BeforeClass
    public static void setUpClass() {
        ServerConfiguration.configure();
    }

    public Application configure() {
        this.userGroupService = Mockito.mock(UserGroupService.class);
        this.userGroupController = new UserGroupController(this.userGroupService);
        this.serviceResult = new ServiceResult();

        return new ResourceConfig().register(this.userGroupController);
    }

    @Test
    public void userGroupController_constructorTest_notNull() {

        UserGroupController userGroupController = new UserGroupController();

        assertNotNull(userGroupController);
    }

    @Test
    public void getAllUserGroups_nonExistingGroups_emptyList() {
        //Arrange
        List<UserGroup> expectedResult = new ArrayList<>();
        this.serviceResult.setObject(expectedResult);
        Mockito.when(this.userGroupService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/user-groups").request().get();
        List<UserGroup> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getAllUserGroups_existingGroups_twoGroups() {
        //Arrange
        this.prepareUserGroup();
        List<UserGroup> expectedResult = new ArrayList<>();
        expectedResult.add(this.userGroup);
        expectedResult.add(this.userGroup);
        this.serviceResult.setObject(expectedResult);
        Mockito.when(this.userGroupService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/user-groups").request().get();
        List<UserGroup> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(actualResult.size(), expectedResult.size());
    }

    @Test
    public void getUserGroupById_existingGroup_correctObject() {
        //Arrange
        this.prepareUserGroup();
        this.serviceResult.setObject(this.userGroup);
        Mockito.when(this.userGroupService.getById(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/user-groups/" + this.userGroup.getId()).request().get();
        UserGroup actualResult = response.readEntity(UserGroup.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.userGroup.getId(), actualResult.getId());
    }

    @Test
    public void getUserGroupById_nonExistingUserGroup_properErrorMessage() {
        //Arrange
        String expectedResult = "UserGroup not found.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedResult);
        Mockito.when(this.userGroupService.getById(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("user-groups/wrongId").request().get();
        String actualResult = response.readEntity(String.class);

        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void addUserGroup_oneObject_successMessage() {
        //Arrange
        this.prepareUserGroup();
        this.serviceResult.setObject(this.userGroup);
        Mockito.when(this.userGroupService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.userGroup, "application/json");

        //Act
        Response response = target("/user-groups").request().post(entity);
        UserGroup actualResult = response.readEntity(UserGroup.class);

        //Assert
        assertEquals(201, response.getStatus());
        assertEquals(this.userGroup.getId(), actualResult.getId());
    }

    /**
     * TODO: to be implemented after SchemaValidator refactoring. ;)
     *
     * @Test public void addUserGroup_invalidField_properErrorMessage()
     */

    @Test
    public void addUserGroup_existingObject_properErrorMessage() {
        //Arrange
        String expectedResult = "UserGroup already exists.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedResult);
        this.prepareUserGroup();
        Mockito.when(this.userGroupService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.userGroup, "application/json");

        //Act
        Response response = target("/user-groups").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void updateUserGroup_existingObject_updatedUserGroup() {
        //Arrange
        this.prepareUserGroup();
        this.serviceResult.setObject(this.userGroup);
        this.userGroup.setName("updated-group");
        Mockito.when(this.userGroupService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.userGroup, "application/json");

        //Act
        Response response = target("/user-groups").request().put(entity);
        UserGroup actualResult = response.readEntity(UserGroup.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.userGroup.getName(), actualResult.getName());
    }

    @Test
    public void updateUserGroup_nonExistingObject_properErrorMessage() {
        //Arrange
        this.prepareUserGroup();
        String expectedResult = "UserGroup not found.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedResult);
        Mockito.when(this.userGroupService.update(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.userGroup, "application/json");

        //Act
        Response response = target("/user-groups").request().put(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }


    /**
     * TODO: service for duplication (of property or else) to be implemented.
     *
     * @Test public void updateUserGroup_withSameNameOrProperty_properErrorMessage()
     */

    /**
     * TODO: to be implemented after SchemaValidator refactoring. ;)
     *
     * @Test public void updateUserGroup_ivalidField_properErrorMessage()
     */

    @Test
    public void deleteUserGroup_userGroup_successMessage() {
        //Arrange
        this.prepareUserGroup();
        Mockito.when(this.userGroupService.delete(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/user-groups/" + this.userGroupService).request().delete();

        //Assert
        assertEquals(204, response.getStatus());
    }

    @Test
    public void deleteUserGroup_nonExistingUserGroup_properErrorMessage() {
        //Arrange
        String expectedMessage = "UserGroup not found.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.userGroupService.delete(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/user-groups/wrongId").request().delete();
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void addUserToUserGroup_existingObject_success(){
        //Arrange
        this.prepareUserGroup();
        User user = new User();
        this.userGroupService.addUserToGroup(user.getId(),this.userGroup.getId());
        this.serviceResult.setError(false);
        this.serviceResult.setObject(this.userGroup);
        Entity entity = Entity.entity(user.getId(),"application/json");
        Mockito.when(this.userGroupService.addUserToGroup(Mockito.anyString(),Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/user-groups/assign-user/"+this.userGroup.getId()).request().post(entity);

        //Assert
        assertEquals(200,response.getStatus());
    }

    @Test
    public void addUserToUserGroup_nonExistingObject_errorMessage(){
        //Arrange
        this.prepareUserGroup();
        User user = new User();
        this.serviceResult.setError(true);
        Entity entity = Entity.entity(user.getId(),"application/json");
        Mockito.when(this.userGroupService.addUserToGroup(Mockito.anyString(),Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/user-groups/assign-user/"+this.userGroup.getId()).request().post(entity);

        //Assert
        assertEquals(400,response.getStatus());
    }


    @Test
    public void removeUserFromUserGroup_existingObject_success(){
        //Arrange
        this.prepareUserGroup();
        User user = new User();
        this.userGroupService.addUserToGroup(user.getId(),this.userGroup.getId());
        this.serviceResult.setError(false);
        this.serviceResult.setObject(this.userGroup);
        Mockito.when(this.userGroupService.removeUserFromGroup(Mockito.anyString(),Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/user-groups/"+this.userGroup.getId()+"/"+user.getId()).request().delete();

        //Assert
        assertEquals(204,response.getStatus());
    }

    @Test
    public void removeUserFromUserGroup_nonExistingObject_badRequest(){
        //Arrange
        this.prepareUserGroup();
        User user = new User();
        this.serviceResult.setError(true);
        this.serviceResult.setObject(null);
        Mockito.when(this.userGroupService.removeUserFromGroup(Mockito.anyString(),Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/user-groups/"+this.userGroup.getId()+"/"+user.getId()).request().delete();

        //Assert
        assertEquals(400,response.getStatus());
    }

    private void prepareUserGroup() {
        this.userGroup = new UserGroup();
        this.userGroup.setName("UserGroup");
    }


}

