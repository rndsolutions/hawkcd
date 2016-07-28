package net.hawkengine.core.materialhandler;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.materialhandler.materialupdaters.IMaterialUpdater;
import net.hawkengine.core.materialhandler.materialupdaters.MaterialUpdater;
import net.hawkengine.core.materialhandler.materialupdaters.MaterialUpdaterFactory;
import net.hawkengine.model.GitMaterial;
import net.hawkengine.model.Material;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.enums.MaterialType;
import net.hawkengine.services.MaterialDefinitionService;
import net.hawkengine.services.MaterialService;
import net.hawkengine.services.interfaces.IMaterialDefinitionService;
import net.hawkengine.services.interfaces.IMaterialService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MaterialUpdaterFactory.class})
public class MaterialHandlerServiceTest {
    private static final String MATERIAL_ONE = "MaterialOne";

    private IMaterialHandlerService materialHandlerService;
    private IMaterialDefinitionService mockedMaterialDefinitionService;
    private IMaterialService mockedMaterialService;
    private IMaterialUpdater mockedMaterialUpdater;

    @BeforeClass
    public static void setUpClass() {
        ServerConfiguration.configure();
    }

    @Before
    public void setUp() {
        this.mockedMaterialDefinitionService = Mockito.mock(MaterialDefinitionService.class);
        this.mockedMaterialService = Mockito.mock(MaterialService.class);
        this.mockedMaterialUpdater = Mockito.mock(MaterialUpdater.class);
        this.materialHandlerService = new MaterialHandlerService(this.mockedMaterialDefinitionService, this.mockedMaterialService, this.mockedMaterialUpdater);

        PowerMockito.mockStatic(MaterialUpdaterFactory.class);
        Mockito.when(MaterialUpdaterFactory.create(Mockito.any(MaterialType.class)))
                .thenReturn((MaterialUpdater) this.mockedMaterialUpdater);

        Mockito.when(this.mockedMaterialUpdater.getLatestMaterialVersion(Mockito.any(MaterialDefinition.class)))
                .thenReturn(new GitMaterial());
        Mockito.when(this.mockedMaterialUpdater.areMaterialsSameVersion(Mockito.any(MaterialDefinition.class), Mockito.any(MaterialDefinition.class)))
                .thenReturn(false);

        ServiceResult latestMaterialServiceResult = new ServiceResult();
        latestMaterialServiceResult.setObject(new Material());
        Mockito.when(this.mockedMaterialService.getLatestMaterial(Mockito.any(String.class))).thenReturn(latestMaterialServiceResult);
    }

    @Test
    public void materialHandlerService_instantiated_notNull() {
        // Act
        this.materialHandlerService = new MaterialHandlerService();

        // Assert
        Assert.assertNotNull(this.materialHandlerService);
    }

//    @Test
//    public void checkPipelineForTriggerMaterials_oneTriggerMaterial_correctMessage() {
//        // Arrange
//        PipelineDefinition pipelineDefinition = new PipelineDefinition();
//        List<MaterialDefinition> materialDefinitions = new ArrayList<>();
//        GitMaterial gitMaterial = new GitMaterial();
//        gitMaterial.setName(MATERIAL_ONE);
//        gitMaterial.setPollingForChanges(true);
//        materialDefinitions.add(gitMaterial);
//        pipelineDefinition.setMaterialDefinitions(materialDefinitions);
//        String expectedResult = MATERIAL_ONE;
//
//        // Act
//        String actualResult = this.materialHandlerService.checkPipelineForTriggerMaterials(pipelineDefinition);
//
//        // Assert
//        Assert.assertEquals(expectedResult, actualResult);
//    }
//
//    @Test
//    public void checkPipelineForTriggerMaterials_noTriggerMaterial_noMessage() {
//        // Arrange
//        PipelineDefinition pipelineDefinition = new PipelineDefinition();
//        List<MaterialDefinition> materialDefinitions = new ArrayList<>();
//        GitMaterial gitMaterial = new GitMaterial();
//        gitMaterial.setName(MATERIAL_ONE);
//        gitMaterial.setPollingForChanges(true);
//        materialDefinitions.add(gitMaterial);
//        pipelineDefinition.setMaterialDefinitions(materialDefinitions);
//
//        Mockito.when(this.mockedMaterialUpdater.areMaterialsSameVersion(Mockito.any(MaterialDefinition.class), Mockito.any(MaterialDefinition.class)))
//                .thenReturn(true);
//
//        // Act
//        String actualResult = this.materialHandlerService.checkPipelineForTriggerMaterials(pipelineDefinition);
//
//        // Assert
//        Assert.assertTrue(actualResult.isEmpty());
//    }
//
//    @Test
//    public void checkPipelineForTriggerMaterials_materialNotPolling_noMessage() {
//        // Arrange
//        PipelineDefinition pipelineDefinition = new PipelineDefinition();
//        List<MaterialDefinition> materialDefinitions = new ArrayList<>();
//        GitMaterial gitMaterial = new GitMaterial();
//        gitMaterial.setName(MATERIAL_ONE);
//        gitMaterial.setPollingForChanges(false);
//        materialDefinitions.add(gitMaterial);
//        pipelineDefinition.setMaterialDefinitions(materialDefinitions);
//
//        // Act
//        String actualResult = this.materialHandlerService.checkPipelineForTriggerMaterials(pipelineDefinition);
//
//        // Assert
//        Assert.assertTrue(actualResult.isEmpty());
//    }
//
//    @Test
//    public void checkPipelineForTriggerMaterials_couldNotGetLatestMaterial_noMessage() {
//        // Arrange
//        GitMaterial gitMaterial = Mockito.mock(GitMaterial.class);
//        GitMaterial latestGitMaterial = Mockito.mock(GitMaterial.class);
//        Mockito.when(latestGitMaterial.getErrorMessage()).thenReturn("errorMessage");
//
//        PipelineDefinition pipelineDefinition = new PipelineDefinition();
//        List<MaterialDefinition> materialDefinitions = new ArrayList<>();
//        ServiceResult updatedMaterialDefinition = new ServiceResult();
//        updatedMaterialDefinition.setMessage("");
//        updatedMaterialDefinition.setError(false);
//        updatedMaterialDefinition.setObject(gitMaterial);
//
//        Mockito.when(gitMaterial.getErrorMessage()).thenReturn("first").thenReturn("second");
//        Mockito.when(this.mockedMaterialDefinitionService.updateMaterialDefinition(Mockito.any(MaterialDefinition.class))).thenReturn(updatedMaterialDefinition);
//        Mockito.when(this.mockedMaterialUpdater.getLatestMaterialVersion(Mockito.any(MaterialDefinition.class))).thenReturn(latestGitMaterial);
//        Mockito.when(gitMaterial.isPollingForChanges()).thenReturn(true);
//
//        materialDefinitions.add(gitMaterial);
//        pipelineDefinition.setMaterialDefinitions(materialDefinitions);
//
//        // Act
//        String actualResult = this.materialHandlerService.checkPipelineForTriggerMaterials(pipelineDefinition);
//
//        // Assert
//        Assert.assertTrue(actualResult.isEmpty());
//    }
//
//    @Test
//    public void checkPipelineForTriggerMaterials_noPreviousMaterialInDb_triggerMessage() {
//        // Arrange
//        PipelineDefinition pipelineDefinition = new PipelineDefinition();
//        List<MaterialDefinition> materialDefinitions = new ArrayList<>();
//        GitMaterial gitMaterial = new GitMaterial();
//        gitMaterial.setName(MATERIAL_ONE);
//        gitMaterial.setPollingForChanges(true);
//        materialDefinitions.add(gitMaterial);
//        pipelineDefinition.setMaterialDefinitions(materialDefinitions);
//        String expectedResult = MATERIAL_ONE;
//
//        ServiceResult latestMaterialServiceResult = new ServiceResult();
//        latestMaterialServiceResult.setObject(null);
//        Mockito.when(this.mockedMaterialService.getLatestMaterial(Mockito.any(String.class))).thenReturn(latestMaterialServiceResult);
//
//        // Act
//        String actualResult = this.materialHandlerService.checkPipelineForTriggerMaterials(pipelineDefinition);
//
//        // Assert
//        Assert.assertEquals(expectedResult, actualResult);
//    }

