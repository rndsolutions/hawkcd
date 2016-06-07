package net.hawkengine.db.tests;

import com.github.fakemongo.Fongo;
import com.mongodb.DBCollection;
import com.mongodb.FongoDB;
import net.hawkengine.db.mongodb.MongoDbRepository;
import net.hawkengine.model.Agent;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MongoDbImplementationTest {

	private static DBCollection collection;
	private static Agent agent;

	private static MongoDbRepository<Agent> mockedRepository;

	@BeforeClass
	public static void initializeDB() throws IOException {
		Fongo fongo = new Fongo("mongo server 1");
		FongoDB db = fongo.getDB("mydb");
		collection = db.getCollection("mycollection");
		agent = new Agent();
		agent.setHostName("ggg");
	}

	@Before
	public void setUp() throws Exception {

		this.mockedRepository = new MongoDbRepository<>(Agent.class, this.collection);
	}

	@Test
	public void add_OneObject_TheObject() throws Exception {
		Object agentFromDb = this.mockedRepository.add(this.agent);
	}

	@Test
	public void getAll_OneObject() throws Exception {

		long repositoryCollectionCount = this.mockedRepository.getAll().size();
		assertEquals(1, repositoryCollectionCount);
	}

	@Test
	public void getById_ValidId_TheObject() throws Exception {

		Agent dbObj = this.mockedRepository.getById(this.agent.getId());

		assertNotEquals(null, dbObj);
	}

	@Test
	public void getById_InvalidId_Null() throws Exception {
		assertNull(this.mockedRepository.getById("ghrjyyhjtdhg"));
	}

	@Test
	public void update_ValidObject_UpdatedObject() throws Exception {

		this.agent.setHostName("newName");

		this.mockedRepository.update(this.agent);

		Agent result = this.mockedRepository.getAll().get(0);

		assertEquals(this.agent.getHostName(), result.getHostName());
	}

//	@Test
//	public void delete_ValidId_True() throws Exception {
//		//Arrange
//
//		boolean expectedResult = true;
//
//		//Act
//		boolean actualResult = mockedRepository.delete(agent.getId());
//
//		//Assert
//		assertEquals(expectedResult, actualResult);
//	}
}
