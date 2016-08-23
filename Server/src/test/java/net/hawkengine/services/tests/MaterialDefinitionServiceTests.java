package net.hawkengine.services.tests;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.utilities.constants.ServerMessages;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.GitMaterial;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.MaterialDefinitionService;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.interfaces.IMaterialDefinitionService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

public class MaterialDefinitionServiceTests {
    private IMaterialDefinitionService materialDefinitionService;
    private IPipelineDefinitionService mockedPipelineDefinitionService;
    private IDbRepository mockedRepository;


    @BeforeClass
    public static void setUpClass() {
        ServerConfiguration.configure();
    }

    @Before
    public void setUp() {
        this.mockedPipelineDefinitionService = Mockito.mock(PipelineDefinitionService.class);
        this.mockedRepository = Mockito.mock(IDbRepository.class);
        this.materialDefinitionService = new MaterialDefinitionService(this.mockedRepository, this.mockedPipelineDefinitionService);
    }

    @Test
    public void materialDefinitionService_instantiated_notNull() {
        // Act
        MaterialDefinitionService materialDefinitionService = new MaterialDefinitionService();

        // Assert
        Assert.assertNotNull(materialDefinitionService);
    }

    @Test
    public void getById_withValidId_correctObject() {
        // Arrange
        MaterialDefinition expectedMaterialDefinition = new GitMaterial();

        Mockito.when(this.mockedRepository.getById(Mockito.anyString())).thenReturn(expectedMaterialDefinition);

        // Act
        ServiceResult actualResult = this.materialDefinitionService.getById(expectedMaterialDefinition.getId());
        MaterialDefinition actualMaterialDefinition = (MaterialDefinition) actualResult.getObject();

        // Assert
        Assert.assertEquals(expectedMaterialDefinition.getId(), actualMaterialDefinition.getId());
        Assert.assertFalse(actualResult.hasError());
    }

    @Test
    public void getById_withInvalidId_null() {
        // Arrange
        String wrongId = UUID.randomUUID().toString();

        Mockito.when(this.mockedRepository.getById(Mockito.anyString())).thenReturn(null);

        // Act
        ServiceResult actualResult = this.materialDefinitionService.getById(wrongId);
        MaterialDefinition actualMaterialDefinition = (MaterialDefinition) actualResult.getObject();

        // Assert
        Assert.assertNull(actualMaterialDefinition);
        Assert.assertTrue(actualResult.hasError());
    }

    @Test
    public void getAll_withOneId_oneObject() {
        // Arrange
        List<MaterialDefinition> expectedMaterialDefinitions = new ArrayList<>();
        MaterialDefinition materialDefinition = new GitMaterial();
        expectedMaterialDefinitions.add(materialDefinition);

        Mockito.when(this.mockedRepository.getAll()).thenReturn(expectedMaterialDefinitions);

        // Act
        ServiceResult actualResult = this.materialDefinitionService.getAll();
        List<MaterialDefinition> actualMaterialDefinitions = (List<MaterialDefinition>) actualResult.getObject();

        // Assert
        Assert.assertEquals(expectedMaterialDefinitions.size(), actualMaterialDefinitions.size());
        Assert.assertEquals(expectedMaterialDefinitions.get(0).getId(), actualMaterialDefinitions.get(0).getId());
        Assert.assertFalse(actualResult.hasError());
    }

    @Test
    public void getAll_withNoId_noObjects() {
        // Arrange
        List<MaterialDefinition> expectedMaterialDefinitions = new ArrayList<>();

        Mockito.when(this.mockedRepository.getAll()).thenReturn(expectedMaterialDefinitions);

        // Act
        ServiceResult actualResult = this.materialDefinitionService.getAll();
        List<MaterialDefinition> actualMaterialDefinitions = (List<MaterialDefinition>) actualResult.getObject();

        // Assert
        Assert.assertEquals(expectedMaterialDefinitions.size(), actualMaterialDefinitions.size());
        Assert.assertFalse(actualResult.hasError());
    }

