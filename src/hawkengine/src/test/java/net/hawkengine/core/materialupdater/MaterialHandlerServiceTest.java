package net.hawkengine.core.materialupdater;

import net.hawkengine.model.*;
import net.hawkengine.model.enums.MaterialType;
import net.hawkengine.services.MaterialService;
import net.hawkengine.services.interfaces.IMaterialService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MaterialUpdaterFactory.class)
public class MaterialHandlerServiceTest {
    private static final String MATERIAL_ONE = "MaterialOne";
    private static final String MATERIAL_TWO = "MaterialTwo";

    private IMaterialHandlerService materialHandlerService;
    private IMaterialService mockedMaterialService;
    private IMaterialUpdater mockedMaterialUpdater;

    @Before
    public void setUp() {
        this.mockedMaterialService = Mockito.mock(MaterialService.class);
        this.mockedMaterialUpdater = Mockito.mock(MaterialUpdater.class);
        this.materialHandlerService = new MaterialHandlerService(this.mockedMaterialService, this.mockedMaterialUpdater);

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
    public void checkPipelineForTriggerMaterials_oneTriggerMaterial_correctMessage() {
        // Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        List<MaterialDefinition> materialDefinitions = new ArrayList<>();
        GitMaterial gitMaterial = new GitMaterial();
        gitMaterial.setName(MATERIAL_ONE);
        gitMaterial.setPollingForChanges(true);
        materialDefinitions.add(gitMaterial);
        pipelineDefinition.setMaterials(materialDefinitions);
        String expectedResult = MATERIAL_ONE;

        // Act
        String actualResult = this.materialHandlerService.checkPipelineForTriggerMaterials(pipelineDefinition);

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void checkPipelineForTriggerMaterials_noTriggerMaterial_noMessage() {
        // Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        List<MaterialDefinition> materialDefinitions = new ArrayList<>();
        GitMaterial gitMaterial = new GitMaterial();
        gitMaterial.setName(MATERIAL_ONE);
        gitMaterial.setPollingForChanges(true);
        materialDefinitions.add(gitMaterial);
        pipelineDefinition.setMaterials(materialDefinitions);

        Mockito.when(this.mockedMaterialUpdater.areMaterialsSameVersion(Mockito.any(MaterialDefinition.class), Mockito.any(MaterialDefinition.class)))
                .thenReturn(true);

        // Act
        String actualResult = this.materialHandlerService.checkPipelineForTriggerMaterials(pipelineDefinition);

        // Assert
        Assert.assertTrue(actualResult.isEmpty());
    }

    @Test
    public void checkPipelineForTriggerMaterials_materialNotPolling_noMessage() {
        // Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        List<MaterialDefinition> materialDefinitions = new ArrayList<>();
        GitMaterial gitMaterial = new GitMaterial();
        gitMaterial.setName(MATERIAL_ONE);
        gitMaterial.setPollingForChanges(false);
        materialDefinitions.add(gitMaterial);
        pipelineDefinition.setMaterials(materialDefinitions);

        // Act
        String actualResult = this.materialHandlerService.checkPipelineForTriggerMaterials(pipelineDefinition);

        // Assert
        Assert.assertTrue(actualResult.isEmpty());
    }

    @Test
    public void updatePipelineMaterials() {
        // Arrange
        Material material = new Material();
        GitMaterial gitMaterial = new GitMaterial();
        gitMaterial.setName(MATERIAL_ONE);
        gitMaterial.setPollingForChanges(false);
        material.setMaterialDefinition(gitMaterial);

        // Act
        Material actualResult = this.materialHandlerService.updateMaterial(material);

        // Assert
        Assert.assertNotNull(actualResult.getChangeDate());
    }
}

