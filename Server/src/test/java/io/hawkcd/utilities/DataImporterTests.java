package io.hawkcd.utilities;

import com.fiftyonred.mock_jedis.MockJedisPool;
import io.hawkcd.core.config.Config;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.db.redis.RedisRepository;
import io.hawkcd.model.PipelineGroup;
import io.hawkcd.model.User;
import io.hawkcd.services.PipelineGroupService;
import io.hawkcd.services.UserService;
import io.hawkcd.services.interfaces.IPipelineGroupService;
import io.hawkcd.services.interfaces.IUserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.JedisPoolConfig;

public class DataImporterTests {
    private IDbRepository<PipelineGroup> mockedPipelineGroupRepository;
    private IDbRepository<User> mockedUserRepository;
    private IPipelineGroupService pipelineGroupService;
    private IUserService userService;
    private Initializer initializer;

    @BeforeClass
    public static void setUpClass() {
        Config.configure();
    }

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testDataImporter");
        this.mockedPipelineGroupRepository = new RedisRepository(PipelineGroup.class, mockedPool);
        this.mockedUserRepository = new RedisRepository(User.class, mockedPool);

        this.pipelineGroupService = new PipelineGroupService(this.mockedPipelineGroupRepository);
        this.userService = new UserService(this.mockedUserRepository);

        this.initializer = new Initializer(this.userService, this.pipelineGroupService);
    }

//    @Test
//    public void importData_validInput_addedEntities() {
//        //Act
//        this.initializer.initialize();
//
//        //Assert
//        List<PipelineGroup> actualPipelineGroups = (List<PipelineGroup>) this.pipelineGroupService.getAll().getEntity();
//        List<User> actualUsers = (List<User>) this.userService.getAll().getEntity();
//
//        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualPipelineGroups.size());
//        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualUsers.size());
//    }

//    @Test
//    public void importData_twoTimesCalled_addedEntitiesOneTime() {
//        //Act
//        this.initializer.initialize();
//        this.initializer.initialize();
//
//        //Assert
//        List<PipelineGroup> actualPipelineGroups = (List<PipelineGroup>) this.pipelineGroupService.getAll().getEntity();
//        List<User> actualUsers = (List<User>) this.userService.getAll().getEntity();
//
//        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualPipelineGroups.size());
//        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualUsers.size());
//    }

    @Test
    public void initialize_validConstructor_notNull() {
        //Act
        this.initializer = new Initializer();

        //Assert
        Assert.assertNotNull(this.initializer);
    }
}
