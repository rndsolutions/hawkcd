package net.hawkengine.core.utilities;

import net.hawkengine.model.*;
import net.hawkengine.model.enums.MaterialType;
import net.hawkengine.model.enums.RunIf;
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

    private List material = new ArrayList();
    private List stage = new ArrayList();
    private List job = new ArrayList();
    private List execTaskList = new ArrayList();


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

    //--------------------------------------------------


    //----------------------------------------------------------------------------------------------
    //PipelineGroup TESTS
    @Test
    public void validate_PielineGroup(){
        //Arrange
        PipelineGroup pipelineGroup = new PipelineGroup();
        pipelineGroup.setName("pieline_das");

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
    public void validate_PipelineGroupeName_Null(){
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
    public void validate_PipelineGroup_Name_Invalid(){
        //Arrange
        PipelineGroup pipelineGroup = new PipelineGroup();
        pipelineGroup.setName("   ");
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
        materialDefinition.setUrl("git://host.xz/path/to/repo.git/");
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
    public void validate_PipelineDefinitionName_Null(){
        //Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
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
    public void validate_PipelineDefinitionName_Invalid(){
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
    public void validate_PipelineDefinitionMaterials_NotAdded(){
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
    public void validate_PipelineDefinitionStages_NotAdded(){
        //Arrange
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("pipelineDefinition");
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
    public void validate_StageDefinitionName_Null(){
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
    public void validate_StageDefinitionName_Invalid(){
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
    public void validate_StageDefinitionJob_Null(){
        //Arrange
        StageDefinition stageDefinition = new StageDefinition();
        stageDefinition.setName("stageDefinitionName");
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
    public void validate_JobDefinitionName_Null(){
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
    public void validate_JobDefinitionName_Invalid(){
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
    public void validate_JobDefinitionTask_Null(){
        //Arrange
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setName("jobDefinitionName");
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
    public void validate_TaskDefinitionEXEC_Null(){
        //Arrange
        TaskDefinition taskDefinition = new ExecTask();
        taskDefinition.setName("execDefinition");
        taskDefinition.setRunIfCondition(RunIf.PASSED);
        String expectedResult = "ERROR: TASK COMMAND IS NULL.";

        //Act
        String actualResult = this.validator.validate(taskDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }


    @Test
    public void validate_TaskDefinition_Null(){
        //Arrange
        String expectedResult = "ERROR: TASK TYPE IS NULL.";

        //Act
        String actualResult = this.validator.validate(taskDefinition);

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
    public void validate_ExecTaskCommnad_Null(){
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
    public void validate_ExecTaskArgumentList_Null(){
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
    public void validate_ExecTaskArgumentList_Empty(){
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
    public void validate_FetchArtifactTaskPipelineName_Null(){
        //Arrange
        FetchArtifactTask fetchArtifactTask = new FetchArtifactTask();
        fetchArtifactTask.setName("TaksDefinitionName");
        fetchArtifactTask.setRunIfCondition(RunIf.PASSED);
        String expectedResult = "ERROR: FETCH ARTIFACT PIPELINE NAME IS NULL.";

        //Act
        String actualResult = this.validator.validate(fetchArtifactTask);

        //Assert
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validate_FetchArtifactTaskStage_NameNull(){
        //Arrange
        FetchArtifactTask fetchArtifactTask = new FetchArtifactTask();
        fetchArtifactTask.setName("TaksDefinitionName");
        fetchArtifactTask.setRunIfCondition(RunIf.PASSED);
        fetchArtifactTask.setPipeline("pipelineName");
        String expectedResult = "ERROR: FETCH ARTIFACT STAGE NAME IS NULL.";

        //Act
        String actualResult = this.validator.validate(fetchArtifactTask);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validate_FetchArtifactTaskJob_NameNull(){
        //Arrange
        FetchArtifactTask fetchArtifactTask = new FetchArtifactTask();
        fetchArtifactTask.setName("TaksDefinitionName");
        fetchArtifactTask.setRunIfCondition(RunIf.PASSED);
        fetchArtifactTask.setPipeline("pipelineName");
        fetchArtifactTask.setStage("stageName");
        String expectedResult = "ERROR: FETCH ARTIFACT JOB NAME IS NULL.";

        //Act
        String actualResult = this.validator.validate(fetchArtifactTask);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validate_FetchArtifactTaskTaskFolder_Null(){
        //Arrange
        FetchArtifactTask fetchArtifactTask = new FetchArtifactTask();
        fetchArtifactTask.setName("TaksDefinitionName");
        fetchArtifactTask.setRunIfCondition(RunIf.PASSED);
        fetchArtifactTask.setPipeline("pipelineName");
        fetchArtifactTask.setStage("stageName");
        fetchArtifactTask.setJob("jobNae");
        String expectedResult = "ERROR: FETCH ARTIFACT TASK SOURCE FOLDER IS NULL.";

        //Act
        String actualResult = this.validator.validate(fetchArtifactTask);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void validate_FetchArtifactTaskDestinationFolder_Null(){
        //Arrange
        FetchArtifactTask fetchArtifactTask = new FetchArtifactTask();
        fetchArtifactTask.setName("TaksDefinitionName");
        fetchArtifactTask.setRunIfCondition(RunIf.PASSED);
        fetchArtifactTask.setPipeline("pipelineName");
        fetchArtifactTask.setStage("stageName");
        fetchArtifactTask.setJob("jobNae");
        fetchArtifactTask.setSource("sourcefolder");
        String expectedResult = "ERROR: FETCH ARTIFACT TASK DESTINATION FOLDER IS NULL.";

        //Act
        String actualResult = this.validator.validate(fetchArtifactTask);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
    }
//--------------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------
    //UploadArtifactTask
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
    public void validate_UploadArtifactTaskSource_Null(){
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
    public void validate_UploadArtifactTaskDestination_Null(){
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
//--------------------------------------------------------------------------------------------------



    //----------------------------------------------------------------------------------------------
    //FetchMaterialTask
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
    public void validate_FetchMaterialtTaskMaterialName_Null(){
        //Arrange
        FetchMaterialTask fetchMaterialTask = new FetchMaterialTask();
        fetchMaterialTask.setName("definitionName");
        fetchMaterialTask.setRunIfCondition(RunIf.PASSED);
        String expectedResult = "ERROR: FETCH TASK MATERIAL NAME IS NULL.";

        //Arrange
        String actualResult = this.validator.validate(fetchMaterialTask);

        //Assert
        assertEquals(expectedResult,actualResult);

    }

    @Test
    public void validate_FetchMaterialtTaskPipelineName_Null(){
        //Arrange
        FetchMaterialTask fetchMaterialTask = new FetchMaterialTask();
        fetchMaterialTask.setName("definitionName");
        fetchMaterialTask.setRunIfCondition(RunIf.PASSED);
        fetchMaterialTask.setMaterialName("fetchMaterial");
        String expectedResult = "ERROR: FETCH MATERIAL PIPELINE NAME IS NULL.";

        //Arrange
        String actualResult = this.validator.validate(fetchMaterialTask);

        //Assert
        assertEquals(expectedResult,actualResult);

    }

    @Test
    public void validate_FetchMaterialtTaskSourceFolder_Null(){
        //Arrange
        FetchMaterialTask fetchMaterialTask = new FetchMaterialTask();
        fetchMaterialTask.setName("definitionName");
        fetchMaterialTask.setRunIfCondition(RunIf.PASSED);
        fetchMaterialTask.setMaterialName("fetchMaterial");
        fetchMaterialTask.setPipelineName("taskPipeline");
        String expectedResult = "ERROR: FETCH MATERIAL TASK SOURCE FOLDER IS NULL.";

        //Arrange
        String actualResult = this.validator.validate(fetchMaterialTask);

        //Assert
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_FetchMaterialtTaskDestinationFolder_Null(){
        //Arrange
        FetchMaterialTask fetchMaterialTask = new FetchMaterialTask();
        fetchMaterialTask.setName("definitionName");
        fetchMaterialTask.setRunIfCondition(RunIf.PASSED);
        fetchMaterialTask.setMaterialName("fetchMaterial");
        fetchMaterialTask.setPipelineName("taskPipeline");
        fetchMaterialTask.setSource("sourceFolder");
        String expectedResult = "ERROR: FETCH MATERIAL TASK DESTINATION FOLDER IS NULL.";

        //Arrange
        String actualResult = this.validator.validate(fetchMaterialTask);

        //Assert
        assertEquals(expectedResult,actualResult);

    }

//--------------------------------------------------------------------------------------------------



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
    public void validate_AgentName_Null(){
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
    public void validate_AgentName_Invalid(){
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
    public void validate_AgentHostname_Null(){
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
    public void validate_AgentIPAdress_Null(){
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
    public void validate_AgentRootPath_Null(){
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
    public void validate_AgentOS_Null(){
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
    public void validate_AgentEnvironment(){
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
        materialDefinition.setUrl("http://dakljsdla.com/kjdlas.git");


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
    public void validate_MaterialDefinitionName_Null() throws Exception {
        //Arrange
        MaterialDefinition materialDefinition = new MaterialDefinition();
        materialDefinition.setName(null);
        String expectedResult = "ERROR: MATERIAL DEFINITION NAME IS NULL.";

        //Act
        String actualResult =  validator.validate(materialDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_MaterialDefinitionName_Invalid() throws Exception {
        //Arrange
        MaterialDefinition materialDefinition = new MaterialDefinition();
        materialDefinition.setName("#$@#%!");
        materialDefinition.setUrl("materialUrl");
        materialDefinition.setType(MaterialType.GIT);
        String expectedResult = "ERROR: MATERIAL DEFINITION NAME IS INVALID.";

        //Act
        String actualResult = this.validator.validate(materialDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void validate_MaterialDefinitionGit_UrlMismatch() throws Exception {
        //Arrange
        MaterialDefinition materialDefinition = new MaterialDefinition();
        materialDefinition.setName("materialDefinition");
        materialDefinition.setUrl("#$@^#&^#&!");
        materialDefinition.setType(MaterialType.GIT);
        String expectedResult = "ERROR: INVALID GIT URL.";

        //Act
        String actualResult = this.validator.validate(materialDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }
    @Test
    public void validate_MaterialDefinitionNuget_UrlMismatch() throws Exception {
        //Arrange
        MaterialDefinition materialDefinition = new MaterialDefinition();
        materialDefinition.setName("materialDefinition");
        materialDefinition.setUrl("#$@^#&^#&!");
        materialDefinition.setType(MaterialType.NUGET);
        String expectedResult = "ERROR: INVALID NUGET URL.";

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

        String expectedResult = "ERROR: MATERIAL URL IS INVALID.";

        //Act
        String actualResult = this.validator.validate(materialDefinition);

        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult,actualResult);
    }
//--------------------------------------------------------------------------------------------------

}
