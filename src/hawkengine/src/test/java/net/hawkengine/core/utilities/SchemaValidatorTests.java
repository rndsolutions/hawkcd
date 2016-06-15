package net.hawkengine.core.utilities;
import net.hawkengine.core.SchemaValidator;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.MaterialType;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.RunIf;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.model.Task;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.TaskType;

import org.junit.Test;

import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;


/**
 * Created by boris on 10.06.16.
 */
public class SchemaValidatorTests {
    private String expectedResult = "OK";
    private SchemaValidator validator = new SchemaValidator();

    private List <MaterialDefinition> material = new ArrayList<MaterialDefinition>();
    private List <StageDefinition> stage = new ArrayList<StageDefinition>();
    private List <JobDefinition> job = new ArrayList<JobDefinition>();
    private List <TaskDefinition> task = new ArrayList<TaskDefinition>();


    //Arranging null objects
    //--------------------------------------------------
    private PipelineGroup pipelineGroup;
    private PipelineDefinition pipelineDefinition;
    private StageDefinition stageDefinition;
    private JobDefinition jobDefinition;
    private TaskDefinition taskDefinition;
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
    public void validate_PipelineGroup_Null(){
        //Arrange
        String expectedResult = "ERROR: PIPELINEGROUP IS NULL.";

        //Act
        String actualResult =  validator.validate(this.pipelineGroup);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_PipelineGroupe_Name_Null(){
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
//--------------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    //PipelineDefinitionDefinition TESTS
    @Test
    public void validate_PipelineDefinition(){
        //Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("pipelineDefinition");
        pipelineDefinition.setPipelineGroupId("pipelineGroupId");
        this.material.add(0,new MaterialDefinition());
        pipelineDefinition.setMaterials(material);
        this.stage.add(0,new StageDefinition());
        pipelineDefinition.setStageDefinitions(stage);


        //Act
        String actualResult = this.validator.validate(pipelineDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);

    }

    @Test
    public void validate_PipelineDefinition_Null(){
        //Arrange
        String expectedResult = "ERROR: PIPELINE DEFINITION IS NULL.";

        //Act
        String actualResult = this.validator.validate(this.pipelineDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_PipelineDefinition_Name_Null(){
        //Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setPipelineGroupId("pipelineGroupId");
        pipelineDefinition.setMaterials(material);
        pipelineDefinition.setStageDefinitions(stage);
        String expectedResult = "ERROR: PIPELINE DEFINITION NAME IS NULL.";

        //Act
        String actualResult = this.validator.validate(pipelineDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_PipelineDefinition_Name_RegExMismatch(){
        //Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("#%$%@");
        String expectedResult = "ERROR: PIPELINE DEFINITION NAME IS INVALID.";

        //Act
        String actualResult = this.validator.validate(pipelineDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validate_PipelineDefinition_PipelineGroup_Null(){
        //Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("pipelineDefinition");
        String expectedResult = "ERROR: PIPELINE GROUP ID IS NULL.";

        //Act
        String actualResult = this.validator.validate(pipelineDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_PipelineDefinition_Materials(){
        //Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("pipelineDefinition");
        pipelineDefinition.setPipelineGroupId("pipelineGroupId");
        pipelineDefinition.setStageDefinitions(stage);
        String expectedResult = "ERROR: PIPELINE MATERIALS NOT ADDED.";

        //Act
        String actualResult = this.validator.validate(pipelineDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_PipelineDefinition_Stages(){
        //Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("pipelineDefinition");
        pipelineDefinition.setPipelineGroupId("pipelineGroupId");
        material.add(0,new MaterialDefinition());
        pipelineDefinition.setMaterials(material);
        String expectedResult = "ERROR: STAGE NOT ADDED.";

        //Act
        String actualResult = this.validator.validate(pipelineDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);



    }
//--------------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------
    //StageDefinition TESTS
    @Test
    public void validate_StageDefinition(){
        //Arrange
        StageDefinition stageDefinition = new StageDefinition();
        stageDefinition.setName("StageDefinition");
        stageDefinition.setPipelineDefinitionId("pipelineDefinition");
        job.add(0, new JobDefinition());
        stageDefinition.setJobDefinitions(job);

        //Act
        String actualResult = this.validator.validate(stageDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_StageDefinition_Null(){
        //Arrange
        String exptectedResult = "ERROR: STAGE DEFINITION IS NULL.";

        //Act
        String actualResult = this.validator.validate(this.stageDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(exptectedResult,actualResult);

    }

    @Test
    public void validate_StageDefinition_Name_Null(){
        //Arrange
        StageDefinition stageDefinition = new StageDefinition();
        String exptectedResult = "ERROR: STAGE DEFINITION NAME IS NULL.";

        //Act
        String actualResult = this.validator.validate(stageDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(exptectedResult,actualResult);

    }

    @Test
    public void validate_StageDefinition_Name_RegExMismatch(){
        //Arrange
        StageDefinition stageDefinition = new StageDefinition();
        stageDefinition.setName("#$@%%^");
        String exptectedResult = "ERROR: STAGE DEFINITION NAME IS INVALID.";

        //Act
        String actualResult = this.validator.validate(stageDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(exptectedResult,actualResult);

    }

    @Test
    public void validate_StageDefinition_PipelineId_Null(){
        //Arrange
        StageDefinition stageDefinition = new StageDefinition();
        stageDefinition.setName("stageDefinitionName");
        String exptectedResult = "ERROR: PIPELINE DEFINITION ID IS NULL.";

        //Act
        String actualResult = this.validator.validate(stageDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(exptectedResult,actualResult);

    }

    @Test
    public void validate_StageDefinition_Job_Null(){
        //Arrange
        StageDefinition stageDefinition = new StageDefinition();
        stageDefinition.setName("stageDefinitionName");
        stageDefinition.setPipelineDefinitionId("pipelineDefinitionId");
        String exptectedResult = "ERROR: JOB NOT ADDED.";

        //Act
        String actualResult = this.validator.validate(stageDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(exptectedResult,actualResult);

    }
//--------------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------
    //JobDefinition

    @Test
    public void validate_JobDefinition(){
        //Arrange
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setName("jobDefinition");
        jobDefinition.setStageDefinitionId("stageDefinitionId");
        task.add(0, new TaskDefinition());
        jobDefinition.setTaskDefinitions(task);

        //Act
        String actualResult = this.validator.validate(jobDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(this.expectedResult,actualResult);
    }

    @Test
    public void validate_JobDefinition_Null(){
        //Arrange
        String expectedResult = "ERROR: JOB DEFINITION IS NULL.";

        //Act
        String actualResult = this.validator.validate(this.jobDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_JobDefinition_Name_Null(){
        //Arrange
        JobDefinition jobDefinition = new JobDefinition();
        String expectedResult = "ERROR: JOB DEFINITION NAME IS NULL.";

        //Act
        String actualResult = this.validator.validate(jobDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_JobDefinition_Name_RegExMismatch(){
        //Arrange
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setName("#@$@!");
        String expectedResult = "ERROR: JOB DEFINITION NAME IS INVALID.";

        //Act
        String actualResult = this.validator.validate(jobDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_JobDefinition_SageId_Null(){
        //Arrange
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setName("jobDefinitionName");
        String expectedResult = "ERROR: STAGE DEFINITION ID IS NULL.";

        //Act
        String actualResult = this.validator.validate(jobDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_JobDefinition_Task_Null(){
        //Arrange
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setName("jobDefinitionName");
        jobDefinition.setStageDefinitionId("stageDefinition");
        String expectedResult = "ERROR: TASK NOT ADDED.";

        //Act
        String actualResult = this.validator.validate(jobDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }
//--------------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------
    //TaskDefinition TESTS
    @Test
    public void validate_TaskDefinition(){
        //Arrange
        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setName("taskDefinition");
        taskDefinition.setJobDefinitionId("jobDefinitionId");
        taskDefinition.setType(TaskType.EXEC);
        taskDefinition.setRunIfCondition(RunIf.ANY);


        //Act
        String actualResult = this.validator.validate(taskDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(this.expectedResult,actualResult);

    }

    @Test
    public void validate_TaskDefinition_Null(){
        //Arrange
        String expectedResult = "ERROR: TASK DEFINITION IS NULL.";

        //Act
        String actualResult = this.validator.validate(this.taskDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_TaskDefinition_Name_Null(){
        //Arrange
        TaskDefinition taskDefinition = new TaskDefinition();
        String expectedResult = "ERROR: TASK DEFINITION NAME IS NULL.";


        //Act
        String actualResult = this.validator.validate(taskDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);

    }

    @Test
    public void validate_TaskDefinition_Name_RegExMismatch(){
        //Arrange
        TaskDefinition taskDefinition = new TaskDefinition();
        String expectedResult = "ERROR: TASK DEFINITION NAME IS NULL.";


        //Act
        String actualResult = this.validator.validate(taskDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);

    }



    //----------------------------------------------------------------------------------------------
    //MaterialDefinition TESTS
    @Test
    public void validate_MaterialDefinition() throws Exception {
        //Arrange
        MaterialDefinition materialDefinition = new MaterialDefinition();
        materialDefinition.setName("materialDefinition");
        materialDefinition.setUrl("materialUrl");
        materialDefinition.setType(MaterialType.GIT);
        materialDefinition.setPipelineDefinitionId("materialPipeline");


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
    public void validate_MaterialDefinition_Name_Null() throws Exception {
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
        materialDefinition.setPipelineDefinitionId("materialPipeline");
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
        materialDefinition.setPipelineDefinitionId("materialPipeline");
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
        materialDefinition.setPipelineDefinitionId("materialPipeline");
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
        materialDefinition.setPipelineDefinitionId(null);
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
        materialDefinition.setType(MaterialType.NONE);
        materialDefinition.setUrl("INVALID URL");
        materialDefinition.setPipelineDefinitionId("pipelineName");
        String expectedResult = "ERROR: MATERIAL URL IS INVALID.";

        //Act
        String actualResult = this.validator.validate(materialDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }
//--------------------------------------------------------------------------------------------------

}
