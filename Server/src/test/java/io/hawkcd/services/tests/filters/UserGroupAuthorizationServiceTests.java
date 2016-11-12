package io.hawkcd.services.tests.filters;

import com.fiftyonred.mock_jedis.MockJedisPool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.hawkcd.Config;
import io.hawkcd.utilities.deserializers.MaterialDefinitionAdapter;
import io.hawkcd.utilities.deserializers.TaskDefinitionAdapter;
import io.hawkcd.utilities.deserializers.WsContractDeserializer;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.db.redis.RedisRepository;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.model.User;
import io.hawkcd.model.UserGroup;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.payload.Permission;
import io.hawkcd.services.UserGroupService;
import io.hawkcd.services.UserService;
import io.hawkcd.services.filters.UserGroupAuthorizationService;
import io.hawkcd.services.filters.interfaces.IAuthorizationService;
import io.hawkcd.services.interfaces.IUserGroupService;
import io.hawkcd.services.interfaces.IUserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

public class UserGroupAuthorizationServiceTests {
    private UserGroup firstUserGroup;
    private UserGroup secondUserGroup;

    private IAuthorizationService authorizationService;

    private Gson jsonConverter;

    private IDbRepository<UserGroup> mockedUserGroupRepository;
    private IUserGroupService mockedUserGroupService;
    private IDbRepository<User> mockedUserRepository;
    private IUserService mockedUserService;

    @BeforeClass
    public static void setUpClass() {
        Config.configure();
    }

    @Before
    public void setUp() {
        this.firstUserGroup = new UserGroup();
        this.secondUserGroup = new UserGroup();

        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();

        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testUserGroupAuthorizationService");
        this.mockedUserRepository = new RedisRepository(User.class, mockedPool);
        this.mockedUserService = new UserService(this.mockedUserRepository);
        this.mockedUserGroupRepository = new RedisRepository(UserGroup.class, mockedPool);
        this.mockedUserGroupService = new UserGroupService(this.mockedUserGroupRepository, this.mockedUserService);

        this.authorizationService = new UserGroupAuthorizationService();
    }

    @Test
    public void getAll_withPermissions_twoUsers() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        //Act
        List<UserGroup> actualUserGroups = this.authorizationService.getAll(this.createPermissions(), expectedUserGroups);

        //Assert
        Assert.assertEquals(expectedUserGroups.size(), actualUserGroups.size());
    }

    @Test
    public void getAll_withoutPermissions_null() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        List<Permission> expectedPermissions = new ArrayList<>();

        //Act
        List<UserGroup> actualUserGroups = this.authorizationService.getAll(expectedPermissions, expectedUserGroups);

        //Assert
        Assert.assertNull(actualUserGroups);
    }

    @Test
    public void getById_withPermissions_true() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        //Act
        boolean hasPermission = this.authorizationService.getById(this.firstUserGroup.getId(), this.createPermissions());

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void getById_withoutPermissions_true() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean hasPermission = this.authorizationService.getById(this.firstUserGroup.getId(), permissions);

        //Assert
        Assert.assertFalse(hasPermission);
    }

    @Test
    public void update_withPermissions_true() {
        //Arrange
        UserGroup expectedUserGroup = new UserGroup();
        String expectedEntityAsString = this.jsonConverter.toJson(expectedUserGroup);

        //Act
        boolean hasPermission = this.authorizationService.update(expectedEntityAsString, this.createPermissions());

        //Assert
        Assert.assertTrue(hasPermission);

    }

    @Test
    public void update_withoutPermissions_false() {
        //Arrange
        UserGroup expectedUserGroup = new UserGroup();
        String expectedEntityAsString = this.jsonConverter.toJson(expectedUserGroup);
        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean hasPermission = this.authorizationService.update(expectedEntityAsString, permissions);

        //Assert
        Assert.assertFalse(hasPermission);

    }

    @Test
    public void delete_withPermissions_true() {
        //Act
        boolean hasPermission = this.authorizationService.delete(this.firstUserGroup.getId(), this.createPermissions());

        //Assert
        Assert.assertTrue(hasPermission);

    }

    @Test
    public void delete_withoutPermissions_false() {
        //Arrange
        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean hasPermission = this.authorizationService.add(this.firstUserGroup.getId(), permissions);

        //Assert
        Assert.assertFalse(hasPermission);

    }

    @Test
    public void add_withPermissions_true() {
        //Arrange
        UserGroup expectedUserGroup = new UserGroup();
        String expectedEntityAsString = this.jsonConverter.toJson(expectedUserGroup);

        //Act
        boolean hasPermission = this.authorizationService.add(expectedEntityAsString, this.createPermissions());

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void add_withoutPermissions_false() {
        //Arrange
        UserGroup expectedUserGroup = new UserGroup();
        String expectedEntityAsString = this.jsonConverter.toJson(expectedUserGroup);
        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean hasPermission = this.authorizationService.add(expectedEntityAsString, permissions);

        //Assert
        Assert.assertFalse(hasPermission);

    }

    private List<Permission> createPermissions() {
        List<Permission> permissions = new ArrayList<>();

        Permission firstPermission = new Permission();
        firstPermission.setPermissionScope(PermissionScope.SERVER);
        firstPermission.setPermissionType(PermissionType.ADMIN);
        firstPermission.setPermittedEntityId(PermissionScope.SERVER.toString());

        permissions.add(firstPermission);

        return permissions;
    }
}
