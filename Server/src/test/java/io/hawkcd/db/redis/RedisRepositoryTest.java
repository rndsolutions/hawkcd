package io.hawkcd.db.redis;

import com.fiftyonred.mock_jedis.MockJedisPool;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.model.Entity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.JedisPoolConfig;

public class RedisRepositoryTest {
    private IDbRepository repository;

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "test");
        this.repository = new RedisRepository(Entity.class, mockedPool);
    }

    @Test
    public void getById() {
        //Arrange
        Entity entry = new Entity();
        this.repository.add(entry);
        String expectedResult = entry.getId();

        //Act
        String actualResult = this.repository.getById(entry.getId()).getId();

        //Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void add() {
        //Arrange
        Entity entry = new Entity();
        String expectedResult = entry.getId();

        //Act
        String actualResult = this.repository.add(entry).getId();

        //Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

//	@Test
//	public void update() throws Exception {
//		//Arrange
//		boolean expectedResult = true;
//
//		//
//		boolean isUpdated = this.repository.update(testEntry);
//		System.out.println(isUpdated);
//
//	}


}
