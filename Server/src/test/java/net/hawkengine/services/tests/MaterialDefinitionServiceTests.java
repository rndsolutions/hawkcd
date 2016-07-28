package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.interfaces.IMaterialDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import org.junit.Before;
import org.junit.BeforeClass;
import redis.clients.jedis.JedisPoolConfig;

public class MaterialDefinitionServiceTests {
    private IPipelineDefinitionService pipelineDefinitionService;
    private IMaterialDefinitionService materialDefinitionService;

    @BeforeClass
    public static void setUpClass() {
        ServerConfiguration.configure();
    }

    @Before
    public void setUp(){
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testMaterialDefinitionService");
        IDbRepository pipelineDefinitionRepo = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.pipelineDefinitionService = new PipelineDefinitionService(pipelineDefinitionRepo);
//        this.materialDefinitionService = new MaterialDefinitionService(this.pipelineDefinitionService);
    }


}
