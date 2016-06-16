package net.hawkengine.core;
import net.hawkengine.model.Agent;
import net.hawkengine.model.ExecTask;
import net.hawkengine.model.FetchArtifactTask;
import net.hawkengine.model.FetchMaterialTask;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.UploadArtifactTask;
import net.hawkengine.model.enums.MaterialType;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.enums.TaskType;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by boris on 09.06.16.
 */
public class SchemaValidator {
    private String message = "OK";
    private static final String NAME_PATTERN = "[A-Za-z]{3,20}"; //TODO enter name pattern
    private static final String GIT_PATTERN = "(git|ssh|http(s)?)(:)(\\/\\/)";
    private static final String NUGET_PATTERN = "[a-z]{5,50}"; //TODO define git url pattern
    //TODO to be handaled non-required insertation of Environments into PipelineDefinition,
    //TODO StageDefinition.

    //PIPELINEGROUP
    public String validate(PipelineGroup pipelineGroup) {
        if (pipelineGroup != null) {
            String name = pipelineGroup.getName();
            if (name == null) {
                return this.message = "ERROR: PIPELINEGROUP NAME IS NULL.";
            }

            if(isValidRegEx(name,NAME_PATTERN) == false){
                return this.message = "ERROR: PIPELINEGROUP NAME IS INVALID.";
            }
        }
        else {
            return this.message = "ERROR: PIPELINEGROUP IS NULL.";
        }
        return message;
    }

    //PIPELINEDEFINITION
    public String validate(PipelineDefinition pipelineDefinition){
        if (pipelineDefinition != null){
            String pipelineDefinitionName = pipelineDefinition.getName();
            if (pipelineDefinitionName == null){
                return this.message = "ERROR: PIPELINE DEFINITION NAME IS NULL.";
            }

            if (this.isValidRegEx(pipelineDefinitionName, NAME_PATTERN) == false){
                return this.message = "ERROR: PIPELINE DEFINITION NAME IS INVALID.";
            }

            int pipelineMaterial = pipelineDefinition.getMaterials().size();
            if (pipelineMaterial <= 0) {
                return this.message = "ERROR: PIPELINE MATERIALS NOT ADDED.";
            }else {
                List<MaterialDefinition> materials = pipelineDefinition.getMaterials();
                materials.forEach(this::validate); //TODO CHECK MATERIAL VALIDATOR AGAIND
            }

            int stageDefinitionSize = pipelineDefinition.getStageDefinitions().size();
            if (stageDefinitionSize == 0) {
                return this.message = "ERROR: STAGE NOT ADDED.";
            }else {
                List<StageDefinition> stageDefinition = pipelineDefinition.getStageDefinitions();
                stageDefinition.forEach(this::validate);
            }

        } else {
            return this.message = "ERROR: PIPELINE DEFINITION IS NULL.";
        }
        return this.message;
    }

    //STAGEDEFINITION
    public String validate(StageDefinition stageDefinition){
        if(stageDefinition != null){
            String stageDefinitionName = stageDefinition.getName();

            if (stageDefinitionName == null){
                return this.message = "ERROR: STAGE DEFINITION NAME IS NULL.";
            }

            if (this.isValidRegEx(stageDefinitionName,NAME_PATTERN) == false) {
                return this.message = "ERROR: STAGE DEFINITION NAME IS INVALID.";
            }

            int jobDefinitions = stageDefinition.getJobDefinitions().size();
            if (jobDefinitions <= 0) {
                return this.message = "ERROR: STAGE DEFINITION JOB NOT ADDED.";
            }else {
                List <JobDefinition> jobDefinition = stageDefinition.getJobDefinitions();
                jobDefinition.forEach(this::validate);
            }

        }else {
            return this.message = "ERROR: STAGE DEFINITION IS NULL.";
        }
        return this.message;
    }

