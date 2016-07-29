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

import java.util.ArrayList;
import java.util.List;

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
    public void getAllFromPipelineDefinition_withOneId_oneObject() {
        // Arrange
        MaterialDefinition expectedMaterialDefinition = new GitMaterial();

        ServiceResult pipelineDefinitionById = new ServiceResult();
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        List<String> materialDefinitionIds = new ArrayList<>();
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
        List<String> materialDefinitionIds = new ArrayList<>();
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
