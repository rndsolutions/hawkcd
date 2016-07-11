package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Material;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.MaterialService;
import net.hawkengine.services.interfaces.IMaterialService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPoolConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MaterialServiceTests {
    private IDbRepository<Material> repository;
    private IMaterialService materialService;

    @Before
    public void setUp() {
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testMaterialService");
        this.repository = new RedisRepository(Material.class, mockedPool);
        this.materialService = new MaterialService(this.repository);
    }

    @Test
    public void getById_existingObject_correctObject() {
        //Arrange
        Material expectedMaterial = new Material();
        String expectedMessage = Material.class.getSimpleName() + " " + expectedMaterial.getId() + " retrieved successfully.";
        this.materialService.add(expectedMaterial);

        //Act
        ServiceResult actualResult = this.materialService.getById(expectedMaterial.getId());
        Material actualMaterial = (Material) actualResult.getObject();

        //Assert
        Assert.assertNotNull(actualMaterial);
        Assert.assertEquals(expectedMaterial.getId(), actualMaterial.getId());
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getById_nonExistingObject_correctErrorMessage() {
        //Arrange
        UUID invalidId = UUID.randomUUID();
        String expectedMessage = Material.class.getSimpleName() + " not found.";

        //Act
        ServiceResult actualResult = this.materialService.getById(invalidId.toString());

        //Assert
        Assert.assertTrue(actualResult.hasError());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void getAll_withExistingObjects_allObjects() {
        //Arrange
        Material firstExpectedMaterial = new Material();
        Material secondExpectedMaterial = new Material();
        this.materialService.add(firstExpectedMaterial);
        this.materialService.add(secondExpectedMaterial);
        String expectedMessage = Material.class.getSimpleName() + "s retrieved successfully.";

        //Act
        ServiceResult actualResult = this.materialService.getAll();
        List<Material> actualResultObject = (List<Material>) actualResult.getObject();
        Material firstActualMaterial = actualResultObject
                .stream()
                .filter(p -> p.getId().equals(firstExpectedMaterial.getId()))
                .collect(Collectors.toList())
                .get(0);

        Material secondActualMaterial = actualResultObject
                .stream()
                .filter(p -> p.getId().equals(secondExpectedMaterial.getId()))
                .collect(Collectors.toList())
                .get(0);

        //Assert
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(firstExpectedMaterial.getId(), firstActualMaterial.getId());
        Assert.assertEquals(secondExpectedMaterial.getId(), secondActualMaterial.getId());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_TWO_OBJECTS, actualResultObject.size());
    }

    @Test
    public void getAll_withNonExistingObjects_noObjects() {
        //Arrange
        String expectedMessage = Material.class.getSimpleName() + "s retrieved successfully.";

        //Act
        ServiceResult actualResult = this.materialService.getAll();
        List<Material> actualResultObject = (List<Material>) actualResult.getObject();

        //Assert
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS, actualResultObject.size());
    }

    @Test
    public void add_nonExistingObject_correctObject() {
        //Arrange
        Material expectedMaterial = new Material();
        String expectedMessage = Material.class.getSimpleName() + " " + expectedMaterial.getId() + " created successfully.";

        //Act
        ServiceResult actualResult = this.materialService.add(expectedMaterial);
        Material actualResultObject = (Material) actualResult.getObject();

        //Assert
        Assert.assertFalse(actualResult.hasError());
        Assert.assertNotNull(actualResultObject);
        Assert.assertEquals(expectedMaterial.getId(), actualResultObject.getId());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void add_existingObject_correctErrorMessage() {
        //Arrange
        Material expectedMaterial = new Material();
        String expectedMessage = Material.class.getSimpleName() + " already exists.";

        //Act
        this.materialService.add(expectedMaterial);
        ServiceResult actualResult = this.materialService.add(expectedMaterial);
        Material actualResultObject = (Material) actualResult.getObject();

        //Assert
        Assert.assertTrue(actualResult.hasError());
        Assert.assertNull(actualResultObject);
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void update_existingObject_updatedObject() {
        //Arrange
        Material expectedMaterial = new Material();
        String expectedMessage = Material.class.getSimpleName() + " " + expectedMaterial.getId() + " updated successfully.";
        LocalDateTime initialDate = LocalDateTime.of(2016, 7, 10, 12, 00);
        expectedMaterial.setChangeDate(initialDate);
        this.materialService.add(expectedMaterial);

        //Act
        LocalDateTime expectedDate = LocalDateTime.of(2016, 7, 11, 12, 00);
        expectedMaterial.setChangeDate(expectedDate);
        ServiceResult actualResult = this.materialService.update(expectedMaterial);
        Material actualResultObject = (Material) actualResult.getObject();

        //Assert
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertNotNull(actualResultObject);
        Assert.assertEquals(expectedDate, actualResultObject.getChangeDate());
        Assert.assertNotEquals(initialDate, actualResultObject.getChangeDate());
    }

    @Test
    public void update_nonExistingObject_correctErrorMessage() {
        //Arrange
        Material expectedMaterial = new Material();
        String expectedMessage = Material.class.getSimpleName() + " " + "not found.";

        //Act
        ServiceResult actualResult = this.materialService.update(expectedMaterial);

        //Assert
        Assert.assertTrue(actualResult.hasError());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void delete_existingObject_false() {
        //Arrange
        Material materialToDelete = new Material();
        this.materialService.add(materialToDelete);
        String expectedMessage = Material.class.getSimpleName() + " deleted successfully.";

        //Act
        ServiceResult actualResult = this.materialService.delete(materialToDelete.getId());
        Material actualResultObject = (Material) actualResult.getObject();

        //Assert
        Assert.assertFalse(actualResult.hasError());
        Assert.assertNotNull(actualResult.getObject());
        Assert.assertEquals(materialToDelete.getId(), actualResultObject.getId());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }

    @Test
    public void delete_nonExistingObject_true() {
        //Arrange
        Material materialToDelete = new Material();
        String expectedMessage = Material.class.getSimpleName() + " not found.";

        //Act
        ServiceResult actualResult = this.materialService.delete(materialToDelete.getId());

        //Assert
        Assert.assertTrue(actualResult.hasError());
        Assert.assertNull(actualResult.getObject());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
    }
}