    //JOBDEFINITION
    public String validate (JobDefinition jobDefinition){
        if (jobDefinition != null){
            String name = jobDefinition.getName();
            if (name == null){
                return this.message = "ERROR: JOB DEFINITION NAME IS NULL.";
            }

            if (this.isValidRegEx(name, NAME_PATTERN) == false) {
                return this.message = "ERROR: JOB DEFINITION NAME IS INVALID.";
            }

            int taskDefinitions = jobDefinition.getTaskDefinitions().size();
            if (taskDefinitions <= 0) {
                return this.message = "ERROR: TASK NOT ADDED.";
            }else {
                List <TaskDefinition> taskDefinition = jobDefinition.getTaskDefinitions();
                taskDefinition.forEach(this::validate);
            }

        }else {
            return this.message = "ERROR: JOB DEFINITION IS NULL.";
        }

        return this.message;
    }

    //TASKDEFINITION
    public String validate (TaskDefinition taskDefinition) {
        if (taskDefinition != null) {
            String name = taskDefinition.getName();
            if (name == null){
                return this.message = "ERROR: TASK DEFINITION NAME IS NULL.";
            }

            if (this.isValidRegEx(name,NAME_PATTERN)){
                return this.message = "ERROR: TASK DEFINITION NAME IS INVALID.";
            }

            if (taskDefinition.getType() == TaskType.EXEC){

                this.validate((ExecTask)taskDefinition);

            } else if (taskDefinition.getType() == TaskType.FETCH_ARTIFACT){

                this.validate((FetchArtifactTask)taskDefinition);

            } else if (taskDefinition.getType() == TaskType.FETCH_MATERIAL){

                this.validate((FetchMaterialTask)taskDefinition);

            } else if (taskDefinition.getType() == TaskType.UPLOAD_ARTIFACT){

                this.validate((UploadArtifactTask)taskDefinition);

            } else {

                return this.message = "ERROR: INVALID TASK TYPE";
            }

            if (taskDefinition.getRunIfCondition() == null){
                return this.message = "ERROR: RUN IF CONDITION IS NULL.";
            }

        } else {
            return this.message = "ERROR: TASK DEFINITION IS NULL.";
        }
        return this.message;
    }

    public String validate (ExecTask execTask){
        if (execTask != null){
            String command = execTask.getCommand();
            if (command == null){
                return this.message = "ERROR: TASK COMMAND IS NULL.";
            }

            List arguments = execTask.getArguments();
            if (arguments == null) {
                return this.message = "ERROR TASK ARGUMENTS IS NULL.";
            }

        }else {
            return this.message = "ERROR: EXEC TASK IS NULL.";
        }

        return this.message;
    }

    public String validate (FetchArtifactTask fetchArtifactTask){
        if (fetchArtifactTask != null) {
            String pipelineName = fetchArtifactTask.getPipeline();
            if (pipelineName == null){
                return this.message = "ERROR: FETCH ARTIFACT PIPELINE NAME IS NULL.";
            }

            String pipelineStage = fetchArtifactTask.getStage();
            if (pipelineStage == null){
                return this.message = "ERROR: FETCH ARTIFACT STAGE NAME IS NULL.";
            }

            String pipelineJob = fetchArtifactTask.getJob();
            if (pipelineJob == null){
                return this.message = "ERROR: FETCH ARTIFACT JOB NAME IS NULL.";
            }

            String source = fetchArtifactTask.getSource();
            if (source == null){
                return this.message = "ERROR: FETCH ARTIFACT TASK SOURCE FOLDER IS NULL.";
            }

            String destination = fetchArtifactTask.getDestination();
            if (destination == null){
                return this.message = "ERROR: FETCH ARTIFACT TASK DESTINATION FOLDER IS NULL.";
            }

        }else {
            return this.message = "ERROR: FETCH ARTIFACT TASK IS NULL.";
        }

        return this.message;
    }

