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

        this.securityService = new SecurityService(this.mockedWsObjectProcessor, this.mockedPipelineDefinitionService, this.mockedUserGroupService);
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
        ServiceResult actualServiceResult = this.securityService.getAll(contract, this.createPermissions());

        //Assert
        Assert.assertEquals(expectedServiceResult, actualServiceResult);
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
        ServiceResult actualServiceResult = this.securityService.getAll(contract, permissions);

        //Assert
        Assert.assertNull(actualServiceResult.getObject());
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
        ServiceResult actualServiceResult = this.securityService.getPipelineDTOs(contract, this.createPermissions());

        //Assert
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_TWO_OBJECTS, ((List<PipelineGroup>) actualServiceResult.getObject()).size());

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
        ServiceResult actualServiceResult = this.securityService.getPipelineDTOs(contract, permissions);

        //Assert
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS, ((List<PipelineGroup>)actualServiceResult.getObject()).size());

    }

    @Test
    public void getById_withPermission_oneEntity() {
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
        ServiceResult actualServiceResult = this.securityService.getById(contract, this.createPermissions());

        //Assert
        Assert.assertEquals(this.firstUserGroup.getId(), ((List<UserGroup>) actualServiceResult.getObject()).get(0).getId());
    }

    @Test
    public void getById_withoutPermission_null() {
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
        ServiceResult actualServiceResult = this.securityService.getById(contract, permissions);

        //Assert
        Assert.assertNull(actualServiceResult.getObject());
    }



    @Test
    public void add_withoutPermission_hasError() {
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
        ServiceResult actualServiceResult = this.securityService.add(contract, permissions);

        //Assert
        Assert.assertTrue(actualServiceResult.hasError());
    }

    @Test
    public void add_withPermission_noError() {
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
        ServiceResult actualServiceResult = this.securityService.add(contract, this.createPermissions());

        //Assert
        Assert.assertFalse(actualServiceResult.hasError());
    }

    @Test
    public void update_withPermission_noError() {
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
        ServiceResult actualServiceResult = this.securityService.update(contract, this.createPermissions());

        //Assert
        Assert.assertFalse(actualServiceResult.hasError());
    }

    @Test
    public void update_withoutPermission_hasError() {
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
        ServiceResult actualServiceResult = this.securityService.update(contract, permissions);

        //Assert
        Assert.assertTrue(actualServiceResult.hasError());
    }

    @Test
    public void delete_withPermission_noError() {
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
        ServiceResult actualServiceResult = this.securityService.delete(contract, this.createPermissions());

        //Assert
        Assert.assertFalse(actualServiceResult.hasError());
    }

    @Test
    public void delete_withoutPermission_hasError() {
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
        ServiceResult actualServiceResult = this.securityService.delete(contract, permissions);

        //Assert
        Assert.assertTrue(actualServiceResult.hasError());
    }

    @Test
    public void addUserToGroup_withPermission_noError() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        User userToAdd = new User();
        this.mockedUserService.add(userToAdd);

        ConversionObject firstConversionObject = new ConversionObject();
        firstConversionObject.setPackageName("java.lang.String");
        firstConversionObject.setObject(this.firstUserGroup.getId());

        ConversionObject secondConversionObject = new ConversionObject();
        secondConversionObject.setPackageName("java.lang.String");
        secondConversionObject.setObject(userToAdd.getId());


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
        ServiceResult actualServiceResult = this.securityService.assignUserToGroup(contract, this.createPermissions());

        //Assert
        Assert.assertFalse(actualServiceResult.hasError());
    }

    @Test
    public void addUserToGroup_withoutPermission_hasError() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        User userToAdd = new User();
        this.mockedUserService.add(userToAdd);

        ConversionObject firstConversionObject = new ConversionObject();
        firstConversionObject.setPackageName("java.lang.String");
        firstConversionObject.setObject(this.firstUserGroup.getId());

        ConversionObject secondConversionObject = new ConversionObject();
        secondConversionObject.setPackageName("java.lang.String");
        secondConversionObject.setObject(userToAdd.getId());


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
        ServiceResult actualServiceResult = this.securityService.assignUserToGroup(contract, permissions);

        //Assert
        Assert.assertTrue(actualServiceResult.hasError());
    }

    @Test
    public void removeUserToGroup_withPermission_noError() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        User userToAdd = new User();
        userToAdd.getUserGroupIds().add(firstUserGroup.getId());
        this.firstUserGroup.getUserIds().add(userToAdd.getId());
        this.mockedUserService.add(userToAdd);
        this.mockedUserGroupService.update(this.firstUserGroup);

        ConversionObject firstConversionObject = new ConversionObject();
        firstConversionObject.setPackageName("java.lang.String");
        firstConversionObject.setObject(this.firstUserGroup.getId());

        ConversionObject secondConversionObject = new ConversionObject();
        secondConversionObject.setPackageName("java.lang.String");
        secondConversionObject.setObject(userToAdd.getId());


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("removeUserToGroup");
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
        ServiceResult actualServiceResult = this.securityService.unassignUserFromGroup(contract, this.createPermissions());

        //Assert
        Assert.assertFalse(actualServiceResult.hasError());
    }

    @Test
    public void removeUserToGroup_withoutPermission_hasError() {
        //Arrange
        List<UserGroup> expectedUserGroups = new ArrayList<>();
        expectedUserGroups.add(this.firstUserGroup);
        expectedUserGroups.add(this.secondUserGroup);

        User userToAdd = new User();
        userToAdd.getUserGroupIds().add(firstUserGroup.getId());
        this.firstUserGroup.getUserIds().add(userToAdd.getId());
        this.mockedUserService.add(userToAdd);
        this.mockedUserGroupService.update(this.firstUserGroup);

        ConversionObject firstConversionObject = new ConversionObject();
        firstConversionObject.setPackageName("java.lang.String");
        firstConversionObject.setObject(this.firstUserGroup.getId());

        ConversionObject secondConversionObject = new ConversionObject();
        secondConversionObject.setPackageName("java.lang.String");
        secondConversionObject.setObject(userToAdd.getId());


        WsContractDto contract = new WsContractDto();
        contract.setClassName("UserGroupService");
        contract.setPackageName("net.hawkengine.services");
        contract.setMethodName("removeUserToGroup");
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
        ServiceResult actualServiceResult = this.securityService.unassignUserFromGroup(contract, permissions);

        //Assert
        Assert.assertTrue(actualServiceResult.hasError());
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
        ServiceResult actualServiceResult = this.securityService.getAllUserGroups(contract, this.createPermissions());

        //Assert
        Assert.assertEquals(expectedServiceResult, actualServiceResult);
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
        ServiceResult actualServiceResult = this.securityService.getAllUserGroups(contract, permissions);

        //Assert
        Assert.assertNull(actualServiceResult.getObject());
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