    @Test
    public void getAllFromPipelineDefinition_withOneId_oneObject() {
        // Arrange
        MaterialDefinition expectedMaterialDefinition = new GitMaterial();

        ServiceResult pipelineDefinitionById = new ServiceResult();
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        Set<String> materialDefinitionIds = new HashSet<>();
        materialDefinitionIds.add(expectedMaterialDefinition.getId());
        pipelineDefinition.setMaterialDefinitionIds(materialDefinitionIds);
        pipelineDefinitionById.setObject(pipelineDefinition);

        Mockito.when(this.mockedPipelineDefinitionService.getById(Mockito.anyString())).thenReturn(pipelineDefinitionById);
        Mockito.when(this.mockedRepository.getById(Mockito.anyString())).thenReturn(expectedMaterialDefinition);

        // Act
        ServiceResult actualResult = this.materialDefinitionService.getAllFromPipelineDefinition(pipelineDefinition.getId());
        List<MaterialDefinition> actualMaterialDefinitions = (List<MaterialDefinition>) actualResult.getObject();

        // Assert
        Assert.assertEquals(1, actualMaterialDefinitions.size());
        Assert.assertEquals(expectedMaterialDefinition.getId(), actualMaterialDefinitions.get(0).getId());
        Assert.assertFalse(actualResult.hasError());
    }

    @Test
    public void getAllFromPipelineDefinition_withNoIds_noObjects() {
        // Arrange
        MaterialDefinition expectedMaterialDefinition = new GitMaterial();

        ServiceResult pipelineDefinitionById = new ServiceResult();
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinitionById.setObject(pipelineDefinition);

        Mockito.when(this.mockedPipelineDefinitionService.getById(Mockito.anyString())).thenReturn(pipelineDefinitionById);
        Mockito.when(this.mockedRepository.getById(Mockito.anyString())).thenReturn(expectedMaterialDefinition);

        // Act
        ServiceResult actualResult = this.materialDefinitionService.getAllFromPipelineDefinition(pipelineDefinition.getId());
        List<MaterialDefinition> actualMaterialDefinitions = (List<MaterialDefinition>) actualResult.getObject();

        // Assert
        Assert.assertEquals(0, actualMaterialDefinitions.size());
        Assert.assertFalse(actualResult.hasError());
    }

    @Test
    public void add_nonExistingObject_correctObject() {
        // Arrange
        MaterialDefinition expectedMaterialDefinition = new GitMaterial();

        Mockito.when(this.mockedRepository.add(Mockito.any(MaterialDefinition.class))).thenReturn(expectedMaterialDefinition);

        // Act
        ServiceResult actualResult = this.materialDefinitionService.add(expectedMaterialDefinition);
        MaterialDefinition actualMaterialDefinition = (MaterialDefinition) actualResult.getObject();

        // Assert
        Assert.assertEquals(expectedMaterialDefinition.getId(), actualMaterialDefinition.getId());
        Assert.assertFalse(actualResult.hasError());
    }

    @Test
    public void add_existingObject_null() {
        // Arrange
        MaterialDefinition materialDefinition = new GitMaterial();

        Mockito.when(this.mockedRepository.add(Mockito.any(MaterialDefinition.class))).thenReturn(null);

        // Act
        ServiceResult actualResult = this.materialDefinitionService.add(materialDefinition);
        MaterialDefinition actualMaterialDefinition = (MaterialDefinition) actualResult.getObject();

        // Assert
        Assert.assertNull(actualMaterialDefinition);
        Assert.assertTrue(actualResult.hasError());
    }

    @Test
    public void update_existingObject_correctObject() {
        // Arrange
        MaterialDefinition expectedMaterialDefinition = new GitMaterial();

        Mockito.when(this.mockedRepository.update(Mockito.any(MaterialDefinition.class))).thenReturn(expectedMaterialDefinition);

        // Act
        ServiceResult actualResult = this.materialDefinitionService.update(expectedMaterialDefinition);
        MaterialDefinition actualMaterialDefinition = (MaterialDefinition) actualResult.getObject();

        // Assert
        Assert.assertEquals(expectedMaterialDefinition.getId(), actualMaterialDefinition.getId());
        Assert.assertFalse(actualResult.hasError());
    }

