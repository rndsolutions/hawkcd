package net.hawkengine.services.tests;

import com.fiftyonred.mock_jedis.MockJedisPool;
import net.hawkengine.core.utilities.constants.TestsConstants;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.*;
import net.hawkengine.services.MaterialDefinitionService;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.interfaces.IMaterialDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.stream.Collectors;

public class MaterialDefinitionServiceTests {
    private IPipelineDefinitionService pipelineDefinitionService;
    private IMaterialDefinitionService materialDefinitionService;

    @Before
    public void setUp(){
        MockJedisPool mockedPool = new MockJedisPool(new JedisPoolConfig(), "testMaterialDefinitionService");
        IDbRepository pipelineDefinitionRepo = new RedisRepository(PipelineDefinition.class, mockedPool);
        this.pipelineDefinitionService = new PipelineDefinitionService(pipelineDefinitionRepo);
        this.materialDefinitionService = new MaterialDefinitionService(this.pipelineDefinitionService);
    }

    @Test
    public void getById_existingObject_correctObject() {
        //Arrange
        PipelineDefinition expectedPipelineDefinition = new PipelineDefinition();
        GitMaterial expectedMaterialDefinition = new GitMaterial();
        expectedMaterialDefinition.setPipelineDefinitionId(expectedPipelineDefinition.getId());
        this.pipelineDefinitionService.add(expectedPipelineDefinition);
        this.materialDefinitionService.add(expectedMaterialDefinition);
        String expectedMessage = MaterialDefinition.class.getSimpleName() + " " + expectedMaterialDefinition.getId() + " retrieved successfully.";

        //Act
        ServiceResult actualResult = this.materialDefinitionService.getById(expectedMaterialDefinition.getId());
        MaterialDefinition actualResultObject = (MaterialDefinition) actualResult.getObject();

        //Assert
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertEquals(expectedMaterialDefinition.getId(), actualResultObject.getId());
    }

    @Test
    public void getById_nonExistingObject_correctErrorMessage() {
        //Arrange
        PipelineDefinition expectedPipelineDefinition = new PipelineDefinition();
        GitMaterial expectedMaterialDefinition = new GitMaterial();
        expectedMaterialDefinition.setPipelineDefinitionId(expectedPipelineDefinition.getId());
        String expectedMessage = MaterialDefinition.class.getSimpleName() + " not found.";

        //Act
        ServiceResult actualResult = this.materialDefinitionService.getById(expectedMaterialDefinition.getId());

        //Assert
        Assert.assertTrue(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertNull(actualResult.getObject());
    }

    @Test
    public void getAll_withExistingObjects_allObjects() {
        //Arrange
        PipelineDefinition expectedPipelineDefinition = new PipelineDefinition();
        GitMaterial firstExpectedMaterialDefinition = new GitMaterial();
        GitMaterial secondExpectedMaterialDefinition = new GitMaterial();
        firstExpectedMaterialDefinition.setPipelineDefinitionId(expectedPipelineDefinition.getId());
        firstExpectedMaterialDefinition.setName("firstMaterialDefinition");
        secondExpectedMaterialDefinition.setPipelineDefinitionId(expectedPipelineDefinition.getId());
        secondExpectedMaterialDefinition.setName("secondMaterialDefinition");
        this.pipelineDefinitionService.add(expectedPipelineDefinition);
        this.materialDefinitionService.add(firstExpectedMaterialDefinition);
        this.materialDefinitionService.add(secondExpectedMaterialDefinition);
        String expectedMessage = MaterialDefinition.class.getSimpleName() + "s retrieved successfully.";

        //Act
        ServiceResult actualResult = this.materialDefinitionService.getAll();
        List<MaterialDefinition> actualResultObject = (List<MaterialDefinition>) actualResult.getObject();
        MaterialDefinition firstActualMaterialDefinition = actualResultObject
                .stream()
                .filter(p -> p.getId().equals(firstExpectedMaterialDefinition.getId()))
                .collect(Collectors.toList())
                .get(0);

        MaterialDefinition secondActualMaterialDefinition = actualResultObject
                .stream()
                .filter(p -> p.getId().equals(secondExpectedMaterialDefinition.getId()))
                .collect(Collectors.toList())
                .get(0);

        //Assert
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertEquals(firstExpectedMaterialDefinition.getId(), firstActualMaterialDefinition.getId());
        Assert.assertEquals(secondExpectedMaterialDefinition.getId(), secondActualMaterialDefinition.getId());
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_TWO_OBJECTS, actualResultObject.size());
    }

