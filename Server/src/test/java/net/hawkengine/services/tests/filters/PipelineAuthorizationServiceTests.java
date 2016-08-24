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
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.filters.EntityPermissionTypeService;
import net.hawkengine.services.filters.PipelineAuthorizationService;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineAuthorizationServiceTests {
    private PipelineDefinition firstPipelineDefinition;
    private PipelineDefinition secondPipelineDefinition;
    private PipelineDefinition thirdPipelineDefinition;
    private PipelineDefinition fourthPipelineDefinition;
    private PipelineDefinition fifthPilineDefinition;
    private PipelineDefinition sixthPipelineDefinition;

    private Pipeline firstPipeline;
    private Pipeline secondPipeline;
    private Pipeline thirdPipeline;
    private Pipeline fourthPipeline;
    private Pipeline fifthPipeline;
    private Pipeline sixthPipeline;

    private PipelineGroup firstPipelineGroup;
    private PipelineGroup secondPipelineGroup;
    private PipelineGroup thirdPipelineGroup;

    private IAuthorizationService authorizationService;

    private Gson jsonConverter;

    private IDbRepository<PipelineDefinition> mockedPipelineDefinitionRepository;
    private IPipelineDefinitionService mockedPipeLineDefinitionService;

    private IDbRepository<Pipeline> mockedPipelineRepository;
    private IPipelineService mockedPipelineService;
    private EntityPermissionTypeService mockedEntityPermissionTypeService;

    @BeforeClass
    public static void setUpClass() {
        ServerConfiguration.configure();
    }

    @Before
    public void setUp() {
        this.firstPipelineGroup = new PipelineGroup();
        this.secondPipelineGroup = new PipelineGroup();
        this.thirdPipelineGroup = new PipelineGroup();

        this.firstPipelineDefinition = new PipelineDefinition();
        this.firstPipelineDefinition.setPipelineGroupId(this.firstPipelineGroup.getId());

        this.secondPipelineDefinition = new PipelineDefinition();
        this.secondPipelineDefinition.setPipelineGroupId(this.firstPipelineGroup.getId());

        this.thirdPipelineDefinition = new PipelineDefinition();
        this.thirdPipelineDefinition.setPipelineGroupId(this.secondPipelineGroup.getId());

        this.fourthPipelineDefinition = new PipelineDefinition();
        this.fourthPipelineDefinition.setPipelineGroupId(this.secondPipelineGroup.getId());

        this.fifthPilineDefinition = new PipelineDefinition();
        this.fifthPilineDefinition.setPipelineGroupId(this.thirdPipelineGroup.getId());

        this.sixthPipelineDefinition = new PipelineDefinition();
        this.sixthPipelineDefinition.setPipelineGroupId(this.thirdPipelineGroup.getId());

        this.firstPipeline = new Pipeline();
        this.firstPipeline.setPipelineDefinitionId(this.firstPipelineDefinition.getId());

        this.secondPipeline = new Pipeline();
        this.secondPipeline.setPipelineDefinitionId(this.secondPipelineDefinition.getId());

        this.thirdPipeline = new Pipeline();
        this.thirdPipeline.setPipelineDefinitionId(this.thirdPipelineDefinition.getId());

        this.fourthPipeline = new Pipeline();
        this.fourthPipeline.setPipelineDefinitionId(this.fourthPipelineDefinition.getId());

        this.fifthPipeline = new Pipeline();
        this.fifthPipeline.setPipelineDefinitionId(this.fifthPilineDefinition.getId());

        this.sixthPipeline = new Pipeline();
        this.sixthPipeline.setPipelineDefinitionId(this.sixthPipelineDefinition.getId());

        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();

        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelineAuthorizationService");
        this.mockedPipelineDefinitionRepository = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.mockedPipeLineDefinitionService = new PipelineDefinitionService(this.mockedPipelineDefinitionRepository);
        this.mockedPipelineRepository = new RedisRepository(Pipeline.class, mockedPool);
        this.mockedPipelineService = Mockito.mock(PipelineService.class);

        this.mockedPipeLineDefinitionService.add(this.firstPipelineDefinition);
        this.mockedPipeLineDefinitionService.add(this.secondPipelineDefinition);
        this.mockedPipeLineDefinitionService.add(this.thirdPipelineDefinition);
        this.mockedPipeLineDefinitionService.add(this.fourthPipelineDefinition);
        this.mockedPipeLineDefinitionService.add(this.fifthPilineDefinition);
        this.mockedPipeLineDefinitionService.add(this.sixthPipelineDefinition);

        this.mockedPipelineService.add(this.firstPipeline);
        this.mockedPipelineService.add(this.secondPipeline);
        this.mockedPipelineService.add(this.thirdPipeline);
        this.mockedPipelineService.add(this.fourthPipeline);
        this.mockedPipelineService.add(this.fifthPipeline);
        this.mockedPipelineService.add(this.sixthPipeline);

        this.authorizationService = new PipelineAuthorizationService(this.mockedPipelineService, this.mockedPipeLineDefinitionService);
        this.mockedEntityPermissionTypeService = new EntityPermissionTypeService(this.mockedPipeLineDefinitionService);
    }

    @Test
    public void getAll_withPermissionsForFourEntities_fourEntities() {
        //Arrange
        List<Permission> permissions = this.createPermissions();

        List<Pipeline> entitiesToFilter = new ArrayList<>();
        entitiesToFilter.add(this.firstPipeline);
        entitiesToFilter.add(this.secondPipeline);
        entitiesToFilter.add(this.thirdPipeline);
        entitiesToFilter.add(this.firstPipeline);
        entitiesToFilter.add(this.fifthPipeline);
        entitiesToFilter.add(this.sixthPipeline);

        //Act
        List<PipelineDefinition> actualResult = this.authorizationService.getAll(permissions, entitiesToFilter);

        //Assert
        Assert.assertEquals(TestsConstants.TESTS_OOLLECTION_SIZE_FOUR_OBJECTS, actualResult.size());
    }

    @Test
    public void getById_withPermissionToGet_true() {
        //Arrange
        List<Permission> permissions = this.createPermissions();

        //Act
        ServiceResult getByIdPipelineServiceResult = new ServiceResult();
        getByIdPipelineServiceResult.setError(false);
        getByIdPipelineServiceResult.setMessage("Pipeline retrieved successfully");
        getByIdPipelineServiceResult.setObject(this.firstPipeline);

        Mockito.when(this.mockedPipelineService.getById(this.firstPipeline.getId())).thenReturn(getByIdPipelineServiceResult);
        boolean hasPermission = this.authorizationService.getById(this.firstPipeline.getId(), permissions);

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void getById_withoutPermissionToGet_false() {
        //Arrange
        List<Permission> permissions = this.createPermissions();

        //Act
        ServiceResult getByIdPipelineServiceResult = new ServiceResult();
        getByIdPipelineServiceResult.setError(false);
        getByIdPipelineServiceResult.setMessage("Pipeline retrieved successfully");
        getByIdPipelineServiceResult.setObject(this.secondPipeline);

        Mockito.when(this.mockedPipelineService.getById(this.secondPipeline.getId())).thenReturn(getByIdPipelineServiceResult);
        boolean hasPermission = this.authorizationService.getById(this.secondPipeline.getId(), permissions);

        //Assert
        Assert.assertFalse(hasPermission);
    }

    @Test
    public void add_withPersmissionToAdd_true() {
        //Arrange
        Pipeline pipeline = new Pipeline();
        pipeline.setPipelineDefinitionId(this.firstPipelineDefinition.getId());

        List<Permission> permissions = this.createPermissions();
        String entityToAdd = this.jsonConverter.toJson(pipeline);

        //Act
        boolean hasPermission = this.authorizationService.add(entityToAdd, permissions);

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void add_withoutPermissionToAdd_false() {
        //Arrange
        Pipeline pipeline = new Pipeline();
        pipeline.setPipelineDefinitionId(this.secondPipelineDefinition.getId());

        List<Permission> permissions = this.createPermissions();
        String entityToAdd = this.jsonConverter.toJson(pipeline);

        //Act
        boolean hasPermission = this.authorizationService.add(entityToAdd, permissions);

        //Assert
        Assert.assertFalse(hasPermission);
    }

    @Test
    public void update_withPermissionToUpdate_true() {
        //Arrange
        List<Permission> permissions = this.createPermissions();
        String entityToUpdate = this.jsonConverter.toJson(this.fourthPipeline);

        //Act
        boolean hasPermission = this.authorizationService.update(entityToUpdate, permissions);

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void update_withoutPermissionToUpdate_false() {
        //Arrange
        List<Permission> permissions = this.createPermissions();
        String entityToUpdate = this.jsonConverter.toJson(this.thirdPipeline);

        //Act
        boolean hasPermission = this.authorizationService.update(entityToUpdate, permissions);

        //Assert
        Assert.assertFalse(hasPermission);
    }

    @Test
    public void delete_withPermissionToDelete_true() {
        //Arrange
        List<Permission> permissions = this.createPermissions();

        //Act
        ServiceResult getByIdPipelineServiceResult = new ServiceResult();
        getByIdPipelineServiceResult.setError(false);
        getByIdPipelineServiceResult.setMessage("Pipeline retrieved successfully");
        getByIdPipelineServiceResult.setObject(this.fifthPipeline);

        Mockito.when(this.mockedPipelineService.getById(this.fifthPipeline.getId())).thenReturn(getByIdPipelineServiceResult);
        boolean hasPermission = this.authorizationService.delete(this.fifthPipeline.getId(), permissions);

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void delete_withoutPermissionToDelete_false() {
        //Arrange
        List<Permission> permissions = this.createPermissions();

        //Act
        ServiceResult getByIdPipelineServiceResult = new ServiceResult();
        getByIdPipelineServiceResult.setError(false);
        getByIdPipelineServiceResult.setMessage("Pipeline retrieved successfully");
        getByIdPipelineServiceResult.setObject(this.thirdPipeline);

        Mockito.when(this.mockedPipelineService.getById(this.thirdPipeline.getId())).thenReturn(getByIdPipelineServiceResult);
        boolean hasPermission = this.authorizationService.delete(this.thirdPipeline.getId(), permissions);

        //Assert
        Assert.assertFalse(hasPermission);
    }

    @Test
    public void initialize_validConstructor_notNull() {
        //Act
        this.authorizationService = new PipelineAuthorizationService();

        //Assert
        Assert.assertNotNull(this.authorizationService);
    }

    private List<Permission> createPermissions() {
        List<Permission> permissions = new ArrayList<>();

        Permission adminServerPermission = new Permission();
        adminServerPermission.setPermissionType(PermissionType.ADMIN);
        adminServerPermission.setPermissionScope(PermissionScope.SERVER);
        adminServerPermission.setPermittedEntityId(PermissionScope.SERVER.toString());

        Permission pipelineGroupPermission = new Permission();
        pipelineGroupPermission.setPermissionType(PermissionType.VIEWER);
        pipelineGroupPermission.setPermissionScope(PermissionScope.PIPELINE_GROUP);
        pipelineGroupPermission.setPermittedEntityId(PermissionScope.PIPELINE_GROUP.toString());

        Permission firstPipelineGroupPermission = new Permission();
        firstPipelineGroupPermission.setPermissionType(PermissionType.NONE);
        firstPipelineGroupPermission.setPermissionScope(PermissionScope.PIPELINE_GROUP);
        firstPipelineGroupPermission.setPermittedEntityId(this.firstPipelineGroup.getId());

        Permission secondPipelineGroupPermission = new Permission();
        secondPipelineGroupPermission.setPermissionType(PermissionType.ADMIN);
        secondPipelineGroupPermission.setPermissionScope(PermissionScope.PIPELINE_GROUP);
        secondPipelineGroupPermission.setPermittedEntityId(this.secondPipelineGroup.getId());

        Permission firstPipelineDefinitionPermission = new Permission();
        firstPipelineDefinitionPermission.setPermissionType(PermissionType.OPERATOR);
        firstPipelineDefinitionPermission.setPermissionScope(PermissionScope.PIPELINE);
        firstPipelineDefinitionPermission.setPermittedEntityId(this.firstPipelineDefinition.getId());

        Permission secondPipelineDefinitionPermission = new Permission();
        secondPipelineDefinitionPermission.setPermissionType(PermissionType.NONE);
        secondPipelineDefinitionPermission.setPermissionScope(PermissionScope.PIPELINE);
        secondPipelineDefinitionPermission.setPermittedEntityId(this.thirdPipelineDefinition.getId());

        Permission thirdPipelineDefinitionPermission = new Permission();
        thirdPipelineDefinitionPermission.setPermissionType(PermissionType.ADMIN);
        thirdPipelineDefinitionPermission.setPermissionScope(PermissionScope.PIPELINE);
        thirdPipelineDefinitionPermission.setPermittedEntityId(this.fifthPilineDefinition.getId());

        permissions.add(adminServerPermission);
        permissions.add(pipelineGroupPermission);
        permissions.add(firstPipelineGroupPermission);
        permissions.add(secondPipelineGroupPermission);
        permissions.add(firstPipelineDefinitionPermission);
        permissions.add(secondPipelineDefinitionPermission);
        permissions.add(thirdPipelineDefinitionPermission);

        List<Permission> orderedPermissions = permissions.stream()
                .sorted((p1, p2) -> p1.getPermissionScope().compareTo(p2.getPermissionScope())).collect(Collectors.toList());

        return orderedPermissions;
    }
}
