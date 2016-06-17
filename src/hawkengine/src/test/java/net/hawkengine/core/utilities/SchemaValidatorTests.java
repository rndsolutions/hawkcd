package net.hawkengine.core.utilities;
import com.sun.jmx.snmp.tasks.TaskServer;

import net.hawkengine.http.Exec;
import net.hawkengine.model.Agent;
import net.hawkengine.model.Environment;
import net.hawkengine.model.ExecTask;
import net.hawkengine.model.FetchArtifactTask;
import net.hawkengine.model.FetchMaterialTask;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.Task;
import net.hawkengine.model.UploadArtifactTask;
import net.hawkengine.model.enums.MaterialType;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.enums.RunIf;
import net.hawkengine.model.enums.TaskType;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;



/**
 * Created by boris on 10.06.16.
 */
public class SchemaValidatorTests {
    private String expectedResult = "OK";
    private SchemaValidator validator = new SchemaValidator();

    private List <MaterialDefinition> material = new ArrayList<MaterialDefinition>();
    private List <StageDefinition> stage = new ArrayList<StageDefinition>();
    private List <JobDefinition> job = new ArrayList<JobDefinition>();
   // private List <TaskDefinition> task = new ArrayList<TaskDefinition>();
    private List <TaskDefinition> execTaskList = new ArrayList<TaskDefinition>();


    //Arranging null objects
    //--------------------------------------------------
    private PipelineGroup pipelineGroup;
    private PipelineDefinition pipelineDefinition;
    private StageDefinition stageDefinition;
    private JobDefinition jobDefinition;
    private TaskDefinition taskDefinition;
    private ExecTask execTask;
    private UploadArtifactTask uploadArtifactTask;
    private MaterialDefinition materialDefinition;
    ;
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

        MaterialDefinition materialDefinition = new MaterialDefinition();
        materialDefinition.setName("materialName");
        materialDefinition.setType(MaterialType.GIT);
        materialDefinition.setUrl("http://");
        materialDefinition.setPipelineDefinitionId("MaterialPipelineId");
        this.material.add(0,materialDefinition);

        ExecTask execTask = new ExecTask();
        ArrayList arguments = new ArrayList();
        arguments.add("command1");
        execTask.setName("execTask");
        execTask.setRunIfCondition(RunIf.ANY);
        execTask.setCommand("start");
        execTask.setArguments(arguments);
        execTaskList.add(execTask);

        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setName("jobDefinition");
        jobDefinition.setTaskDefinitions(execTaskList);
        job.add(0,jobDefinition);

