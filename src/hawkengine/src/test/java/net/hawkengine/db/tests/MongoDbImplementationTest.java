package net.hawkengine.db.tests;

import com.github.fakemongo.Fongo;
import com.mongodb.DBCollection;
import com.mongodb.FongoDB;
import junit.framework.TestCase;
import net.hawkengine.db.mongodb.MongoDbRepository;
import net.hawkengine.model.Agent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MongoDbImplementationTest {

    private DBCollection collection;
    private Agent agent;

    private MongoDbRepository<Agent> mockedRepository;

    @Before
    public void setUp() {
        Fongo fongo = new Fongo("mongo server 1");
        FongoDB db = fongo.getDB("mydb");
        this.collection = db.getCollection("mycollection");
        this.agent = new Agent();
        this.agent.setHostName("ggg");
        this.mockedRepository = new MongoDbRepository<>(Agent.class, this.collection);

    }

    @Test
    public void add_oneObject_theObject() {
        Agent agentFromDb = this.mockedRepository.add(this.agent);

        Assert.assertEquals(this.agent.getId(), agentFromDb.getId());
    }

    @Test
    public void getAll_oneObject() {

        this.mockedRepository.add(this.agent);
        long repositoryCollectionCount = this.mockedRepository.getAll().size();
        Assert.assertEquals(1, repositoryCollectionCount);
    }

    @Test
    public void getById_validId_theObject() {

        this.mockedRepository.add(this.agent);

        Agent dbObj = this.mockedRepository.getById(this.agent.getId());

        Assert.assertNotEquals(null, dbObj);
    }

    @Test
    public void getById_invalidId_null() {
        this.mockedRepository.add(this.agent);

        TestCase.assertNull(this.mockedRepository.getById("ghrjyyhjtdhg"));
    }

    @Test
    public void update_validObject_updatedObject() {

        this.mockedRepository.add(this.agent);

        this.agent.setHostName("newName");

        this.mockedRepository.update(this.agent);

        Agent result = this.mockedRepository.getAll().get(0);

        Assert.assertEquals(this.agent.getHostName(), result.getHostName());
    }

    @Test
    public void delete_validId_true() {
        //Arrange
        this.mockedRepository.add(this.agent);

        //Act
        boolean actualResult = this.mockedRepository.delete(this.agent.getId());

        //Assert
        Assert.assertTrue(actualResult);
    }
}
