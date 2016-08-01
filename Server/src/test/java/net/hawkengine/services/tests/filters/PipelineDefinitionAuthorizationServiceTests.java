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
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.filters.PipelineDefinitionAuthorizationService;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineDefinitionAuthorizationServiceTests {
    private PipelineDefinition firstPipeline;
    private PipelineDefinition secondPipeline;
    private PipelineDefinition thirdPipeline;
    private PipelineDefinition fourthPipeline;
    private PipelineGroup firstPipelineGroup;
    private PipelineGroup secondPipelineGroup;

    private IAuthorizationService authorizationService;

    private Gson jsonConverter;

    private IDbRepository<PipelineDefinition> mockedRepository;
    private IPipelineDefinitionService mockedPipeLineDefinitionService;

    @Before
    public void setUp() {
        this.firstPipelineGroup = new PipelineGroup();
        this.secondPipelineGroup = new PipelineGroup();

        this.firstPipeline = new PipelineDefinition();
        this.firstPipeline.setPipelineGroupId(this.firstPipelineGroup.getId());

        this.secondPipeline = new PipelineDefinition();
        this.secondPipeline.setPipelineGroupId(this.firstPipelineGroup.getId());

        this.thirdPipeline = new PipelineDefinition();
        this.thirdPipeline.setPipelineGroupId(this.secondPipelineGroup.getId());

        this.fourthPipeline = new PipelineDefinition();
        this.fourthPipeline.setPipelineGroupId(this.secondPipelineGroup.getId());

        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();

        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testPipelineDefinitionService");
        ServerConfiguration.configure();
        this.mockedRepository = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.mockedPipeLineDefinitionService = new PipelineDefinitionService(this.mockedRepository);

        this.mockedPipeLineDefinitionService.add(this.firstPipeline);
        this.mockedPipeLineDefinitionService.add(this.secondPipeline);
        this.mockedPipeLineDefinitionService.add(this.thirdPipeline);
        this.mockedPipeLineDefinitionService.add(this.fourthPipeline);

        this.authorizationService = new PipelineDefinitionAuthorizationService(this.mockedPipeLineDefinitionService);
    }

    @Test
    public void getAll_withPermissionsForTwoEntities_twoEntities() {
        //Arrange
        List<Permission> permissions = this.createPermissions();

        List<PipelineDefinition> entitiesToFilter = new ArrayList<>();
        entitiesToFilter.add(this.firstPipeline);
        entitiesToFilter.add(this.secondPipeline);
        entitiesToFilter.add(this.thirdPipeline);
        entitiesToFilter.add(this.fourthPipeline);

        //Act
        List<PipelineDefinition> actualResult = this.authorizationService.getAll(permissions, entitiesToFilter);

        //Assert
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_THREE_OBJECTS, actualResult.size());
    }

    @Test
    public void getById_withPermissionToGet_true() {
        //Arrange
        List<Permission> permissions = this.createPermissions();
        //Act
        boolean hasPermission = this.authorizationService.getById(this.firstPipeline.getId(), permissions);

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void getById_withoutPermissionToGet_false() {
        //Arrange
        List<Permission> permissions = this.createPermissions();
        //Act
        boolean hasPermission = this.authorizationService.getById(this.secondPipeline.getId(), permissions);

        //Assert
        Assert.assertFalse(hasPermission);
    }

    @Test
    public void add_withoutPermissionToAdd_false() {
        //Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setPipelineGroupId(this.firstPipelineGroup.getId());

        List<Permission> permissions = this.createPermissions();

        String entityToAdd = this.jsonConverter.toJson(pipelineDefinition);
        //Act
        boolean hasPermission = this.authorizationService.add(entityToAdd, permissions);

        //Assert
        Assert.assertFalse(hasPermission);
    }

    @Test
    public void update_withPermissionToUpdate_true() {
        //Arrange
        List<Permission> permissions = this.createPermissions();
        String entityToUpdate = this.jsonConverter.toJson(this.thirdPipeline);

        //Act
        boolean hasPermission = this.authorizationService.update(entityToUpdate, permissions);

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void update_withoutPermissionToUpdate_false() {
        //Arrange
        List<Permission> permissions = this.createPermissions();
        String entityToUpdate = this.jsonConverter.toJson(this.fourthPipeline);

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
        boolean hasPermission = this.authorizationService.delete(this.thirdPipeline.getId(), permissions);

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void delete_withoutPermissionToDelete_false() {
        //Arrange
        List<Permission> permissions = this.createPermissions();

        //Act
        boolean hasPermission = this.authorizationService.delete(this.firstPipeline.getId(), permissions);

        //Assert
        Assert.assertFalse(hasPermission);
    }

    @Test
    public void initialize_validConstructor_notNull() {
        //Act
        this.authorizationService = new PipelineDefinitionAuthorizationService();

        //Assert
        Assert.assertNotNull(this.authorizationService);
    }

    private List<Permission> createPermissions() {
        List<Permission> permissions = new ArrayList<>();

        Permission firstPermission = new Permission();
        firstPermission.setPermissionType(PermissionType.NONE);
        firstPermission.setPermissionScope(PermissionScope.PIPELINE_GROUP);
        firstPermission.setPermittedEntityId(this.firstPipelineGroup.getId());

        Permission secondPermission = new Permission();
        secondPermission.setPermissionType(PermissionType.OPERATOR);
        secondPermission.setPermissionScope(PermissionScope.PIPELINE);
        secondPermission.setPermittedEntityId(this.firstPipeline.getId());

        Permission thirdPermission = new Permission();
        thirdPermission.setPermissionType(PermissionType.ADMIN);
        thirdPermission.setPermissionScope(PermissionScope.SERVER);

        Permission fourthPermission = new Permission();
        fourthPermission.setPermissionType(PermissionType.VIEWER);
        fourthPermission.setPermissionScope(PermissionScope.PIPELINE_GROUP);
        fourthPermission.setPermittedEntityId(this.secondPipelineGroup.getId());

        Permission fifthPermission = new Permission();
        fifthPermission.setPermissionType(PermissionType.ADMIN);
        fifthPermission.setPermissionScope(PermissionScope.PIPELINE);
        fifthPermission.setPermittedEntityId(this.thirdPipeline.getId());

        permissions.add(firstPermission);
        permissions.add(secondPermission);
        permissions.add(thirdPermission);
        permissions.add(fourthPermission);
        permissions.add(fifthPermission);

        List<Permission> orderedPermissions = permissions.stream()
                .sorted((p1, p2) -> p1.getPermissionScope().compareTo(p2.getPermissionScope())).collect(Collectors.toList());

        return orderedPermissions;
    }
}
