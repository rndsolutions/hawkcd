package net.hawkengine.services.tests.filters;

import com.fiftyonred.mock_jedis.MockJedisPool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.*;
import net.hawkengine.model.dto.ConversionObject;
import net.hawkengine.model.dto.UserDto;
import net.hawkengine.model.dto.UserGroupDto;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineGroupService;
import net.hawkengine.services.UserGroupService;
import net.hawkengine.services.UserService;
import net.hawkengine.services.filters.SecurityService;
import net.hawkengine.services.filters.interfaces.ISecurityService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineGroupService;
import net.hawkengine.services.interfaces.IUserGroupService;
import net.hawkengine.services.interfaces.IUserService;
import net.hawkengine.ws.WsObjectProcessor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

public class SecurityServiceTests {
    Gson jsonConverter;
    private UserGroup firstUserGroup;
    private UserGroup secondUserGroup;
    private PipelineGroup firstPipelineGroup;
    private PipelineGroup secondPipelineGroup;
    private PipelineDefinition firstPipelineDefinition;
    private PipelineDefinition secondPipelineDefinition;
    private PipelineDefinition thirdPipelineDefinition;
    private PipelineDefinition fourthPipelineDefinition;

    private WsObjectProcessor mockedWsObjectProcessor;
    private IDbRepository<UserGroup> mockedUserGroupRepository;
    private IUserGroupService mockedUserGroupService;
    private IDbRepository<User> mockedUserRepository;
    private IUserService mockedUserService;
    private IDbRepository<PipelineGroup> mockedPipelineGroupRepository;
    private IPipelineGroupService mockedPipelineGroupService;
    private IDbRepository<PipelineDefinition> mockedPipelineDefinitionRepository;
    private IPipelineDefinitionService mockedPipelineDefinitionService;

    private ISecurityService securityService;

    @BeforeClass
    public static void setUpClass() {
        ServerConfiguration.configure();
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

        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testSecurityService");
        this.mockedUserRepository = new RedisRepository(User.class, mockedPool);
        this.mockedUserService = new UserService(this.mockedUserRepository);
        this.mockedUserGroupRepository = new RedisRepository(UserGroup.class, mockedPool);
        this.mockedUserGroupService = new UserGroupService(this.mockedUserGroupRepository, this.mockedUserService);

        this.mockedWsObjectProcessor = Mockito.mock(WsObjectProcessor.class);

        this.mockedPipelineGroupRepository = new RedisRepository(PipelineGroup.class, mockedPool);
        this.mockedPipelineGroupService = new PipelineGroupService(this.mockedPipelineGroupRepository);

        this.mockedPipelineDefinitionRepository = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.mockedPipelineDefinitionService = new PipelineDefinitionService(this.mockedPipelineDefinitionRepository);

        this.securityService = new SecurityService(this.mockedWsObjectProcessor, this.mockedPipelineDefinitionService, this.mockedUserGroupService, this.mockedPipelineGroupService);
    }

