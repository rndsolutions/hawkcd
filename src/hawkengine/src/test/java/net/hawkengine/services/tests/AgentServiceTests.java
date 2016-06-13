package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Agent;
import net.hawkengine.model.AgentExecutionState;
import net.hawkengine.model.ConfigState;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.AgentService;
import net.hawkengine.services.interfaces.IAgentService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import redis.clients.jedis.JedisPoolConfig;

public class AgentServiceTests {

    private IDbRepository<Agent> mockedRepository;
    private IAgentService mockedAgentService;
    private String expectedEnabledAgentMessage = "All enabled Agents retrieved successfully.";
    private String expectedEnabledIdleMessage = "All enabled idle Agents retrieved successfully.";

    @Before
    public void setUp() throws Exception {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testAgentService");
        this.mockedRepository = new RedisRepository(Agent.class, mockedPool);
        this.mockedAgentService = new AgentService(this.mockedRepository);
    }

    @Test
    public void getAllEnabledAgents_withExistingObjects_validObjects() {
        Agent firstAgent = new Agent();
        Agent secondAgent = new Agent();
        Agent thirdAgent = new Agent();
        thirdAgent.setConfigState(ConfigState.Disabled);
        this.mockedAgentService.add(firstAgent);
        this.mockedAgentService.add(secondAgent);
        this.mockedAgentService.add(thirdAgent);
        int expectedCollectionSize = 2;

        ServiceResult actualResult = this.mockedAgentService.getAllEnabledAgents();
        List<Agent> actualResultObject = (List<Agent>) actualResult.getObject();
        int actualCollectionSize = actualResultObject.size();

        Assert.assertNotNull(actualResultObject);
        Assert.assertEquals(expectedEnabledAgentMessage, actualResult.getMessage());
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
        Assert.assertFalse(actualResult.hasError());

        for (Agent agent : actualResultObject) {
            Assert.assertEquals(agent.getConfigState(), ConfigState.Enabled);
        }
    }

    @Test
    public void getAllEnabledAgents_withNonexistentObjects_noObjects() {
        int expectedSizeOfCollection = 0;

        ServiceResult actualResult = this.mockedAgentService.getAllEnabledAgents();
        List<Agent> actualResultObject = (List<Agent>) actualResult.getObject();
        int actualCollectionSize = actualResultObject.size();

        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedEnabledAgentMessage, actualResult.getMessage());
        Assert.assertNotNull(actualResult.getObject());
        Assert.assertEquals(expectedSizeOfCollection, actualCollectionSize);
    }

    @Test
    public void getAllEnabledIdleAgents_withExistingObjects_validObjects() {
        Agent firstAgent = new Agent();
        Agent secondAgent = new Agent();
        secondAgent.setExecutionState(AgentExecutionState.Running);
        Agent thirdAgent = new Agent();
        thirdAgent.setConfigState(ConfigState.Disabled);
        this.mockedAgentService.add(firstAgent);
        this.mockedAgentService.add(secondAgent);
        this.mockedAgentService.add(thirdAgent);
        int expectedCollectionSize = 1;

        ServiceResult actualResult = this.mockedAgentService.getAllEnabledIdleAgents();
        List<Agent> actualResultObject = (List<Agent>) actualResult.getObject();
        int actualCollectionSize = actualResultObject.size();
        Agent actualAgent = actualResultObject.get(0);

        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedEnabledIdleMessage, actualResult.getMessage());
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
        Assert.assertEquals(firstAgent.getId(), actualAgent.getId());
        Assert.assertTrue(actualAgent.getConfigState() == ConfigState.Enabled);
        Assert.assertTrue(actualAgent.getExecutionState() == AgentExecutionState.Idle);
    }

    @Test
    public void getAllEnabledIdleAgents_withNonexistentObjects_noObjects() {
        int expectedCollectionSize = 0;

        ServiceResult actualResult = this.mockedAgentService.getAllEnabledIdleAgents();
        List<Agent> actualResultObject = (List<Agent>) actualResult.getObject();
        int actualCollectionSize = actualResultObject.size();

        Assert.assertNotNull(actualResultObject);
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedEnabledIdleMessage, actualResult.getMessage());
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }
}