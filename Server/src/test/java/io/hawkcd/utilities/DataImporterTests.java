package io.hawkcd.utilities;

import com.fiftyonred.mock_jedis.MockJedisPool;

import io.hawkcd.Config;
import io.hawkcd.utilities.constants.TestsConstants;
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

import java.util.List;

public class DataImporterTests {
    private IDbRepository<PipelineGroup> mockedPipelineGroupRepository;
    private IDbRepository<User> mockedUserRepository;
    private IPipelineGroupService pipelineGroupService;
    private IUserService userService;
    private DataImporter dataImporter;

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

        this.dataImporter = new DataImporter(this.userService, this.pipelineGroupService);
    }

    @Test
    public void importData_validInput_addedEntities() {
        //Act
        this.dataImporter.importDefaultEntities();

        //Assert
        List<PipelineGroup> actualPipelineGroups = (List<PipelineGroup>) this.pipelineGroupService.getAll().getObject();
        List<User> actualUsers = (List<User>) this.userService.getAll().getObject();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualPipelineGroups.size());
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualUsers.size());
    }

    @Test
    public void importData_twoTimesCalled_addedEntitiesOneTime() {
        //Act
        this.dataImporter.importDefaultEntities();
        this.dataImporter.importDefaultEntities();

        //Assert
        List<PipelineGroup> actualPipelineGroups = (List<PipelineGroup>) this.pipelineGroupService.getAll().getObject();
        List<User> actualUsers = (List<User>) this.userService.getAll().getObject();

        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualPipelineGroups.size());
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_ONE_OBJECT, actualUsers.size());
    }

    @Test
    public void initialize_validConstructor_notNull() {
        //Act
        this.dataImporter = new DataImporter();

        //Assert
        Assert.assertNotNull(this.dataImporter);
    }
}