        StageDefinition stageDefinition = new StageDefinition();
        stageDefinition.setName("stageName");
        stageDefinition.setJobDefinitions(job);
        this.stage.add(0,stageDefinition);

        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("pipelineDefinition");
        pipelineDefinition.setStageDefinitions(stage);
        pipelineDefinition.setMaterials(this.material);

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
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setName("jobDefinition");
        ExecTask execTask = new ExecTask();
        ArrayList arguments = new ArrayList();
        arguments.add("command1");
        execTask.setName("execTask");
        execTask.setRunIfCondition(RunIf.ANY);
        execTask.setCommand("start");
        execTask.setArguments(arguments);
        execTaskList.add(execTask);
        jobDefinition.setTaskDefinitions(execTaskList);
        job.add(0,jobDefinition);;
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
    public void validate_StageDefinition_Job_Null(){
        //Arrange
        StageDefinition stageDefinition = new StageDefinition();
        stageDefinition.setName("stageDefinitionName");
        stageDefinition.setPipelineDefinitionId("pipelineDefinitionId");
        String exptectedResult = "ERROR: STAGE DEFINITION JOB NOT ADDED.";

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
        ExecTask execTask = new ExecTask();
        ArrayList arguments = new ArrayList();
        arguments.add("command1");
        execTask.setName("execTask");
        execTask.setRunIfCondition(RunIf.ANY);
        execTask.setCommand("start");
        execTask.setArguments(arguments);
        execTaskList.add(execTask);;
        jobDefinition.setTaskDefinitions(execTaskList);

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
    //Tasks TEST

    @Test
    public void validate_ExecTask(){
        //Arrange
        ExecTask execTask = new ExecTask();
        execTask.setName("execTask");
        execTask.setRunIfCondition(RunIf.PASSED);
        execTask.setCommand("kill");
        ArrayList argumentsList = new ArrayList();
        argumentsList.add("killAll");
        execTask.setArguments(argumentsList);

        //Act
        String actualReult = this.validator.validate(execTask);

        //Assert
        assertEquals(expectedResult,actualReult);
    }


    @Test
    public void validate_ExecTask_Commnad_Null(){
        //Arrange
        ExecTask execTask = new ExecTask();
        execTask.setName("taskDefinition");
        execTask.setRunIfCondition(RunIf.PASSED);
        String expectedResult = "ERROR: TASK COMMAND IS NULL.";

        //Act
        String actualReult = this.validator.validate(execTask);

        //Assert
        assertEquals(expectedResult,actualReult);
    }

    @Test
    public void validate_ExecTask_ArgumentList_Null(){
        //Arrange
        ExecTask execTask = new ExecTask();
        execTask.setName("taskDefinition");
        execTask.setRunIfCondition(RunIf.PASSED);
        execTask.setCommand("kill");
        String expectedResult = "ERROR TASK ARGUMENTS LIST IS NULL.";

        //Act
        String actualReult = this.validator.validate(execTask);

        //Assert
        assertEquals(expectedResult,actualReult);
    }

    @Test
    public void validate_ExecTask_ArgumentList_Empty(){
        //Arrange
        ExecTask execTask = new ExecTask();
        execTask.setName("taskDefinition");
        execTask.setRunIfCondition(RunIf.PASSED);
        execTask.setCommand("kill");
        execTask.setCommand("kill");
        execTask.setArguments(new ArrayList());
        String expectedResult = "ERROR: TASK ARGUMENT LIST IS EMPTY.";

        //Act
        String actualReult = this.validator.validate(execTask);

        //Assert
        assertEquals(expectedResult,actualReult);
    }

    @Test
    public void validate_FetchArtifactTask(){
        //Arrange
        FetchArtifactTask fetchArtifactTask = new FetchArtifactTask();
        fetchArtifactTask.setName("taskDefinition");
        fetchArtifactTask.setRunIfCondition(RunIf.PASSED);
        fetchArtifactTask.setPipeline("pipelineName");
        fetchArtifactTask.setStage("pipelineStage");
        fetchArtifactTask.setJob("pipelineJob");
        fetchArtifactTask.setSource("sourceFolder");
        fetchArtifactTask.setDestination("destinationFolder");


        //Arrange
        String actualResult = this.validator.validate(fetchArtifactTask);

        //Assert
        assertEquals(this.expectedResult,actualResult);

    }





    @Test
    public void validate_FetchMaterialtTask(){
        //Arrange
        FetchMaterialTask fetchMaterialTask = new FetchMaterialTask();
        fetchMaterialTask.setName("taskDefinition");
        fetchMaterialTask.setRunIfCondition(RunIf.PASSED);
        fetchMaterialTask.setMaterialName("materialName");
        fetchMaterialTask.setPipelineName("pipelineName");
        fetchMaterialTask.setSource("source");
        fetchMaterialTask.setDestination("destination");


        //Arrange
        String actualResult = this.validator.validate(fetchMaterialTask);

        //Assert
        assertEquals(this.expectedResult,actualResult);

    }



    @Test
    public void validate_UploadArtifactTask(){
        //Arrane
        UploadArtifactTask uploadArtifactTask = new UploadArtifactTask();
        uploadArtifactTask.setName("taskDefinition");
        uploadArtifactTask.setRunIfCondition(RunIf.PASSED);
        uploadArtifactTask.setSource("/sourceFolder");
        uploadArtifactTask.setDestination("/destinationFolder");

        //Act
        String actualResult = this.validator.validate(uploadArtifactTask);

        //Arrange
        assertNotNull(actualResult);
        assertEquals(this.expectedResult,actualResult);
    }


    @Test
    public void validate_UploadArtifactTask_Source(){
        //Arrane
        UploadArtifactTask uploadArtifactTask = new UploadArtifactTask();
        uploadArtifactTask.setName("taskDefinition");
        uploadArtifactTask.setRunIfCondition(RunIf.PASSED);
        String expectedResult = "ERROR: UPLOAD ARTIFACT TASK SOURCE FOLDER IS NULL.";

        //Act
        String actualResult = this.validator.validate(uploadArtifactTask);

        //Arrange
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_UploadArtifactTask_Destination(){
        //Arrane
        UploadArtifactTask uploadArtifactTask = new UploadArtifactTask();
        uploadArtifactTask.setName("taskDefinition");
        uploadArtifactTask.setRunIfCondition(RunIf.PASSED);
        uploadArtifactTask.setSource("/source");
        String expectedResult = "ERROR: UPLOAD ARTIFACT TASK DESTINATION FOLDER IS NULL.";

        //Act
        String actualResult = this.validator.validate(uploadArtifactTask);

        //Arrange
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }




    //----------------------------------------------------------------------------------------------
    //Agent TESTS
    @Test
    public void validate_Agent(){
        //Arrange
        Agent agent = new Agent();
        agent.setName("agent");
        agent.setHostName("hostname");
        agent.setIpAddress("ipadress");
        agent.setRootPath("path");
        agent.setOperatingSystem("debian");
        agent.setEnvironment(new Environment());


        //Act
        String actualResult = this.validator.validate(agent);

        //Assert
        assertNotNull(agent);
        assertEquals(expectedResult,actualResult);
    }


    @Test
    public void validate_Agent_Name_Null(){
        //Arrange
        Agent agent = new Agent();;
        String expectedResult = "ERROR: AGENT NAME IS NULL.";

        //Act
        String actualResult = this.validator.validate(agent);

        //Assert
        assertNotNull(agent);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_Agent_Name_RegExMismatch(){
        //Arrange
        Agent agent = new Agent();
        agent.setName("##$@#");
        String expectedResult = "ERROR: AGENT NAME IS INVALID.";

        //Act
        String actualResult = this.validator.validate(agent);

        //Assert
        assertNotNull(agent);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_Agent_Hostname_Null(){
        //Arrange
        Agent agent = new Agent();
        agent.setName("agentBro");
        String expectedResult = "ERROR: AGENT HOSTNAME IS NULL.";

        //Act
        String actualResult = this.validator.validate(agent);

        //Assert
        assertNotNull(agent);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_Agent_IPAdress(){
        //Arrange
        Agent agent = new Agent();
        agent.setName("agentBro");
        agent.setHostName("hostname");
        String expectedResult = "ERROR: AGENT IP ADRESS IS NULL.";

        //Act
        String actualResult = this.validator.validate(agent);

        //Assert
        assertNotNull(agent);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_Agent_RootPath(){
        //Arrange
        Agent agent = new Agent();
        agent.setName("agentBro");
        agent.setHostName("hostname");
        agent.setIpAddress("ipadress");
        String expectedResult = "ERROR: AGENT ROOT PATH IS NULL.";

        //Act
        String actualResult = this.validator.validate(agent);

        //Assert
        assertNotNull(agent);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_Agent_OS(){
        //Arrange
        Agent agent = new Agent();
        agent.setName("agentBro");
        agent.setHostName("hostname");
        agent.setIpAddress("ipadress");
        agent.setRootPath("/path");
        String expectedResult = "ERROR: AGENT OPERATIONAL SYSTEM IS NULL.";

        //Act
        String actualResult = this.validator.validate(agent);

        //Assert
        assertNotNull(agent);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_Agent_Env(){
        //Arrange
        Agent agent = new Agent();
        agent.setName("agentBro");
        agent.setHostName("hostname");
        agent.setIpAddress("ipadress");
        agent.setRootPath("/path");
        agent.setOperatingSystem("mindows");
        String expectedResult = "ERROR: AGENT ENVIRONMENT IS NULL.";

        //Act
        String actualResult = this.validator.validate(agent);

        //Assert
        assertNotNull(agent);
        assertEquals(expectedResult,actualResult);
    }






//--------------------------------------------------------------------------------------------------
    //MaterialDefinition TESTS
    @Test
    public void validate_MaterialDefinition() throws Exception {
        //Arrange
        MaterialDefinition materialDefinition = new MaterialDefinition();
        materialDefinition.setName("materialDefinition");
        materialDefinition.setType(MaterialType.GIT);
        materialDefinition.setUrl("http://");
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
