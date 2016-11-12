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
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.payload.Permission;
import io.hawkcd.services.UserService;
import io.hawkcd.services.filters.UserAuthorizationService;
import io.hawkcd.services.filters.interfaces.IAuthorizationService;
import io.hawkcd.services.interfaces.IUserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

public class UserAuthorizationServiceTests {
    User firstUser;
    User secondUser;

    IAuthorizationService authorizationService;

    Gson jsonConverter;

    private IDbRepository<User> mockedUserRepository;
    private IUserService mockedUserService;

    @BeforeClass
    public static void setUpClass() {
        Config.configure();
    }

    @Before
    public void setUp() {
        this.firstUser = new User();
        this.secondUser = new User();

        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();

        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testUserAuthorizationService");
        this.mockedUserRepository = new RedisRepository(User.class, mockedPool);
        this.mockedUserService = new UserService(this.mockedUserRepository);

        this.authorizationService = new UserAuthorizationService();
    }

    @Test
    public void getAll_withPermissions_twoUsers() {
        //Arrange
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(this.firstUser);
        expectedUsers.add(this.secondUser);

        //Act
        List<User> actualUsers = this.authorizationService.getAll(this.createPermissions(), expectedUsers);

        //Assert
        Assert.assertEquals(expectedUsers.size(), actualUsers.size());
    }

    @Test
    public void getAll_withoutPermissions_null() {
        //Arrange
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(this.firstUser);
        expectedUsers.add(this.secondUser);

        List<Permission> expectedPermissions = new ArrayList<>();

        //Act
        List<User> actualUsers = this.authorizationService.getAll(expectedPermissions, expectedUsers);

        //Assert
        Assert.assertNull(actualUsers);
    }

    @Test
    public void getById_withPermissions_true() {
        //Arrange
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(this.firstUser);
        expectedUsers.add(this.secondUser);

        //Act
        boolean hasPermission = this.authorizationService.getById(this.firstUser.getId(), this.createPermissions());

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void getById_withoutPermissions_true() {
        //Arrange
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(this.firstUser);
        expectedUsers.add(this.secondUser);

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean hasPermission = this.authorizationService.getById(this.firstUser.getId(), permissions);

        //Assert
        Assert.assertFalse(hasPermission);
    }

    @Test
    public void update_withPermissions_true() {
        //Arrange
        User expectedUser = new User();
        String expectedEntityAsString = this.jsonConverter.toJson(expectedUser);

        //Act
        boolean hasPermission = this.authorizationService.update(expectedEntityAsString, this.createPermissions());

        //Assert
        Assert.assertTrue(hasPermission);

    }

    @Test
    public void update_withoutPermissions_false() {
        //Arrange
        User expectedUser = new User();
        String expectedEntityAsString = this.jsonConverter.toJson(expectedUser);
        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean hasPermission = this.authorizationService.update(expectedEntityAsString, permissions);

        //Assert
        Assert.assertFalse(hasPermission);

    }

    @Test
    public void delete_withPermissions_true() {
        //Act
        boolean hasPermission = this.authorizationService.delete(this.firstUser.getId(), this.createPermissions());

        //Assert
        Assert.assertTrue(hasPermission);

    }

    @Test
    public void delete_withoutPermissions_false() {
        //Arrange
        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean hasPermission = this.authorizationService.add(this.firstUser.getId(), permissions);

        //Assert
        Assert.assertFalse(hasPermission);

    }

    @Test
    public void add_withPermissions_true() {
        //Arrange
        User expectedUser = new User();
        String expectedEntityAsString = this.jsonConverter.toJson(expectedUser);

        //Act
        boolean hasPermission = this.authorizationService.add(expectedEntityAsString, this.createPermissions());

        //Assert
        Assert.assertTrue(hasPermission);

    }

    @Test
    public void add_withoutPermissions_false() {
        //Arrange
        User expectedUser = new User();
        String expectedEntityAsString = this.jsonConverter.toJson(expectedUser);
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