    @Test
    public void add_nonExistingGitObject_correctObject() {
        //Arrange
        PipelineDefinition expectedPipelineDefinition = new PipelineDefinition();
        GitMaterial expectedMaterialDefinition = new GitMaterial();
        String expectedMessage = MaterialDefinition.class.getSimpleName() + " " + expectedMaterialDefinition.getId() + " added successfully.";
        expectedMaterialDefinition.setPipelineDefinitionId(expectedPipelineDefinition.getId());
        this.pipelineDefinitionService.add(expectedPipelineDefinition);

        //Act
        ServiceResult actualResult = this.materialDefinitionService.add(expectedMaterialDefinition);
        MaterialDefinition actualResultObject = (MaterialDefinition) actualResult.getObject();

        //Assert
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertEquals(expectedMaterialDefinition.getId(), actualResultObject.getId());
        Assert.assertNotNull(actualResultObject);
    }

    @Test
    public void add_nonExistingNugetObject_correctObject() {
        //Arrange
        PipelineDefinition expectedPipelineDefinition = new PipelineDefinition();
        NugetMaterial expectedMaterialDefinition = new NugetMaterial();
        String expectedMessage = MaterialDefinition.class.getSimpleName() + " " + expectedMaterialDefinition.getId() + " added successfully.";
        expectedMaterialDefinition.setPipelineDefinitionId(expectedPipelineDefinition.getId());
        this.pipelineDefinitionService.add(expectedPipelineDefinition);

        //Act
        ServiceResult actualResult = this.materialDefinitionService.add(expectedMaterialDefinition);
        MaterialDefinition actualResultObject = (MaterialDefinition) actualResult.getObject();

        //Assert
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertEquals(expectedMaterialDefinition.getId(), actualResultObject.getId());
        Assert.assertNotNull(actualResultObject);
    }

