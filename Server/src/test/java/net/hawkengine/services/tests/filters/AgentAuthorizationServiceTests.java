package net.hawkengine.services.tests.filters;

import com.fiftyonred.mock_jedis.MockJedisPool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.ServerConfiguration;
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
import net.hawkengine.services.AgentService;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.filters.AgentAuthorizationService;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;
import net.hawkengine.services.interfaces.IAgentService;
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

public class AgentAuthorizationServiceTests {
    Agent firstAgent;
    Agent secondAgent;

    IAuthorizationService authorizationService;

    Gson jsonConverter;

    private IDbRepository<Agent> mockedAgentRepository;

    @BeforeClass
    public static void setUpClass() {
        ServerConfiguration.configure();
    }

    @Before
    public void setUp() {
        this.firstAgent = new Agent();
        this.secondAgent = new Agent();

        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();

        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testAgentAuthorizationService");
        this.mockedAgentRepository = new RedisRepository(Agent.class, mockedPool);
        this.authorizationService = new AgentAuthorizationService();
    }

    @Test
    public void getAll_withPermissions_twoAgents(){
        //Arrange
        List<Agent> expectedAgents = new ArrayList<>();
        expectedAgents.add(this.firstAgent);
        expectedAgents.add(this.secondAgent);

        //Act
        List<Agent> actualAgents = this.authorizationService.getAll(this.createPermissions(), expectedAgents);

        //Assert
        Assert.assertEquals(expectedAgents.size(), actualAgents.size());
    }

    @Test
    public void getAll_withoutPermissions_twoAgents(){
        //Arrange
        List<Agent> expectedAgents = new ArrayList<>();
        expectedAgents.add(this.firstAgent);
        expectedAgents.add(this.secondAgent);

        List<Permission> expectedPermissions = new ArrayList<>();

        //Act
        List<Agent> actualAgents = this.authorizationService.getAll(expectedPermissions, expectedAgents);

        //Assert
        Assert.assertEquals(expectedAgents.size(), actualAgents.size());
    }

    @Test
    public void getById_withPermissions_true(){
        //Arrange
        List<Agent> expectedAgents = new ArrayList<>();
        expectedAgents.add(this.firstAgent);
        expectedAgents.add(this.secondAgent);

        //Act
        boolean hasPermission = this.authorizationService.getById(this.firstAgent.getId(), expectedAgents);

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void getById_withoutPermissions_true(){
        //Arrange
        List<Agent> expectedAgents = new ArrayList<>();
        expectedAgents.add(this.firstAgent);
        expectedAgents.add(this.secondAgent);

        //Act
        boolean hasPermission = this.authorizationService.getById(this.firstAgent.getId(), expectedAgents);

        //Assert
        Assert.assertTrue(hasPermission);
    }

    @Test
    public void update_withPermissions_true(){
        //Arrange
        Agent expectedAgent = new Agent();
        String expectedEntityAsString = this.jsonConverter.toJson(expectedAgent);

        //Act
        boolean hasPermission = this.authorizationService.update(expectedEntityAsString, this.createPermissions());

        //Assert
        Assert.assertTrue(hasPermission);

    }

    @Test
    public void update_withoutPermissions_false(){
        //Arrange
        Agent expectedAgent = new Agent();
        String expectedEntityAsString = this.jsonConverter.toJson(expectedAgent);
        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean hasPermission = this.authorizationService.update(expectedEntityAsString, permissions);

        //Assert
        Assert.assertFalse(hasPermission);

    }

    @Test
    public void delete_withPermissions_true(){
        //Act
        boolean hasPermission = this.authorizationService.delete(this.firstAgent.getId(), this.createPermissions());

        //Assert
        Assert.assertTrue(hasPermission);

    }

    @Test
    public void delete_withoutPermissions_false(){
        //Arrange
        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean hasPermission = this.authorizationService.add(this.firstAgent.getId(), permissions);

        //Assert
        Assert.assertFalse(hasPermission);

    }

    @Test
    public void add_withPermissions_true(){
        //Arrange
        Agent expectedAgent = new Agent();
        String expectedEntityAsString = this.jsonConverter.toJson(expectedAgent);

        //Act
        boolean hasPermission = this.authorizationService.add(expectedEntityAsString, this.createPermissions());

        //Assert
        Assert.assertTrue(hasPermission);

    }

    @Test
    public void add_withoutPermissions_false(){
        //Arrange
        Agent expectedAgent = new Agent();
        String expectedEntityAsString = this.jsonConverter.toJson(expectedAgent);
        List<Permission> permissions = new ArrayList<>();

        //Act
        boolean hasPermission = this.authorizationService.add(expectedEntityAsString, permissions);

        //Assert
        Assert.assertFalse(hasPermission);

    }

    private List<Permission> createPermissions(){
        List<Permission> permissions = new ArrayList<>();

        Permission firstPermission = new Permission();
        firstPermission.setPermissionScope(PermissionScope.SERVER);
        firstPermission.setPermissionType(PermissionType.ADMIN);
        firstPermission.setPermittedEntityId(PermissionScope.SERVER.toString());

        permissions.add(firstPermission);

        return permissions;
    }

}
