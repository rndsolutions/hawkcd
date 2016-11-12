package io.hawkcd.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;

import io.hawkcd.Config;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.db.redis.RedisRepository;
import io.hawkcd.model.Agent;
import io.hawkcd.services.AgentService;
import io.hawkcd.services.interfaces.IAgentService;

import org.junit.Before;

import org.junit.BeforeClass;

import redis.clients.jedis.JedisPoolConfig;

public class AgentServiceTests {
    private IAgentService mockedAgentService;

    @BeforeClass
    public static void setUpClass() {
        Config.configure();
    }

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testAgentService");
        IDbRepository<Agent> mockedRepository = new RedisRepository(Agent.class, mockedPool);
        this.mockedAgentService = new AgentService();
    }

//    @Test
//    public void getAllEnabledAgents_withExistingObjects_twoObjects() {
//        Agent firstAgent = new Agent();
//        Agent secondAgent = new Agent();
//        Agent thirdAgent = new Agent();
//        firstAgent.setEnabled(true);
//        secondAgent.setEnabled(true);
//        this.mockedAgentService.add(firstAgent);
//        this.mockedAgentService.add(secondAgent);
//        this.mockedAgentService.add(thirdAgent);
//        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_TWO_OBJECTS;
//
//        ServiceResult actualResult = this.mockedAgentService.getAllEnabledAgents();
//        List<Agent> actualResultObject = (List<Agent>) actualResult.getObject();
//        int actualCollectionSize = actualResultObject.size();
//
//        Assert.assertNotNull(actualResultObject);
//        Assert.assertEquals(TestsConstants.EXPECTED_ENABLED_AGEND_MESSAGE, actualResult.getMessage());
//        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
//        Assert.assertFalse(actualResult.hasError());
//
//        for (Agent agent : actualResultObject) {
//            Assert.assertEquals(agent.isEnabled(), true);
//        }
//    }
//
//    @Test
//    public void getAllEnabledAgents_withNonexistentObjects_noObjects() {
//        int expectedSizeOfCollection = TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS;
//
//        ServiceResult actualResult = this.mockedAgentService.getAllEnabledAgents();
//        List<Agent> actualResultObject = (List<Agent>) actualResult.getObject();
//        int actualCollectionSize = actualResultObject.size();
//
//        Assert.assertFalse(actualResult.hasError());
//        Assert.assertEquals(TestsConstants.EXPECTED_ENABLED_AGEND_MESSAGE, actualResult.getMessage());
//        Assert.assertNotNull(actualResult.getObject());
//        Assert.assertEquals(expectedSizeOfCollection, actualCollectionSize);
//    }
//
//    @Test
//    public void getAllEnabledIdleAgents_withExistingObjects_oneObject() {
//        Agent firstAgent = new Agent();
//        firstAgent.setEnabled(true);
//        firstAgent.setRunning(false);
//        Agent secondAgent = new Agent();
//        secondAgent.setRunning(true);
//        Agent thirdAgent = new Agent();
//        thirdAgent.setEnabled(false);
//        this.mockedAgentService.add(firstAgent);
//        this.mockedAgentService.add(secondAgent);
//        this.mockedAgentService.add(thirdAgent);
//        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT;
//
//        ServiceResult actualResult = this.mockedAgentService.getAllEnabledIdleAgents();
//        AtomicReference<List<Agent>> actualResultObject = new AtomicReference<>((List<Agent>) actualResult.getObject());
//        int actualCollectionSize = actualResultObject.get().size();
//        Agent actualAgent = actualResultObject.get().get(0);
//
//        Assert.assertFalse(actualResult.hasError());
//        Assert.assertEquals(TestsConstants.EXPECTED_IDLE_AGENT_MESSAGE, actualResult.getMessage());
//        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
//        Assert.assertEquals(firstAgent.getId(), actualAgent.getId());
//        Assert.assertTrue(actualAgent.isEnabled());
//        Assert.assertFalse(actualAgent.isRunning());
//    }
//
//    @Test
//    public void getAllEnabledIdleAgents_withNonexistentObjects_noObjects() {
//        int expectedCollectionSize = TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS;
//
//        ServiceResult actualResult = this.mockedAgentService.getAllEnabledIdleAgents();
//        List<Agent> actualResultObject = (List<Agent>) actualResult.getObject();
//        int actualCollectionSize = actualResultObject.size();
//
//        Assert.assertNotNull(actualResultObject);
//        Assert.assertFalse(actualResult.hasError());
//        Assert.assertEquals(TestsConstants.EXPECTED_IDLE_AGENT_MESSAGE, actualResult.getMessage());
//        Assert.assertEquals(expectedCollectionSize, actualCollectionSize);
//    }
}