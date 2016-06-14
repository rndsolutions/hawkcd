package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;

import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Agent;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.AgentService;
import net.hawkengine.services.interfaces.IAgentService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import redis.clients.jedis.JedisPoolConfig;

public class AgentServiceTests {

    private IAgentService mockedAgentService;
    private final String expectedEnabledAgentMessage = "All enabled Agents retrieved successfully.";
    private final String expectedEnabledIdleMessage = "All enabled idle Agents retrieved successfully.";

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testAgentService");
        IDbRepository<Agent> mockedRepository = new RedisRepository(Agent.class, mockedPool);
        this.mockedAgentService = new AgentService(mockedRepository);
    }

    @Test
    public void getAllEnabledAgents_withExistingObjects_twoObjects() {
        Agent firstAgent = new Agent();
        Agent secondAgent = new Agent();
        Agent thirdAgent = new Agent();
        firstAgent.setEnabled(true);
        secondAgent.setEnabled(true);
        this.mockedAgentService.add(firstAgent);
        this.mockedAgentService.add(secondAgent);
        this.mockedAgentService.add(thirdAgent);
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_TWO_OBJECTS;

        ServiceResult actualResult = this.mockedAgentService.getAllEnabledAgents();
        List<Agent> actualResultObject = (List<Agent>) actualResult.getObject();
        int actualCollectionSize = actualResultObject.size();

        Assert.assertNotNull(actualResultObject);
        Assert.assertEquals(this.expectedEnabledAgentMessage, actualResult.getMessage());
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
        Assert.assertFalse(actualResult.hasError());

        for (Agent agent : actualResultObject) {
            Assert.assertEquals(agent.isEnabled(), true);
        }
    }

    @Test
    public void getAllEnabledAgents_withNonexistentObjects_noObjects() {
        int expectedSizeOfCollection = TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS;

        ServiceResult actualResult = this.mockedAgentService.getAllEnabledAgents();
        List<Agent> actualResultObject = (List<Agent>) actualResult.getObject();
        int actualCollectionSize = actualResultObject.size();

        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(this.expectedEnabledAgentMessage, actualResult.getMessage());
        Assert.assertNotNull(actualResult.getObject());
        Assert.assertEquals(expectedSizeOfCollection, actualCollectionSize);
    }

    @Test
    public void getAllEnabledIdleAgents_withExistingObjects_oneObject() {
        Agent firstAgent = new Agent();
        firstAgent.setEnabled(true);
        firstAgent.setRunning(false);
        Agent secondAgent = new Agent();
        secondAgent.setRunning(true);
        Agent thirdAgent = new Agent();
        thirdAgent.setEnabled(false);
        this.mockedAgentService.add(firstAgent);
        this.mockedAgentService.add(secondAgent);
        this.mockedAgentService.add(thirdAgent);
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;

        ServiceResult actualResult = this.mockedAgentService.getAllEnabledIdleAgents();
        AtomicReference<List<Agent>> actualResultObject = new AtomicReference<>((List<Agent>) actualResult.getObject());
        int actualCollectionSize = actualResultObject.get().size();
        Agent actualAgent = actualResultObject.get().get(0);

        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(this.expectedEnabledIdleMessage, actualResult.getMessage());
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
        Assert.assertEquals(firstAgent.getId(), actualAgent.getId());
        Assert.assertTrue(actualAgent.isEnabled());
        Assert.assertFalse(actualAgent.isRunning());
    }

    @Test
    public void getAllEnabledIdleAgents_withNonexistentObjects_noObjects() {
        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS;

        ServiceResult actualResult = this.mockedAgentService.getAllEnabledIdleAgents();
        List<Agent> actualResultObject = (List<Agent>) actualResult.getObject();
        int actualCollectionSize = actualResultObject.size();

        Assert.assertNotNull(actualResultObject);
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(this.expectedEnabledIdleMessage, actualResult.getMessage());
        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
    }
}