    @Test
    public void update_existingObject_null() {
        // Arrange
        MaterialDefinition materialDefinition = new GitMaterial();

        Mockito.when(this.mockedRepository.update(Mockito.any(MaterialDefinition.class))).thenReturn(null);

        // Act
        ServiceResult actualResult = this.materialDefinitionService.update(materialDefinition);
        MaterialDefinition actualMaterialDefinition = (MaterialDefinition) actualResult.getObject();

        // Assert
        Assert.assertNull(actualMaterialDefinition);
        Assert.assertTrue(actualResult.hasError());
    }

    @Test
    public void delete_withValidId_deletedObject() {
        // Arrange
        MaterialDefinition expectedMaterialDefinition = new GitMaterial();

        ServiceResult allPipelineDefinitions = new ServiceResult();
        List<PipelineDefinition> pipelineDefinitions = new ArrayList<>();
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinitions.add(pipelineDefinition);
        allPipelineDefinitions.setObject(pipelineDefinitions);
        Mockito.when(this.mockedPipelineDefinitionService.getAll()).thenReturn(allPipelineDefinitions);
        Mockito.when(this.mockedRepository.delete(Mockito.anyString())).thenReturn(expectedMaterialDefinition);

        // Act
        ServiceResult actualResult = this.materialDefinitionService.delete(expectedMaterialDefinition.getId());
        MaterialDefinition actualMaterialDefinition = (MaterialDefinition) actualResult.getObject();

        // Assert
        Assert.assertEquals(expectedMaterialDefinition.getId(), actualMaterialDefinition.getId());
        Assert.assertFalse(actualResult.hasError());
    }

    @Test
    public void delete_withInvalidId_null() {
        // Arrange
        MaterialDefinition expectedMaterialDefinition = new GitMaterial();

        ServiceResult allPipelineDefinitions = new ServiceResult();
        allPipelineDefinitions.setObject(new ArrayList<PipelineDefinition>());
        Mockito.when(this.mockedPipelineDefinitionService.getAll()).thenReturn(allPipelineDefinitions);
        Mockito.when(this.mockedRepository.delete(Mockito.anyString())).thenReturn(null);

        // Act
        ServiceResult actualResult = this.materialDefinitionService.delete(expectedMaterialDefinition.getId());
        MaterialDefinition actualMaterialDefinition = (MaterialDefinition) actualResult.getObject();

        // Assert
        Assert.assertNull(actualMaterialDefinition);
        Assert.assertTrue(actualResult.hasError());
    }

    @Test
    public void delete_assignedToOnePipeline_correctMessage() {
        // Arrange
        MaterialDefinition expectedMaterialDefinition = new GitMaterial();

        ServiceResult allPipelineDefinitions = new ServiceResult();
        List<PipelineDefinition> pipelineDefinitions = new ArrayList<>();
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        Set<String> materialDefinitionIds = new HashSet<>();
        materialDefinitionIds.add(expectedMaterialDefinition.getId());
        pipelineDefinition.setMaterialDefinitionIds(materialDefinitionIds);
        pipelineDefinitions.add(pipelineDefinition);
        allPipelineDefinitions.setObject(pipelineDefinitions);
        Mockito.when(this.mockedPipelineDefinitionService.getAll()).thenReturn(allPipelineDefinitions);

        String expectedMessage = MaterialDefinition.class.getSimpleName() + " " + String.format(ServerMessages.COULD_NOT_BE_DELETED, expectedMaterialDefinition.getId()) + ".";

        // Act
        ServiceResult actualResult = this.materialDefinitionService.delete(expectedMaterialDefinition.getId());
        MaterialDefinition actualMaterialDefinition = (MaterialDefinition) actualResult.getObject();

        // Assert
        Assert.assertNull(actualMaterialDefinition);
        Assert.assertTrue(actualResult.hasError());
        Assert.assertEquals(actualResult.getMessage(), expectedMessage);
    }
}