    @Test
    public void add_existingObject_correctErrorMessage() {
        //Arrange
        PipelineDefinition expectedPipelineDefinition = new PipelineDefinition();
        GitMaterial expectedMaterialDefinition = new GitMaterial();
        String expectedMessage = MaterialDefinition.class.getSimpleName() + " already exists.";
        expectedMaterialDefinition.setPipelineDefinitionId(expectedPipelineDefinition.getId());
        this.pipelineDefinitionService.add(expectedPipelineDefinition);
        this.materialDefinitionService.add(expectedMaterialDefinition);

        //Act
        ServiceResult actualResult = this.materialDefinitionService.add(expectedMaterialDefinition);

        //Assert
        Assert.assertTrue(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertNull(actualResult.getObject());
    }

    @Test
    public void add_nonExistingObjectWithSameName_correctErrorMessage() {
        //Arrange
        PipelineDefinition expectedPipelineDefinition = new PipelineDefinition();
        GitMaterial firstMaterialDefinition = new GitMaterial();
        GitMaterial secondMaterialDefinition = new GitMaterial();
        String expectedMessage = MaterialDefinition.class.getSimpleName() + " with the same name exists.";
        firstMaterialDefinition.setPipelineDefinitionId(expectedPipelineDefinition.getId());
        firstMaterialDefinition.setName("expectedMaterial");
        secondMaterialDefinition.setPipelineDefinitionId(expectedPipelineDefinition.getId());
        secondMaterialDefinition.setName(firstMaterialDefinition.getName());
        this.pipelineDefinitionService.add(expectedPipelineDefinition);
        this.materialDefinitionService.add(firstMaterialDefinition);

        //Act
        ServiceResult actualResult = this.materialDefinitionService.add(secondMaterialDefinition);
        MaterialDefinition actualResultObject = (MaterialDefinition) actualResult.getObject();

        //Assert
        Assert.assertTrue(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertEquals(firstMaterialDefinition.getName(), actualResultObject.getName());
    }

    @Test
    public void update_existingGitObject_updatedObject() {
        //Arrange
        PipelineDefinition expectedPipelineDefinition = new PipelineDefinition();
        GitMaterial expectedMaterialDefinition = new GitMaterial();
        String expectedMessage = MaterialDefinition.class.getSimpleName() + " " + expectedMaterialDefinition.getId() + " updated successfully.";
        expectedMaterialDefinition.setPipelineDefinitionId(expectedPipelineDefinition.getId());
        expectedMaterialDefinition.setName("nameBeforeUpdate");
        this.pipelineDefinitionService.add(expectedPipelineDefinition);
        this.materialDefinitionService.add(expectedMaterialDefinition);

        //Act
        String expectedName = "nameAfterUpdate";
        expectedMaterialDefinition.setName(expectedName);
        ServiceResult actualResult = this.materialDefinitionService.update(expectedMaterialDefinition);
        MaterialDefinition actualResultObject = (MaterialDefinition) actualResult.getObject();

        //Assert
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertEquals(expectedName, actualResultObject.getName());
    }

    @Test
    public void update_existingNugetObject_updatedObject() {
        //Arrange
        PipelineDefinition expectedPipelineDefinition = new PipelineDefinition();
        NugetMaterial expectedMaterialDefinition = new NugetMaterial();
        String expectedMessage = MaterialDefinition.class.getSimpleName() + " " + expectedMaterialDefinition.getId() + " updated successfully.";
        expectedMaterialDefinition.setPipelineDefinitionId(expectedPipelineDefinition.getId());
        expectedMaterialDefinition.setName("nameBeforeUpdate");
        this.pipelineDefinitionService.add(expectedPipelineDefinition);
        this.materialDefinitionService.add(expectedMaterialDefinition);

        //Act
        String expectedName = "nameAfterUpdate";
        expectedMaterialDefinition.setName(expectedName);
        ServiceResult actualResult = this.materialDefinitionService.update(expectedMaterialDefinition);
        MaterialDefinition actualResultObject = (MaterialDefinition) actualResult.getObject();

        //Assert
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertEquals(expectedName, actualResultObject.getName());
    }

    @Test
    public void update_existingObjectWithSameName_updatedObject(){
        //Arrange
        PipelineDefinition expectedPipelineDefinition = new PipelineDefinition();
        GitMaterial firstMaterialDefinition = new GitMaterial();
        GitMaterial secondMaterialDefinition = new GitMaterial();
        String expectedMessage = MaterialDefinition.class.getSimpleName() + " with the same name exists.";
        firstMaterialDefinition.setPipelineDefinitionId(expectedPipelineDefinition.getId());
        secondMaterialDefinition.setPipelineDefinitionId(expectedPipelineDefinition.getId());
        firstMaterialDefinition.setName("firstMaterial");
        secondMaterialDefinition.setName("secondMaterial");
        this.pipelineDefinitionService.add(expectedPipelineDefinition);
        this.materialDefinitionService.add(firstMaterialDefinition);
        this.materialDefinitionService.add(secondMaterialDefinition);

        //Act
        secondMaterialDefinition.setName("firstMaterial");
        ServiceResult actualResult = this.materialDefinitionService.update(secondMaterialDefinition);
        MaterialDefinition actualResultObject = (MaterialDefinition) actualResult.getObject();

        //Assert
        Assert.assertTrue(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertEquals(secondMaterialDefinition.getId(), actualResultObject.getId());
    }

    @Test
    public void update_nonExistingObject_correctErrorMessage() {
        //Arrange
        PipelineDefinition expectedPipelineDefinition = new PipelineDefinition();
        GitMaterial expectedMaterialDefinition = new GitMaterial();
        String expectedMessage = MaterialDefinition.class.getSimpleName() + " not found.";
        expectedMaterialDefinition.setPipelineDefinitionId(expectedPipelineDefinition.getId());
        expectedMaterialDefinition.setName("nameBeforeUpdate");
        this.pipelineDefinitionService.add(expectedPipelineDefinition);

        //Act
        ServiceResult actualResult = this.materialDefinitionService.update(expectedMaterialDefinition);

        //Assert
        Assert.assertTrue(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertNull(actualResult.getObject());
    }

    @Test
    public void delete_existingObject_false() {
        //Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        GitMaterial materialDefinitionToDelete = new GitMaterial();
        GitMaterial secondMaterialDefinition = new GitMaterial();
        String expectedMessage = MaterialDefinition.class.getSimpleName() + " deleted successfully.";
        materialDefinitionToDelete.setPipelineDefinitionId(pipelineDefinition.getId());
        materialDefinitionToDelete.setName("expectedMaterialDefinition");
        secondMaterialDefinition.setPipelineDefinitionId(pipelineDefinition.getId());
        secondMaterialDefinition.setName("secondMaterialDefinition");
        this.pipelineDefinitionService.add(pipelineDefinition);
        this.materialDefinitionService.add(materialDefinitionToDelete);
        this.materialDefinitionService.add(secondMaterialDefinition);

        //Act
        ServiceResult actualResult = this.materialDefinitionService.delete(materialDefinitionToDelete.getId());

        //Assert
        Assert.assertFalse(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertNull(actualResult.getObject());
    }

    @Test
    public void delete_nonExistingObject_true() {
        //Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        GitMaterial materialDefinitionToDelete = new GitMaterial();
        String expectedMessage = MaterialDefinition.class.getSimpleName() + " does not exist.";
        materialDefinitionToDelete.setPipelineDefinitionId(pipelineDefinition.getId());
        this.pipelineDefinitionService.add(pipelineDefinition);

        //Act
        ServiceResult actualResult = this.materialDefinitionService.delete(materialDefinitionToDelete.getId());

        //Assert
        Assert.assertTrue(actualResult.hasError());
        Assert.assertEquals(expectedMessage, actualResult.getMessage());
        Assert.assertNull(actualResult.getObject());
    }
}
