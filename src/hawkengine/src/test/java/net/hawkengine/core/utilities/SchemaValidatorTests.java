package net.hawkengine.core.utilities;
import net.hawkengine.core.SchemaValidator;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.MaterialType;
import net.hawkengine.model.PipelineGroup;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;


/**
 * Created by boris on 10.06.16.
 */
public class SchemaValidatorTests {
    private String expectedResult = "OK";
    private SchemaValidator validator = new SchemaValidator();

    //Arranging null objects
    //--------------------------------------------------
    private  PipelineGroup pipelineGroup;
    private MaterialDefinition materialDefinition;
    //--------------------------------------------------


    //----------------------------------------------------------------------------------------------
    //PipelineGroup TESTS
    @Test
    public void validate_PielineGroup(){
        //Arrange
        PipelineGroup pipelineGroup = new PipelineGroup();
        pipelineGroup.setName("PIPELINEGROUPBRO");

        //Act
        String actualResult = validator.validate(pipelineGroup);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_NullPipelineGroup(){
        //Arrange
        String expectedResult = "ERROR: PIPELINEGROUP IS NULL.";

        //Act
        String actualResult =  validator.validate(pipelineGroup);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_NullPipelineGroupe_Name(){
        //Arrange
        PipelineGroup pipelineGroup = new PipelineGroup();
        pipelineGroup.setName(null);
        this.validator.validate(pipelineGroup);

        String expectedResult = "ERROR: PIPELINEGROUP NAME IS NULL.";

        //Act
        String actualResult =  validator.validate(pipelineGroup);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_PipelineGroup_Name_RegExMismatch(){
        //Arrange
        PipelineGroup pipelineGroup = new PipelineGroup();
        //pipelineGroup.setName(" "); //too short name
        //pipelineGroup.setName("this name is too long");
        pipelineGroup.setName("#$!13das");
        String expectedResult = "ERROR: PIPELINEGROUP NAME IS INVALID.";

        //Act
        String actualResult = this.validator.validate(pipelineGroup);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }
    //----------------------------------------------------------------------------------------------



    //----------------------------------------------------------------------------------------------
    //MaterialDefinition TESTS
    @Test
    public void validate_MaterialDefinition() throws Exception {
        //Arrange
        MaterialDefinition materialDefinition = new MaterialDefinition();
        materialDefinition.setName("materialDefinition");
        materialDefinition.setUrl("materialUrl");
        materialDefinition.setType(MaterialType.GIT);
        materialDefinition.setPipelineName("materialPipeline");

        //Act
        String actualResult = this.validator.validate(materialDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(this.expectedResult,actualResult);
    }


    @Test
    public void validate_MaterialDefinition_Null(){
        //Arrange
        String expectedResult = "ERROR: Material Definition is NULL";

        //Act
        String actualResult =  validator.validate(this.materialDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_NullMaterialDefinition_Name() throws Exception {
        //Arrange
        MaterialDefinition materialDefinition = new MaterialDefinition();
        materialDefinition.setName(null);
        this.validator.validate(materialDefinition);

        String expectedResult = "ERROR: MATERIAL DEFINITION NAME IS NULL.";

        //Act
        String actualResult =  validator.validate(materialDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_MaterialDefinition_Name_RegExMismatch() throws Exception {
        //Arrange
        MaterialDefinition materialDefinition = new MaterialDefinition();
        materialDefinition.setName("#$@#%!");
        materialDefinition.setUrl("materialUrl");
        materialDefinition.setType(MaterialType.GIT);
        materialDefinition.setPipelineName("materialPipeline");
        String expectedResult = "ERROR: MATERIAL DEFINITION NAME IS INVALID.";

        //Act
        String actualResult = this.validator.validate(materialDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_MaterialDefinition_GitUrl_Mismatch() throws Exception {
        //Arrange
        MaterialDefinition materialDefinition = new MaterialDefinition();
        materialDefinition.setName("materialDefinition");
        materialDefinition.setUrl("#$@^#&^#&!");
        materialDefinition.setType(MaterialType.GIT);
        materialDefinition.setPipelineName("materialPipeline");
        String expectedResult = "ERROR: INVALID GIT URL.";

        //Act
        String actualResult = this.validator.validate(materialDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }
    @Test
    public void validate_MaterialDefinition_NugetUrl_Mismatch() throws Exception {
        //Arrange
        MaterialDefinition materialDefinition = new MaterialDefinition();
        materialDefinition.setName("materialDefinition");
        materialDefinition.setUrl("#$@^#&^#&!");
        materialDefinition.setType(MaterialType.NUGET);
        materialDefinition.setPipelineName("materialPipeline");
        String expectedResult = "ERROR: INVALID NUGET URL.";

        //Act
        String actualResult = this.validator.validate(materialDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);

    }

    @Test
    public void validate_MaterialDefinition_NullPipeline() throws Exception {
        //Arrange
        MaterialDefinition materialDefinition = new MaterialDefinition();
        materialDefinition.setName("materialDefinition");
        materialDefinition.setUrl("materialUrl");
        materialDefinition.setType(MaterialType.NUGET);
        materialDefinition.setPipelineName(null);
        String expectedResult = "ERROR: MATERIAL PIPELINE ID CAN NOT BE NULL.";

        //Act
        String actualResult = this.validator.validate(materialDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);

    }

    @Test
    public void validate_MaterialDefinition_InvalidURL() throws Exception {
        //Arrange
        MaterialDefinition materialDefinition = new MaterialDefinition();
        materialDefinition.setName("materialDefinition");
        materialDefinition.setType(MaterialType.PIPELINE);
        materialDefinition.setUrl("INVALID URL");
        materialDefinition.setPipelineName("pipelineName");
        String expectedResult = "ERROR: MATERIAL URL IS INVALID.";

        //Act
        String actualResult = this.validator.validate(materialDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);

    }
    //----------------------------------------------------------------------------------------------

}
