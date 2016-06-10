package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Agent;
import net.hawkengine.model.AgentExecutionState;
import net.hawkengine.model.ConfigState;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.AgentService;
import net.hawkengine.services.IAgentService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import redis.clients.jedis.JedisPoolConfig;

public class AgentServiceTests {

    private IDbRepository<Agent> agentRepo;
    private IAgentService agentService;
    private String expectedEnabledAgentMessage = "All enabled Agents retrieved successfully.";
    private String expectedEnabledIdleMessage = "All enabled idle Agents retrieved successfully.";

    @Before
    public void setUp() throws Exception {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testAgentService");
        this.agentRepo = new RedisRepository(Agent.class, mockedPool);
        this.agentService = new AgentService(this.agentRepo);
    }

    @Test
    public void getAllEnabledAgents_withValidInput_shouldReturnValidModelState() throws Exception {
        Agent firstAgent = new Agent();
        Agent secondAgent = new Agent();
        Agent thirdAgent = new Agent();
        thirdAgent.setConfigState(ConfigState.Disabled);
        this.agentService.add(firstAgent);
        this.agentService.add(secondAgent);
        this.agentService.add(thirdAgent);
        int expectedCollectionSize = 2;

        ServiceResult actual = this.agentService.getAllEnabledAgents();
        List<Agent> actualObject = (List<Agent>) actual.getObject();

        Assert.assertNotNull(actualObject);
        Assert.assertEquals(expectedEnabledAgentMessage, actual.getMessage());
        Assert.assertEquals(expectedCollectionSize, actualObject.size());
        Assert.assertFalse(actual.hasError());

        for (Agent agent : actualObject) {
            Assert.assertEquals(agent.getConfigState(), ConfigState.Enabled);
        }
    }

    @Test
    public void getAllEnabledAgents_withNoAgents_shouldReturnValidModelState() throws Exception {
        ServiceResult actual = this.agentService.getAllEnabledAgents();
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
        this.agentService.add(firstAgent);
        this.agentService.add(secondAgent);
        this.agentService.add(thirdAgent);

        ServiceResult actual = this.agentService.getAllEnabledIdleAgents();
        List<Agent> actualAgentCollection = (List<Agent>) actual.getObject();
        int actualCollectionSize = actualAgentCollection.size();
        Agent actualAgent = actualAgentCollection.get(0);

        Assert.assertFalse(actual.hasError());
        Assert.assertEquals(expectedEnabledIdleMessage, actual.getMessage());
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
        Assert.assertEquals(firstAgent.getId(), actualAgent.getId());
        Assert.assertTrue(actualAgent.getConfigState() == ConfigState.Enabled);
        Assert.assertTrue(actualAgent.getExecutionState() == AgentExecutionState.Idle);
    }

    @Test
    public void getAllEnabledIdle_withNoAgents_shouldReturnValidModelState() throws Exception {
        ServiceResult actual = this.agentService.getAllEnabledIdleAgents();
        List<Agent> actualCollection = (List<Agent>)actual.getObject();
        int expectedCollectionSize = 0;
        int actualCollectionSize = actualCollection.size();

        Assert.assertNotNull(actualCollection);
        Assert.assertFalse(actual.hasError());
        Assert.assertEquals(expectedEnabledIdleMessage,actual.getMessage());
        Assert.assertEquals(expectedCollectionSize,actualCollectionSize);

    }
}