    public String validate (FetchMaterialTask fetchMaterialTask){
        if (fetchMaterialTask != fetchMaterialTask){
            String materialName = fetchMaterialTask.getMaterialName();
            if (materialName == null){
                return this.message = "ERROR: FETCH TASK MATERIAL NAME IS NULL.";
            }

            String pipelineName = fetchMaterialTask.getPipelineName();
            if (pipelineName == null){
                return this.message = "ERROR: FETCH MATERLIAL PIPELINE NAME IS NULL.";
            }

            String source = fetchMaterialTask.getSource();
            if (source == null){
                return this.message = "ERROR: FETCH MATERIAL TASK SOURCE FOLDER IS NULL.";
            }

            String destination = fetchMaterialTask.getDestination();
            if (destination == null){
                return this.message = "ERROR: FETCH MATERIAL TASK DESTINATION FOLDER IS NULL.";
            }

        }else {
            return this.message = "ERROR: FETCH MATERIAL TASK IS NULL.";
        }

        //TODO MATERIAL SPECIFIC DETAILS TO BE HANDALED.
        return this.message;
    }

    public String validate (UploadArtifactTask uploadArtifactTask){
        if (uploadArtifactTask != null){

            String source = uploadArtifactTask.getSource();
            if (source == null){
                return this.message = "ERROR: UPLOAD ARTIFACT TASK SOURCE FOLDER IS NULL.";
            }

            String destination = uploadArtifactTask.getDestination();
            if (destination == null){
                return this.message = "ERROR: UPLOAD ARTIFACT TASK DESTINATION FOLDER IS NULL.";
            }

        }else {
            return this.message = "ERROR: UPLOAD ARTIFACT TASK IS NULL.";
        }

        return this.message;
    }

    //MATERIAL DEFINITION
    public String validate(MaterialDefinition materialDefinition){
        if (materialDefinition != null){
            String materialName = materialDefinition.getName();
            if (materialName == null){
                return this.message = "ERROR: MATERIAL DEFINITION NAME IS NULL.";
            }

            if (this.isValidRegEx(materialName, NAME_PATTERN) == false){
                return this.message = "ERROR: MATERIAL DEFINITION NAME IS INVALID.";
            }

            String materialURL = materialDefinition.getUrl();
            if (materialURL != null){
                if(materialDefinition.getType() == MaterialType.GIT){
                    if(this.isValidRegEx(materialURL, GIT_PATTERN) == false){
                       return this.message = "ERROR: INVALID GIT URL.";
                    }
                } else if (materialDefinition.getType() == MaterialType.NUGET){
                    if (this.isValidRegEx(materialURL, NUGET_PATTERN) == false){
                       return this.message = "ERROR: INVALID NUGET URL.";
                    }
                }else {
                    return this.message = "ERROR: MATERIAL URL IS INVALID.";
                }
            } else {
                return this.message = "ERROR: Material URL is NULL.";
            }

            String materialPipeline = materialDefinition.getPipelineDefinitionId();
            if (materialPipeline == null){
                return this.message = "ERROR: MATERIAL PIPELINE ID CAN NOT BE NULL.";
            }

        } else {
            return this.message = "ERROR: Material Definition is NULL";
        }

        return this.message;
    }

    //AGENT
    public String validate(Agent agent){
        if (agent != null){
            String agentName = agent.getName();
            if (agentName == null){
                return this.message = "ERROR: AGENT NAME IS NULL.";
            }

            if (this.isValidRegEx(agentName,NAME_PATTERN) == false){
                return this.message = "ERROR: AGENT NAME IS INVALID.";
            }

            String hostname = agent.getHostName();
            if (hostname == null){
                return this.message = "ERROR: AGENT HOSTNAME IS NULL.";
            }
            String ipAdress = agent.getIpAddress();
            if (ipAdress == null){
                return this.message = "ERROR: AGENT IP ADRESS IS NULL.";
            }

            String rootPath = agent.getRootPath();
            if (rootPath == null){
                return this.message = "ERROR: AGENT ROOT PATH IS NULL.";
            }

            String os = agent.getOperatingSystem();
            if (os == null){
                return this.message = "ERROR: AGENT OPERATIONAL SYSTEM IS NULL.";
            }

            Object environment = agent.getEnvironment();
            if (environment == null){
                return this.message = "ERROR: AGENT ENVIRONMENT IS NULL.";
            }
        }
        return this.message;
    }

    private boolean isValidRegEx(String input, String string_pattern){
        Pattern pattern = Pattern.compile(string_pattern);
        Matcher matcher = pattern.matcher(input);
        boolean isMatch = matcher.matches();
        return isMatch;
    }
}