    @Test
    public void updateMaterial_newVersion_materialUpdated() {
        // Arrange
        Material material = new Material();
        GitMaterial gitMaterial = new GitMaterial();
        material.setMaterialDefinition(gitMaterial);

        // Act
        Material actualResult = this.materialHandlerService.updateMaterial(material);

        // Assert
        Assert.assertNotNull(actualResult.getChangeDate());
        Assert.assertTrue(actualResult.isUpdated());
    }

    @Test
    public void updateMaterial_noNewVersion_materialNotUpdated() {
        // Arrange
        Material material = new Material();
        GitMaterial gitMaterial = new GitMaterial();
        material.setMaterialDefinition(gitMaterial);

        Mockito.when(this.mockedMaterialUpdater.areMaterialsSameVersion(Mockito.any(MaterialDefinition.class), Mockito.any(MaterialDefinition.class)))
                .thenReturn(true);

        // Act
        Material actualResult = this.materialHandlerService.updateMaterial(material);

        // Assert
        Assert.assertFalse(actualResult.isUpdated());
    }

    @Test
    public void updateMaterial_couldNotGetLatestMaterial_null() {
        // Arrange
        Material material = new Material();
        GitMaterial gitMaterial = Mockito.mock(GitMaterial.class);
        material.setMaterialDefinition(gitMaterial);
        ServiceResult updatedServiceResult = new ServiceResult();
        updatedServiceResult.setError(true);
        updatedServiceResult.setMessage("");
        updatedServiceResult.setObject(gitMaterial);
        Mockito.when(gitMaterial.getErrorMessage()).thenReturn("not empty").thenReturn("new message");
        Mockito.when(this.mockedMaterialUpdater.getLatestMaterialVersion(Mockito.any(MaterialDefinition.class)))
                .thenReturn(null);
        Mockito.when(this.mockedMaterialDefinitionService.update(Mockito.any(MaterialDefinition.class))).thenReturn(updatedServiceResult);

        // Act
        Material actualResult = this.materialHandlerService.updateMaterial(material);

        // Assert
        Assert.assertNull(actualResult);
    }

    @Test
    public void updateMaterial_noPreviousMaterialInDb_materialUpdated() {
        // Arrange
        Material material = new Material();
        GitMaterial gitMaterial = new GitMaterial();
        material.setMaterialDefinition(gitMaterial);

        ServiceResult latestMaterialServiceResult = new ServiceResult();
        latestMaterialServiceResult.setObject(null);
        Mockito.when(this.mockedMaterialService.getLatestMaterial(Mockito.any(String.class))).thenReturn(latestMaterialServiceResult);

        // Act
        Material actualResult = this.materialHandlerService.updateMaterial(material);

        // Assert
        Assert.assertNotNull(actualResult.getChangeDate());
        Assert.assertTrue(actualResult.isUpdated());
    }
}

