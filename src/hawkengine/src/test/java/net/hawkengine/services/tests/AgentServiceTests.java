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

    private IDbRepository<Agent> mockedAgentRepo;
    private IAgentService mockedAgentService;
    private String expectedEnabledAgentMessage = "All enabled Agents retrieved successfully.";
    private String expectedEnabledIdleMessage = "All enabled idle Agents retrieved successfully.";

    @Before
    public void setUp() throws Exception {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testAgentService");
        this.mockedAgentRepo = new RedisRepository(Agent.class, mockedPool);
        this.mockedAgentService = new AgentService(this.mockedAgentRepo);
    }

    @Test
    public void getAllEnabledAgents_withValidInput_shouldReturnValidObject() throws Exception {
        Agent firstAgent = new Agent();
        Agent secondAgent = new Agent();
        Agent thirdAgent = new Agent();
        thirdAgent.setConfigState(ConfigState.Disabled);
        this.mockedAgentService.add(firstAgent);
        this.mockedAgentService.add(secondAgent);
        this.mockedAgentService.add(thirdAgent);
        int expectedCollectionSize = 2;

        ServiceResult actualResultObject = this.mockedAgentService.getAllEnabledAgents();
        List<Agent> actualObject = (List<Agent>) actualResultObject.getObject();

        Assert.assertNotNull(actualObject);
        Assert.assertEquals(expectedEnabledAgentMessage, actualResultObject.getMessage());
        Assert.assertEquals(expectedCollectionSize, actualObject.size());
        Assert.assertFalse(actualResultObject.hasError());

        for (Agent agent : actualObject) {
            Assert.assertEquals(agent.getConfigState(), ConfigState.Enabled);
        }
    }

    @Test
    public void getAllEnabledAgents_withNoAgentsInput_shouldReturnValidObject() throws Exception {
        ServiceResult actual = this.mockedAgentService.getAllEnabledAgents();
        List<Agent> actualAgentCollection = (List<Agent>) actual.getObject();
        int actualSizeOfCollection = actualAgentCollection.size();
        int expectedSizeOfCollection = 0;

        Assert.assertFalse(actual.hasError());
        Assert.assertEquals(expectedEnabledAgentMessage, actual.getMessage());
        Assert.assertNotNull(actual.getObject());
        Assert.assertEquals(expectedSizeOfCollection, actualSizeOfCollection);
    }

    @Test
    public void getAllEnabledIdle_withValidInput_shouldReturnValidModelState() throws Exception {
        int expectedCollectionSize = 1;
        Agent firstAgent = new Agent();
        Agent secondAgent = new Agent();
        secondAgent.setExecutionState(AgentExecutionState.Running);
        Agent thirdAgent = new Agent();
        thirdAgent.setConfigState(ConfigState.Disabled);
        this.mockedAgentService.add(firstAgent);
        this.mockedAgentService.add(secondAgent);
        this.mockedAgentService.add(thirdAgent);

        ServiceResult actualResultObject = this.mockedAgentService.getAllEnabledIdleAgents();
        List<Agent> actualAgentsObjects = (List<Agent>) actualResultObject.getObject();
        int actualCollectionSize = actualAgentsObjects.size();
        Agent actualAgent = actualAgentsObjects.get(0);

        Assert.assertFalse(actualResultObject.hasError());
        Assert.assertEquals(expectedEnabledIdleMessage, actualResultObject.getMessage());
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
        Assert.assertEquals(firstAgent.getId(), actualAgent.getId());
        Assert.assertTrue(actualAgent.getConfigState() == ConfigState.Enabled);
        Assert.assertTrue(actualAgent.getExecutionState() == AgentExecutionState.Idle);
    }

    @Test
    public void getAllEnabledIdle_withNoAgents_shouldReturnValidObject() throws Exception {
        ServiceResult actualResultObject = this.mockedAgentService.getAllEnabledIdleAgents();
        List<Agent> actualCollection = (List<Agent>)actualResultObject.getObject();
        int expectedCollectionSize = 0;
        int actualCollectionSize = actualCollection.size();

        Assert.assertNotNull(actualCollection);
        Assert.assertFalse(actualResultObject.hasError());
        Assert.assertEquals(expectedEnabledIdleMessage,actualResultObject.getMessage());
        Assert.assertEquals(expectedCollectionSize,actualCollectionSize);

    }
}