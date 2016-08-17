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

        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("getAll");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User Groups retrieved successfully");
        expectedServiceResult.setObject(expectedUserGroups);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("getAll");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User Groups retrieved successfully");
        expectedServiceResult.setObject(expectedUserGroups);

        List<Permission> permissions = new ArrayList<>();

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

        WsContractDto contract = new WsContractDto();
        contract.setClassName("PipelineGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("getPipelineDTOs");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User Groups retrieved successfully");
        expectedServiceResult.setObject(expectedPipelineGroups);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

        WsContractDto contract = new WsContractDto();
        contract.setClassName("PipelineGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("getPipelineDTOs");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User Groups retrieved successfully");
        expectedServiceResult.setObject(expectedPipelineGroups);

        List<Permission> permissions = new ArrayList<>();

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName("java.lang.String");
        conversionObject.setObject(this.firstUserGroup.getId());

        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("getById");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{conversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User Group retrieved successfully");
        expectedServiceResult.setObject(expectedUserGroups);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName("java.lang.String");
        conversionObject.setObject(this.firstUserGroup.getId());


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("getById");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{conversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User Group retrieved successfully");
        expectedServiceResult.setObject(expectedUserGroups);

        List<Permission> permissions = new ArrayList<>();

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //Act
        boolean actualServiceResult = this.securityService.getById(this.firstUserGroup.getId(), contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void add_withPermission_true() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);
        UserGroup userGroupToAdd = new UserGroup();

        String userGroupToAddAsString = this.jsonConverter.toJson(userGroupToAdd);

        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName("net.hawkserver.models");
        conversionObject.setObject(userGroupToAddAsString);


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("add");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{conversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User Group updated successfully");
        expectedServiceResult.setObject(null);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //Act
        boolean actualServiceResult = this.securityService.add(userGroupToAddAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void add_withoutPermission_fasle() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        UserGroup userGroupToAdd = new UserGroup();

        String userGroupToAddAsString = this.jsonConverter.toJson(userGroupToAdd);

        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName("net.hawkserver.models");
        conversionObject.setObject(userGroupToAddAsString);


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("add");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{conversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(true);
        expectedServiceResult.setMessage("User Group cannot be added");
        expectedServiceResult.setObject(expectedUserGroups);

        List<Permission> permissions = new ArrayList<>();

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //Act
        boolean actualServiceResult = this.securityService.add(userGroupToAddAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void update_withPermission_true() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);
        this.firstUserGroup.setName("TestName");

        String firstUserGroupAsString = this.jsonConverter.toJson(this.firstUserGroup);

        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName("net.hawkserver.models");
        conversionObject.setObject(firstUserGroupAsString);


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("update");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{conversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User Group updated successfully");
        expectedServiceResult.setObject(null);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //Act
        boolean actualServiceResult = this.securityService.update(firstUserGroupAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void update_withoutPermission_false() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);
        this.firstUserGroup.setName("TestName");

        String firstUserGroupAsString = this.jsonConverter.toJson(this.firstUserGroup);

        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName("net.hawkserver.models");
        conversionObject.setObject(firstUserGroupAsString);


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("update");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{conversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(true);
        expectedServiceResult.setMessage("User Group cannot be deleted");
        expectedServiceResult.setObject(expectedUserGroups);

        List<Permission> permissions = new ArrayList<>();

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //Act
        boolean actualServiceResult = this.securityService.update(firstUserGroupAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void delete_withPermission_true() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName("java.lang.String");
        conversionObject.setObject(this.firstUserGroup.getId());


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("delete");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{conversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User Group deleted successfully");
        expectedServiceResult.setObject(null);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //Act
        boolean actualServiceResult = this.securityService.delete(this.firstUserGroup.getId(), contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void delete_withoutPermission_false() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName("java.lang.String");
        conversionObject.setObject(this.firstUserGroup.getId());


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("delete");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{conversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(true);
        expectedServiceResult.setMessage("User Group cannot be deleted");
        expectedServiceResult.setObject(expectedUserGroups);

        List<Permission> permissions = new ArrayList<>();

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //Act
        boolean actualServiceResult = this.securityService.delete(this.firstUserGroup.getId(), contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void addUserGroupDto_withPermission_true() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);
        UserGroupDto userGroupToAdd = new UserGroupDto();

        String userGroupToAddAsString = this.jsonConverter.toJson(userGroupToAdd);

        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName("net.hawkserver.models");
        conversionObject.setObject(userGroupToAddAsString);


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("addUserGroupDto");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{conversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User Group added successfully");
        expectedServiceResult.setObject(null);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //Act
        boolean actualServiceResult = this.securityService.addUserGroupDto(userGroupToAddAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void addUserGroupDto_withoutPermission_fasle() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        UserGroupDto userGroupToAdd = new UserGroupDto();

        String userGroupToAddAsString = this.jsonConverter.toJson(userGroupToAdd);

        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName("net.hawkserver.models");
        conversionObject.setObject(userGroupToAddAsString);


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("addUserGroupDto");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{conversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(true);
        expectedServiceResult.setMessage("User Group cannot be added");
        expectedServiceResult.setObject(expectedUserGroups);

        List<Permission> permissions = new ArrayList<>();

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

        ConversionObject firstConversionObject = new ConversionObject();
        firstConversionObject.setPackageName("net.hawkserver.models");
        firstConversionObject.setObject(userToAddAsString);

        ConversionObject secondConversionObject = new ConversionObject();
        secondConversionObject.setPackageName("net.hawkserver.models");
        secondConversionObject.setObject(firstUserGoupAsString);


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("assignUserToGroup");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{firstConversionObject, secondConversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User added successfully to group");
        expectedServiceResult.setObject(null);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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
        userToAdd.setPassword("1233");
        this.mockedUserService.add(userToAdd);

        String userToAddAsString = this.jsonConverter.toJson(userToAdd);
        String firstUserGoupAsString = this.jsonConverter.toJson(this.firstUserGroup);

        ConversionObject firstConversionObject = new ConversionObject();
        firstConversionObject.setPackageName("net.hawkserver.models");
        firstConversionObject.setObject(userToAddAsString);

        ConversionObject secondConversionObject = new ConversionObject();
        secondConversionObject.setPackageName("net.hawkserver.models");
        secondConversionObject.setObject(firstUserGoupAsString);


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("assignUserToGroup");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{firstConversionObject, secondConversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(true);
        expectedServiceResult.setMessage("User cannot be added to group");
        expectedServiceResult.setObject(null);

        List<Permission> permissions = new ArrayList<>();

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName("net.hawkserver.models");
        conversionObject.setObject(userGroupDtoAsString);

        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("updateUserGroupDto");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{conversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User Group Dto updated successfully");
        expectedServiceResult.setObject(null);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName("net.hawkserver.models");
        conversionObject.setObject(userGroupDtoAsString);

        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("update");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{conversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(true);
        expectedServiceResult.setMessage("User Group cannot be deleted");
        expectedServiceResult.setObject(userGroupDto);

        List<Permission> permissions = new ArrayList<>();

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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
        userToAdd.getUserGroupIds().add(firstUserGroup.getId());
        this.firstUserGroup.getUserIds().add(userToAdd.getId());
        this.mockedUserService.add(userToAdd);
        this.mockedUserGroupService.update(this.firstUserGroup);

        String userToAddAsString = this.jsonConverter.toJson(userToAdd);
        String firstUserGoupAsString = this.jsonConverter.toJson(this.firstUserGroup);

        ConversionObject firstConversionObject = new ConversionObject();
        firstConversionObject.setPackageName("net.hawkserver.models");
        firstConversionObject.setObject(userToAddAsString);

        ConversionObject secondConversionObject = new ConversionObject();
        secondConversionObject.setPackageName("net.hawkserver.models");
        secondConversionObject.setObject(firstUserGoupAsString);


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("unassignUserFromGroup");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{firstConversionObject, secondConversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User removed successfully to group");
        expectedServiceResult.setObject(null);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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
        userToAdd.getUserGroupIds().add(firstUserGroup.getId());
        this.firstUserGroup.getUserIds().add(userToAdd.getId());
        this.mockedUserService.add(userToAdd);
        this.mockedUserGroupService.update(this.firstUserGroup);

        String userToAddAsString = this.jsonConverter.toJson(userToAdd);
        String firstUserGoupAsString = this.jsonConverter.toJson(this.firstUserGroup);

        ConversionObject firstConversionObject = new ConversionObject();
        firstConversionObject.setPackageName("net.hawkserver.models");
        firstConversionObject.setObject(userToAddAsString);

        ConversionObject secondConversionObject = new ConversionObject();
        secondConversionObject.setPackageName("net.hawkserver.models");
        secondConversionObject.setObject(firstUserGoupAsString);


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("unassignUserFromGroup");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{firstConversionObject, secondConversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(true);
        expectedServiceResult.setMessage("User cannot be removed to group");
        expectedServiceResult.setObject(null);

        List<Permission> permissions = new ArrayList<>();

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName("net.hawkserver.models");
        conversionObject.setObject(userAsString);


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("addUserWithoutProvider");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{conversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User added successfully");
        expectedServiceResult.setObject(null);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //Act
        boolean actualServiceResult = this.securityService.addUserWithoutProvider(userAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void addUserWithoutProvider_withoutPermission_fasle() {
        //Arrange
        User user = new User();

        String userAsString = this.jsonConverter.toJson(user);

        ConversionObject conversionObject = new ConversionObject();
        conversionObject.setPackageName("net.hawkserver.models");
        conversionObject.setObject(userAsString);


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("addUserWithoutProvider");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{conversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(true);
        expectedServiceResult.setMessage("User cannot be added");
        expectedServiceResult.setObject(user);

        List<Permission> permissions = new ArrayList<>();

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("getAllUserGroups");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User Groups retrieved successfully");
        expectedServiceResult.setObject(expectedUserGroups);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("getAllUserGroups");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("User Groups retrieved successfully");
        expectedServiceResult.setObject(expectedUserGroups);

        List<Permission> permissions = new ArrayList<>();

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

        ConversionObject firstConversionObject = new ConversionObject();
        firstConversionObject.setPackageName("net.hawkserver.models");
        firstConversionObject.setObject(pipeleineDefinitionAsString);

        ConversionObject secondConversionObject = new ConversionObject();
        secondConversionObject.setPackageName("net.hawkserver.models");
        secondConversionObject.setObject(firstPipelineGroupAsString);


        WsContractDto contract = new WsContractDto();
        contract.setClassName("PipelineDefinitionService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("assignPipelineToGroup");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{firstConversionObject, secondConversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("Pipeline assigned successfully to group");
        expectedServiceResult.setObject(null);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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

        ConversionObject firstConversionObject = new ConversionObject();
        firstConversionObject.setPackageName("net.hawkserver.models");
        firstConversionObject.setObject(pipeleineDefinitionAsString);

        ConversionObject secondConversionObject = new ConversionObject();
        secondConversionObject.setPackageName("net.hawkserver.models");
        secondConversionObject.setObject(firstPipelineGroupAsString);


        WsContractDto contract = new WsContractDto();
        contract.setClassName("PipelineDefinitionService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("assignPipelineToGroup");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{firstConversionObject, secondConversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("Pipeline assigned successfully to group");
        expectedServiceResult.setObject(null);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean actualServiceResult = this.securityService.assignPipelineToGroup(pipeleineDefinitionAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void unAssignPipelineFromGroup_withPermission_true() {
        //Arrange
        this.createPipelineDefinitionsAndPipelineGroups();
        this.firstPipelineDefinition.setPipelineGroupId(this.firstPipelineGroup.getId());
        String pipeleineDefinitionAsString = this.jsonConverter.toJson(this.firstPipelineDefinition);


        ConversionObject firstConversionObject = new ConversionObject();
        firstConversionObject.setPackageName("net.hawkserver.models");
        firstConversionObject.setObject(pipeleineDefinitionAsString);

        WsContractDto contract = new WsContractDto();
        contract.setClassName("PipelineDefinitionService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("unAssignPipelineFromGroup");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{firstConversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("Pipeline unassigned successfully from group");
        expectedServiceResult.setObject(null);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //Act
        boolean actualServiceResult = this.securityService.unassignPipelineFromGroup(pipeleineDefinitionAsString, contract.getClassName(), this.createPermissions());

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void unAssignPipelineFromGroup_withoutPermission_false() {
        //Arrange
        this.createPipelineDefinitionsAndPipelineGroups();
        this.firstPipelineDefinition.setPipelineGroupId(this.firstPipelineGroup.getId());
        String pipeleineDefinitionAsString = this.jsonConverter.toJson(this.firstPipelineDefinition);


        ConversionObject firstConversionObject = new ConversionObject();
        firstConversionObject.setPackageName("net.hawkserver.models");
        firstConversionObject.setObject(pipeleineDefinitionAsString);

        WsContractDto contract = new WsContractDto();
        contract.setClassName("PipelineDefinitionService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("unAssignPipelineFromGroup");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{firstConversionObject});

        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setMessage("Pipeline unassigned successfully from group");
        expectedServiceResult.setObject(null);

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
        String userAsString = this.jsonConverter.toJson(user);

        this.mockedUserService.add(user);

        ConversionObject firstConversionObject = new ConversionObject();
        firstConversionObject.setObject(userAsString);
        firstConversionObject.setPackageName("net.hawkserver.models");

        ConversionObject secondConversionObject = new ConversionObject();
        secondConversionObject.setPackageName("java.lang.String");
        secondConversionObject.setObject("changedPassword");

        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("changeUserPassword");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{firstConversionObject, secondConversionObject});

        ServiceResult expectedServiceResult = this.mockedUserService.getByEmail("test@test.com");

        List<Permission> permissions = new ArrayList<>();

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //Act
        boolean actualServiceResult = this.securityService.changeUserPassword("test@test.com", userAsString, contract.getClassName(), permissions);

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void changeUserPassword_withoutPermission_false() {
        //Arrange
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("test");
        String userAsString = this.jsonConverter.toJson(user);

        this.mockedUserService.add(user);

        ConversionObject firstConversionObject = new ConversionObject();
        firstConversionObject.setObject(userAsString);
        firstConversionObject.setPackageName("net.hawkserver.models");

        ConversionObject secondConversionObject = new ConversionObject();
        secondConversionObject.setPackageName("java.lang.String");
        secondConversionObject.setObject("changedPassword");

        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("changeUserPassword");
        contract.setResult("");
        contract.setError(false);
        contract.setErrorMessage("");
        contract.setArgs(new ConversionObject[]{firstConversionObject, secondConversionObject});

        ServiceResult expectedServiceResult = this.mockedUserService.getByEmail("test@test.com");

        List<Permission> permissions = new ArrayList<>();

        try {
            Mockito.when(this.mockedWsObjectProcessor.call(contract)).thenReturn(expectedServiceResult);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //Act
        boolean actualServiceResult = this.securityService.changeUserPassword("invalid@test.com", userAsString, contract.getClassName(), permissions);

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
}
