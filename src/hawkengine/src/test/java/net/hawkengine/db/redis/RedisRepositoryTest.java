package net.hawkengine.db.redis;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.DbEntry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPoolConfig;

public class RedisRepositoryTest {
	private IDbRepository repository;

	@Before
	public void setUp() {
		MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "test");
		this.repository = new RedisRepository(DbEntry.class, mockedPool);
	}

	@Test
	public void getById() {
		//Arrange
		DbEntry entry = new DbEntry();
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
		DbEntry entry = new DbEntry();
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