    @Test
    public void getAll_withPermissionForTwoObjects_twoEntities() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "getAll", new ConversionObject[]{});

        //Act
        List<UserGroup> actualServiceResult = this.securityService.getAll(expectedUserGroups, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_TWO_OBJECTS, actualServiceResult.size());
    }

    @Test
    public void getAll_withoutPermission_null() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "getAll", new ConversionObject[]{});

        List<Permission> permissions = new ArrayList<>();

        //Act
        List actualServiceResult = this.securityService.getAll(expectedUserGroups, contract.getClassName(), permissions);

        //Assert
        Assert.assertNull(actualServiceResult);
    }

    @Test
    public void getPipelineDTOs_withPermissionsForOneObject_twoEntities() {
        List<PipelineGroup> expectedPipelineGroups = new ArrayList<>();
        this.createPipelineDefinitionsAndPipelineGroups();
        expectedPipelineGroups.add(this.firstPipelineGroup);
        expectedPipelineGroups.add(this.secondPipelineGroup);

        expectedPipelineGroups.get(0).getPipelines().add(this.firstPipelineDefinition);
        expectedPipelineGroups.get(0).getPipelines().add(this.secondPipelineDefinition);
        expectedPipelineGroups.get(1).getPipelines().add(this.thirdPipelineDefinition);
        expectedPipelineGroups.get(1).getPipelines().add(this.fourthPipelineDefinition);

        WsContractDto contract = this.createContractDto("PipelineGroupService", "net.hawkengine.services", "getPipelineDTOs", new ConversionObject[]{});

        //Act
        List actualServiceResult = this.securityService.getPipelineDTOs(expectedPipelineGroups, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_TWO_OBJECTS, actualServiceResult.size());

    }

    @Test
    public void getPipelineDTOs_withoutPermissions_noEntity() {
        List<PipelineGroup> expectedPipelineGroups = new ArrayList<>();
        this.createPipelineDefinitionsAndPipelineGroups();
        expectedPipelineGroups.add(this.firstPipelineGroup);
        expectedPipelineGroups.add(this.secondPipelineGroup);

        expectedPipelineGroups.get(0).getPipelines().add(this.firstPipelineDefinition);
        expectedPipelineGroups.get(0).getPipelines().add(this.secondPipelineDefinition);
        expectedPipelineGroups.get(1).getPipelines().add(this.thirdPipelineDefinition);
        expectedPipelineGroups.get(1).getPipelines().add(this.fourthPipelineDefinition);

        WsContractDto contract = this.createContractDto("PipelineGroupService", "net.hawkengine.services", "getPipelineDTOs", new ConversionObject[]{});

        List<Permission> permissions = new ArrayList<>();

        //Act
        List actualServiceResult = this.securityService.getPipelineDTOs(expectedPipelineGroups, contract.getClassName(), permissions);

        //Assert
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS, actualServiceResult.size());

    }

    @Test
    public void getById_withPermission_true() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        ConversionObject conversionObject = this.createConversionObject("java.lang.String", this.firstUserGroup.getId());
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "getById", new ConversionObject[]{conversionObject});

        //Act
        boolean actualServiceResult = this.securityService.getById(this.firstUserGroup.getId(), contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertEquals(true, actualServiceResult);
    }

    @Test
    public void getById_withoutPermission_false() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        ConversionObject conversionObject = this.createConversionObject("java.lang.String", this.firstUserGroup.getId());
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "getById", new ConversionObject[]{conversionObject});

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.getById(this.firstUserGroup.getId(), contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void add_withPermission_true() {
        //Arrange
        UserGroup userGroupToAdd = new UserGroup();

        String userGroupToAddAsString = this.jsonConverter.toJson(userGroupToAdd);

        ConversionObject conversionObject = this.createConversionObject("net.hawkserver.models", userGroupToAddAsString);
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "add", new ConversionObject[]{conversionObject});

        //Act
        boolean actualServiceResult = this.securityService.add(userGroupToAddAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void add_withoutPermission_fasle() {
        //Arrange
        UserGroup userGroupToAdd = new UserGroup();

        String userGroupToAddAsString = this.jsonConverter.toJson(userGroupToAdd);

        ConversionObject conversionObject = this.createConversionObject("net.hawkserver.models", userGroupToAddAsString);
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "add", new ConversionObject[]{conversionObject});

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.add(userGroupToAddAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void update_withPermission_true() {
        //Arrange
        this.firstUserGroup.setName("TestName");
        String firstUserGroupAsString = this.jsonConverter.toJson(this.firstUserGroup);

        ConversionObject conversionObject = this.createConversionObject("net.hawkserver.models", firstUserGroupAsString);
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "update", new ConversionObject[]{conversionObject});

        //Act
        boolean actualServiceResult = this.securityService.update(firstUserGroupAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void update_withoutPermission_false() {
        //Arrange
        this.firstUserGroup.setName("TestName");

        String firstUserGroupAsString = this.jsonConverter.toJson(this.firstUserGroup);

        ConversionObject conversionObject = this.createConversionObject("net.hawkserver.models", firstUserGroupAsString);
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "update", new ConversionObject[]{conversionObject});


        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.update(firstUserGroupAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void delete_withPermission_true() {
        //Arrange
        ConversionObject conversionObject = this.createConversionObject("java.lang.String", this.firstUserGroup.getId());
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "delete", new ConversionObject[]{conversionObject});

        //Act
        boolean actualServiceResult = this.securityService.delete(this.firstUserGroup.getId(), contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void delete_withoutPermission_false() {
        //Arrange
        ConversionObject conversionObject = this.createConversionObject("java.lang.String", this.firstUserGroup.getId());
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "delete", new ConversionObject[]{conversionObject});

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.delete(this.firstUserGroup.getId(), contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void addUserGroupDto_withPermission_true() {
        //Arrange
        UserGroupDto userGroupToAdd = new UserGroupDto();
        String userGroupToAddAsString = this.jsonConverter.toJson(userGroupToAdd);

        ConversionObject conversionObject = this.createConversionObject("net.hawkserver.models", userGroupToAddAsString);
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "addUserGroupDto", new ConversionObject[]{conversionObject});

        //Act
        boolean actualServiceResult = this.securityService.addUserGroupDto(userGroupToAddAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void addUserGroupDto_withoutPermission_false() {
        //Arrange
        UserGroupDto userGroupToAdd = new UserGroupDto();
        String userGroupToAddAsString = this.jsonConverter.toJson(userGroupToAdd);

        ConversionObject conversionObject = this.createConversionObject("net.hawkserver.models", userGroupToAddAsString);
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "addUserGroupDto", new ConversionObject[]{conversionObject});

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.add(userGroupToAddAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void addUserToGroup_withPermission_true() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        User userToAdd = new User();
        userToAdd.setPassword("123");
        this.mockedUserService.add(userToAdd);

        String userToAddAsString = this.jsonConverter.toJson(userToAdd);
        String firstUserGoupAsString = this.jsonConverter.toJson(this.firstUserGroup);

        ConversionObject firstConversionObject = this.createConversionObject("net.hawkserver.models", userToAddAsString);
        ConversionObject secondConversionObject = this.createConversionObject("net.hawkserver.models", firstUserGoupAsString);
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "assignUserToGroup", new ConversionObject[]{firstConversionObject, secondConversionObject});

        //Act
        boolean actualServiceResult = this.securityService.assignUserToGroup(userToAddAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void addUserToGroup_withoutPermission_false() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        User userToAdd = new User();
        userToAdd.setPassword("123");
        this.mockedUserService.add(userToAdd);

        String userToAddAsString = this.jsonConverter.toJson(userToAdd);
        String firstUserGoupAsString = this.jsonConverter.toJson(this.firstUserGroup);

        ConversionObject firstConversionObject = this.createConversionObject("net.hawkserver.models", userToAddAsString);
        ConversionObject secondConversionObject = this.createConversionObject("net.hawkserver.models", firstUserGoupAsString);
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "assignUserToGroup", new ConversionObject[]{firstConversionObject, secondConversionObject});

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.assignUserToGroup(firstUserGoupAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void updateUserGroupDto_withPermission_true() {
        //Arrange
        UserGroupDto userGroupDto = new UserGroupDto();
        userGroupDto.setName("testName");
        this.mockedUserGroupService.addUserGroupDto(userGroupDto);

        userGroupDto.setName("changedName");

        String userGroupDtoAsString = this.jsonConverter.toJson(userGroupDto);

        ConversionObject conversionObject = this.createConversionObject("net.hawkserver.models", userGroupDtoAsString);
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "updateUserGroupDto", new ConversionObject[]{conversionObject});

        //Act
        boolean actualServiceResult = this.securityService.updateUserGroupDto(userGroupDtoAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void updateUserGroupDto_withoutPermission_false() {
        //Arrange
        UserGroupDto userGroupDto = new UserGroupDto();
        userGroupDto.setName("testName");
        this.mockedUserGroupService.addUserGroupDto(userGroupDto);

        userGroupDto.setName("changedName");

        String userGroupDtoAsString = this.jsonConverter.toJson(userGroupDto);

        ConversionObject conversionObject = this.createConversionObject("net.hawkserver.models", userGroupDtoAsString);
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "updateUserGroupDto", new ConversionObject[]{conversionObject});

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.updateUserGroupDto(userGroupDtoAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void unassignUserFromGroup_withPermission_true() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        User userToAdd = new User();
        userToAdd.setPassword("123");
        this.mockedUserService.add(userToAdd);

        String userToAddAsString = this.jsonConverter.toJson(userToAdd);
        String firstUserGoupAsString = this.jsonConverter.toJson(this.firstUserGroup);

        ConversionObject firstConversionObject = this.createConversionObject("net.hawkserver.models", userToAddAsString);
        ConversionObject secondConversionObject = this.createConversionObject("net.hawkserver.models", firstUserGoupAsString);
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "unassignUserFromGroup", new ConversionObject[]{firstConversionObject, secondConversionObject});

        //Act
        boolean actualServiceResult = this.securityService.unassignUserFromGroup(firstUserGoupAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void unassignUserFromGroup_withoutPermission_false() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        User userToAdd = new User();
        userToAdd.setPassword("123");
        this.mockedUserService.add(userToAdd);

        String userToAddAsString = this.jsonConverter.toJson(userToAdd);
        String firstUserGoupAsString = this.jsonConverter.toJson(this.firstUserGroup);

        ConversionObject firstConversionObject = this.createConversionObject("net.hawkserver.models", userToAddAsString);
        ConversionObject secondConversionObject = this.createConversionObject("net.hawkserver.models", firstUserGoupAsString);
        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "unassignUserFromGroup", new ConversionObject[]{firstConversionObject, secondConversionObject});

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.unassignUserFromGroup(firstUserGoupAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void addUserWithoutProvider_withPermission_true() {
        //Arrange
        User user = new User();

        String userAsString = this.jsonConverter.toJson(user);

        ConversionObject conversionObject = this.createConversionObject("net.hawkserver.models", userAsString);
        WsContractDto contract = this.createContractDto("UserService", "net.hawkengine.services", "addUserWithoutProvider", new ConversionObject[]{conversionObject});

        //Act
        boolean actualServiceResult = this.securityService.addUserWithoutProvider(userAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void addUserWithoutProvider_withoutPermission_false() {
        //Arrange
        User user = new User();

        String userAsString = this.jsonConverter.toJson(user);

        ConversionObject conversionObject = this.createConversionObject("net.hawkserver.models", userAsString);
        WsContractDto contract = this.createContractDto("UserService", "net.hawkengine.services", "addUserWithoutProvider", new ConversionObject[]{conversionObject});

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.add(userAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void getAllUserGroups_withPermissionForTwoObjects_twoEntities() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "getAllUserGroups", new ConversionObject[]{});

        //Act
        List actualServiceResult = this.securityService.getAllUserGroups(expectedUserGroups, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertEquals(expectedUserGroups, actualServiceResult);
    }

    @Test
    public void getAllUserGroups_withoutPermission_null() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        WsContractDto contract = this.createContractDto("UserGroupService", "net.hawkengine.services", "getAllUserGroups", new ConversionObject[]{});

        List<Permission> permissions = new ArrayList<>();

        //Act
        List actualServiceResult = this.securityService.getAllUserGroups(expectedUserGroups, contract.getClassName(), permissions);

        //Assert
        Assert.assertNull(actualServiceResult);
    }

    @Test
    public void assignPipelineToGroup_withPermission_true() {
        //Arrange
        this.createPipelineDefinitionsAndPipelineGroups();
        List<PipelineDefinition> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstPipelineDefinition);

        String pipeleineDefinitionAsString = this.jsonConverter.toJson(this.firstPipelineDefinition);
        String firstPipelineGroupAsString = this.jsonConverter.toJson(this.firstPipelineGroup);

        ConversionObject firstConversionObject = this.createConversionObject("net.hawkserver.models", pipeleineDefinitionAsString);
        ConversionObject secondConversionObject = this.createConversionObject("net.hawkserver.models", firstPipelineGroupAsString);
        WsContractDto contract = this.createContractDto("PipelineDefinitionService", "net.hawkengine.services", "assignPipelineToGroup", new ConversionObject[]{firstConversionObject, secondConversionObject});

        //Act
        boolean actualServiceResult = this.securityService.assignPipelineToGroup(firstPipelineGroupAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void assignPipelineToGroup_withoutPermission_false() {
        //Arrange
        this.createPipelineDefinitionsAndPipelineGroups();
        List<PipelineDefinition> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstPipelineDefinition);

        String pipeleineDefinitionAsString = this.jsonConverter.toJson(this.firstPipelineDefinition);
        String firstPipelineGroupAsString = this.jsonConverter.toJson(this.firstPipelineGroup);

        ConversionObject firstConversionObject = this.createConversionObject("net.hawkserver.models", pipeleineDefinitionAsString);
        ConversionObject secondConversionObject = this.createConversionObject("net.hawkserver.models", firstPipelineGroupAsString);
        WsContractDto contract = this.createContractDto("PipelineDefinitionService", "net.hawkengine.services", "assignPipelineToGroup", new ConversionObject[]{firstConversionObject, secondConversionObject});

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.assignPipelineToGroup(pipeleineDefinitionAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void unassignPipelineFromGroup_withPermission_true() {
        //Arrange
        this.createPipelineDefinitionsAndPipelineGroups();
        this.firstPipelineDefinition.setPipelineGroupId(this.firstPipelineGroup.getId());
        String pipeleineDefinitionAsString = this.jsonConverter.toJson(this.firstPipelineDefinition);

        ConversionObject conversionObject = this.createConversionObject("net.hawkserver.models", pipeleineDefinitionAsString);
        WsContractDto contract = this.createContractDto("PipelineDefinitionService", "net.hawkengine.services", "unassignPipelineFromGroup", new ConversionObject[]{conversionObject});

        //Act
        boolean actualServiceResult = this.securityService.unassignPipelineFromGroup(pipeleineDefinitionAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void unassignPipelineFromGroup_withoutPermission_false() {
        //Arrange
        this.createPipelineDefinitionsAndPipelineGroups();
        this.firstPipelineDefinition.setPipelineGroupId(this.firstPipelineGroup.getId());
        String pipeleineDefinitionAsString = this.jsonConverter.toJson(this.firstPipelineDefinition);

        ConversionObject conversionObject = this.createConversionObject("net.hawkserver.models", pipeleineDefinitionAsString);
        WsContractDto contract = this.createContractDto("PipelineDefinitionService", "net.hawkengine.services", "unassignPipelineFromGroup", new ConversionObject[]{conversionObject});

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.unassignPipelineFromGroup(pipeleineDefinitionAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void changeUserPassword_withPermission_true() {
        //Arrange
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("test");

        this.mockedUserService.add(user);

        UserDto userDto = new UserDto();
        userDto.setUsername("test@test.com");
        String userDtoAsString = this.jsonConverter.toJson(userDto);

        ConversionObject firstConversionObject = this.createConversionObject("net.hawkserver.models", userDtoAsString);
        ConversionObject secondConversionObject = this.createConversionObject("net.hawkserver.models", "changedPassword");
        ConversionObject thirdConversionObject = this.createConversionObject("net.hawkserver.models", "test");

        WsContractDto contract = this.createContractDto("UserService", "net.hawkengine.services", "changeUserPassword", new ConversionObject[]{firstConversionObject, secondConversionObject, thirdConversionObject});

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.changeUserPassword("test@test.com", userDtoAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void changeUserPassword_withoutPermission_false() {
        //Arrange
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("test");

        this.mockedUserService.add(user);

        UserDto userDto = new UserDto();
        userDto.setUsername("test@test.com");
        String userDtoAsString = this.jsonConverter.toJson(userDto);

        ConversionObject firstConversionObject = this.createConversionObject("net.hawkserver.models", userDtoAsString);
        ConversionObject secondConversionObject = this.createConversionObject("net.hawkserver.models", "changedPassword");
        ConversionObject thirdConversionObject = this.createConversionObject("net.hawkserver.models", "test");

        WsContractDto contract = this.createContractDto("UserService", "net.hawkengine.services", "changeUserPassword", new ConversionObject[]{firstConversionObject, secondConversionObject, thirdConversionObject});

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.changeUserPassword("invalid@test.com", userDtoAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void assignMaterialToPipeline_withPermission_true() {
        //Arrange
        this.createPipelineDefinitionsAndPipelineGroups();

        String pipeleineDefinitionAsString = this.jsonConverter.toJson(this.firstPipelineDefinition);

        MaterialDefinition expectedMaterialDefintion = new GitMaterial();
        expectedMaterialDefintion.setName("GitMaterial");

        ConversionObject firstConversionObject = this.createConversionObject("net.hawkserver.models", pipeleineDefinitionAsString);
        ConversionObject secondConversionObject = this.createConversionObject("net.hawkserver.models", expectedMaterialDefintion.getId());

        WsContractDto contract = this.createContractDto("PipelineDefinitionService", "net.hawkengine.services", "assignMaterialToPipeline", new ConversionObject[]{firstConversionObject, secondConversionObject});

        //Act
        boolean actualServiceResult = this.securityService.assignMaterialToPipeline(pipeleineDefinitionAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void assignMaterialToPipeline_withoutPermission_false() {
        //Arrange
        this.createPipelineDefinitionsAndPipelineGroups();

        String pipeleineDefinitionAsString = this.jsonConverter.toJson(this.firstPipelineDefinition);

        MaterialDefinition expectedMaterialDefintion = new GitMaterial();
        expectedMaterialDefintion.setName("GitMaterial");


        ConversionObject firstConversionObject = this.createConversionObject("net.hawkserver.models", pipeleineDefinitionAsString);
        ConversionObject secondConversionObject = this.createConversionObject("net.hawkserver.models", expectedMaterialDefintion.getId());

        WsContractDto contract = this.createContractDto("PipelineDefinitionService", "net.hawkengine.services", "assignMaterialToPipeline", new ConversionObject[]{firstConversionObject, secondConversionObject});

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.assignPipelineToGroup(pipeleineDefinitionAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void unassignMaterialFromPipeline_withPermission_true() {
        //Arrange
        this.createPipelineDefinitionsAndPipelineGroups();

        MaterialDefinition expectedMaterialDefintion = new GitMaterial();
        expectedMaterialDefintion.setName("GitMaterial");

        this.firstPipelineDefinition.getMaterialDefinitionIds().add(expectedMaterialDefintion.getId());
        this.mockedPipelineDefinitionService.update(this.firstPipelineDefinition);

        String pipeleineDefinitionAsString = this.jsonConverter.toJson(this.firstPipelineDefinition);

        ConversionObject firstConversionObject = this.createConversionObject("net.hawkserver.models", pipeleineDefinitionAsString);
        ConversionObject secondConversionObject = this.createConversionObject("net.hawkserver.models", expectedMaterialDefintion.getId());

        WsContractDto contract = this.createContractDto("PipelineDefinitionService", "net.hawkengine.services", "unassignMaterialToPipeline", new ConversionObject[]{firstConversionObject, secondConversionObject});

        //Act
        boolean actualServiceResult = this.securityService.unassignMaterialFromPipeline(pipeleineDefinitionAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void unassignMaterialFromPipeline_withoutPermission_false() {
        //Arrange
        this.createPipelineDefinitionsAndPipelineGroups();

        MaterialDefinition expectedMaterialDefintion = new GitMaterial();
        expectedMaterialDefintion.setName("GitMaterial");

        this.firstPipelineDefinition.getMaterialDefinitionIds().add(expectedMaterialDefintion.getId());
        this.mockedPipelineDefinitionService.update(this.firstPipelineDefinition);

        String pipeleineDefinitionAsString = this.jsonConverter.toJson(this.firstPipelineDefinition);

        ConversionObject firstConversionObject = this.createConversionObject("net.hawkserver.models", pipeleineDefinitionAsString);
        ConversionObject secondConversionObject = this.createConversionObject("net.hawkserver.models", expectedMaterialDefintion.getId());

        WsContractDto contract = this.createContractDto("PipelineDefinitionService", "net.hawkengine.services", "unassignMaterialToPipeline", new ConversionObject[]{firstConversionObject, secondConversionObject});

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.unassignPipelineFromGroup(pipeleineDefinitionAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void initialize_validConstructor_notNull() {
        //Act
        this.securityService = new SecurityService();

        //Assert
        Assert.assertNotNull(this.securityService);
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

    private void createPipelineDefinitionsAndPipelineGroups() {
        this.firstPipelineGroup = new PipelineGroup();
        this.secondPipelineGroup = new PipelineGroup();

        this.firstPipelineDefinition = new PipelineDefinition();
        this.firstPipelineDefinition.setPipelineGroupId(this.firstPipelineGroup.getId());

        this.secondPipelineDefinition = new PipelineDefinition();
        this.secondPipelineDefinition.setPipelineGroupId(this.firstPipelineGroup.getId());

        this.thirdPipelineDefinition = new PipelineDefinition();
        this.thirdPipelineDefinition.setPipelineGroupId(this.secondPipelineGroup.getId());

        this.fourthPipelineDefinition = new PipelineDefinition();
        this.fourthPipelineDefinition.setPipelineGroupId(this.secondPipelineGroup.getId());

        this.mockedPipelineGroupService.add(this.firstPipelineGroup);
        this.mockedPipelineGroupService.add(this.secondPipelineGroup);

        this.mockedPipelineDefinitionService.add(this.firstPipelineDefinition);
        this.mockedPipelineDefinitionService.add(this.secondPipelineDefinition);
        this.mockedPipelineDefinitionService.add(this.thirdPipelineDefinition);
        this.mockedPipelineDefinitionService.add(this.fourthPipelineDefinition);
    }

    private ConversionObject createConversionObject(String packageName, String object) {
        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName(packageName);
        conversionObject.setObject(object);

        return conversionObject;
    }

    private WsContractDto createContractDto(String className, String packageName, String methodName, ConversionObject[] conversionObjects) {

        WsContractDto contract = new WsContractDto();
        contract.setClassName(className);
        contract.setPackageName(packageName);
        contract.setMethodName(methodName);
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(conversionObjects);

        return contract;
    }
}
