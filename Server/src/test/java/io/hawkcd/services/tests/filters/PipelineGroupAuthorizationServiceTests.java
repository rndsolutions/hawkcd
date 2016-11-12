package io.hawkcd.services.tests.filters;

import com.fiftyonred.mock_jedis.MockJedisPool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.hawkcd.Config;
import io.hawkcd.utilities.constants.TestsConstants;
import io.hawkcd.utilities.deserializers.MaterialDefinitionAdapter;
import io.hawkcd.utilities.deserializers.TaskDefinitionAdapter;
import io.hawkcd.utilities.deserializers.WsContractDeserializer;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.db.redis.RedisRepository;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.PipelineGroup;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.payload.Permission;
import io.hawkcd.services.PipelineGroupService;
import io.hawkcd.services.filters.PipelineGroupAuthorizationService;
import io.hawkcd.services.filters.interfaces.IAuthorizationService;
import io.hawkcd.services.interfaces.IPipelineGroupService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineGroupAuthorizationServiceTests {
    private PipelineGroup firstPipelineGroup;
    private PipelineGroup secondPipelineGroup;
    private PipelineGroup thirdPipelineGroup;

    private IAuthorizationService authorizationService;
    private IDbRepository<PipelineGroup> mockedRepository;
    private IPipelineGroupService mockedPipelineGroupService;

    private Gson jsonConverter;

    @BeforeClass
    public static void setUpClass() {
        Config.configure();
    }

    @Before
    public void setUp() {
        this.firstPipelineGroup = new PipelineGroup();
        this.firstPipelineGroup.setName("firstGroup");
        this.secondPipelineGroup = new PipelineGroup();
        this.secondPipelineGroup.setName("secondPipelineGroup");
        this.thirdPipelineGroup = new PipelineGroup();
        this.thirdPipelineGroup.setName("thirdPipelineGroup");

        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();

        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelineGroupAuthorizationService");
        this.mockedRepository = new RedisRepository(PipelineGroup.class, mockedPool);
        this.mockedPipelineGroupService = new PipelineGroupService(this.mockedRepository);

        this.mockedPipelineGroupService.add(this.firstPipelineGroup);
        this.mockedPipelineGroupService.add(this.secondPipelineGroup);
        this.mockedPipelineGroupService.add(this.thirdPipelineGroup);

        this.authorizationService = new PipelineGroupAuthorizationService(this.mockedPipelineGroupService);
    }

    @Test
    public void getAll_withPermissionsForTwoEntities_twoEntities() {
        //Arrange
        List<Permission> permissions = this.createPermissions();

        List<PipelineGroup> entitiesToFilter = new ArrayList<>();
        entitiesToFilter.add(this.firstPipelineGroup);
        entitiesToFilter.add(this.secondPipelineGroup);
        entitiesToFilter.add(this.thirdPipelineGroup);

        //Act
        List<PipelineGroup> actualResult = this.authorizationService.getAll(permissions, entitiesToFilter);

        //Assert
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_TWO_OBJECTS, actualResult.size());
    }

    @Test
    public void getById_withPermissionToGet_true() {
        //Arrange
        List<Permission> permissions = this.createPermissions();

        //Act
        boolean hasPermission = this.authorizationService.getById(this.secondPipelineGroup.getId(), permissions);

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void getById_withoutPermissionToGet_false() {
        //Arrange
        List<Permission> permissions = this.createPermissions();

        //Act
        boolean hasPermission = this.authorizationService.getById(this.firstPipelineGroup.getId(), permissions);

        //Assert
        Assert.assertFalse(hasPermission);
    }

    @Test
    public void add_withPersmissionToAdd_true() {
        //Arrange
        PipelineGroup pipelineGroup = new PipelineGroup();

        List<Permission> permissions = new ArrayList<>();
        Permission pipelineGroupAdminPermission = new Permission();
        pipelineGroupAdminPermission.setPermittedEntityId(PermissionScope.PIPELINE_GROUP.toString());
        pipelineGroupAdminPermission.setPermissionType(PermissionType.ADMIN);
        pipelineGroupAdminPermission.setPermissionScope(PermissionScope.PIPELINE_GROUP);

        permissions.add(pipelineGroupAdminPermission);

        String entityToAdd = this.jsonConverter.toJson(pipelineGroup);

        //Act
        boolean hasPermission = this.authorizationService.add(entityToAdd, permissions);

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void add_withoutPermissionToAdd_false() {
        //Arrange
        PipelineGroup pipelineGroup = new PipelineGroup();

        List<Permission> permissions = this.createPermissions();
        String entityToAdd = this.jsonConverter.toJson(pipelineGroup);

        //Act
        boolean hasPermission = this.authorizationService.add(entityToAdd, permissions);

        //Assert
        Assert.assertFalse(hasPermission);
    }

    @Test
    public void update_withPermissionToUpdate_true() {
        //Arrange
        List<Permission> permissions = this.createPermissions();
        String entityToUpdate = this.jsonConverter.toJson(this.secondPipelineGroup);

        //Act
        boolean hasPermission = this.authorizationService.update(entityToUpdate, permissions);

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void update_withoutPermissionToUpdate_false() {
        //Arrange
        List<Permission> permissions = this.createPermissions();
        String entityToUpdate = this.jsonConverter.toJson(this.thirdPipelineGroup);

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
        boolean hasPermission = this.authorizationService.delete(this.secondPipelineGroup.getId(), permissions);

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void delete_withoutPermissionToDelete_false() {
        //Arrange
        List<Permission> permissions = this.createPermissions();

        //Act
        boolean hasPermission = this.authorizationService.delete(this.firstPipelineGroup.getId(), permissions);

        //Assert
        Assert.assertFalse(hasPermission);
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

        permissions.add(adminServerPermission);
        permissions.add(pipelineGroupPermission);
        permissions.add(firstPipelineGroupPermission);
        permissions.add(secondPipelineGroupPermission);

        List<Permission> orderedPermissions = permissions.stream()
                .sorted((p1, p2) -> p1.getPermissionScope().compareTo(p2.getPermissionScope())).collect(Collectors.toList());

        return orderedPermissions;
    }
}
