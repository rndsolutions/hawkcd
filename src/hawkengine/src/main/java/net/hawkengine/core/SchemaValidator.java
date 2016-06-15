package net.hawkengine.core;

import net.hawkengine.model.Agent;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.enums.MaterialType;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.model.TaskDefinition;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.attribute.standard.MediaSize;

/**
 * Created by boris on 09.06.16.
 */
public class SchemaValidator {
    private String message = "OK";
    private static final String NAME_PATTERN = "[A-Za-z]{3,20}"; //TODO enter name pattern
    private static final String GIT_PATTERN = "[a-zA-Z]{5,50}"; //TODO define git url pattern
    private static final String NUGET_PATTERN = "[a-zA-Z]{5,50}"; //TODO define git url pattern
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

            String pipelineGroupId = pipelineDefinition.getPipelineGroupId();
            if (pipelineGroupId == null) {
                return this.message = "ERROR: PIPELINE GROUP ID IS NULL.";
            }

            int stageDefinitionSize = pipelineDefinition.getStageDefinitions().size();
            if (stageDefinitionSize <= 0) {
                return this.message = "ERROR: STAGE NOT ADDED.";
            }else {
                List<StageDefinition> stageDefinition = pipelineDefinition.getStageDefinitions();
                stageDefinition.forEach(this::validate);
            }

            int pipelineMaterial = pipelineDefinition.getMaterials().size();
            if (pipelineMaterial <= 0) {
                return this.message = "ERROR: PIPELINE MATERIALS NOT ADDED.";
            }else {
                List<MaterialDefinition> materials = pipelineDefinition.getMaterials();
                materials.forEach(this::validate);
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
         //   String stageDefinitionPipelineId = stageDefinition.getPipelineDefinitionId();
            //int enviromentVariables = stageDefinition.getEnvironmentVariables().size();
            int jobDefinitions = stageDefinition.getJobDefinitions().size();
            if (stageDefinitionName == null){
                return this.message = "ERROR: STAGE DEFINITION NAME IS NULL.";
            }

            if (this.isValidRegEx(stageDefinitionName,NAME_PATTERN) == false) {
                return this.message = "ERROR: STAGE DEFINITION NAME IS INVALID.";
            }
         //   if (stageDefinitionPipelineId == null){
           //     return this.message = "ERROR: PIPELINE STAGE DEFINITION ID IS NULL.";
           // }

            if (jobDefinitions <= 0) {
                return this.message = "ERROR: STAGE DEFINITION JOB NOT ADDED.";
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
            String stageDefinitionId = jobDefinition.getStageDefinitionId();
            //int environmentVariables = jobDefinition.getEnvironmentVariables().size();
            int taskDefinitions = jobDefinition.getTaskDefinitions().size();

            if (name == null){
                return this.message = "ERROR: JOB DEFINITION NAME IS NULL.";
            }

            if (this.isValidRegEx(name, NAME_PATTERN) == false) {
                return this.message = "ERROR: JOB DEFINITION NAME IS INVALID.";
            }

            if (stageDefinitionId == null){
                return this.message = "ERROR: STAGE DEFINITION ID IS NULL.";
            }

            if (taskDefinitions <= 0) {
                return this.message = "ERROR: TASK NOT ADDED.";
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
            String jobDefinitionId = taskDefinition.getJobDefinitionId();

            if (name == null) {
                return this.message = "ERROR: TASK DEFINITION NAME IS NULL.";
            }

            if (this.isValidRegEx(name, NAME_PATTERN) == false) {
                return this.message = "ERROR: TASK DEFINITION NAME IS INVALID.";
            }

            if (jobDefinitionId == null) {
                return this.message = "ERROR: JOB DEFINITION ID IS NULL.";
            }

            if (this.isValidRegEx(jobDefinitionId, NAME_PATTERN) == false) {
                return this.message = "ERROR: JOB DEFINITION ID IS INVALID.";
            }

            if (taskDefinition.getType() == null) {
                return this.message = "ERROR: TASK TYPE IS NULL.";
            }

            if (taskDefinition.getRunIfCondition() == null) {
                this.message = "ERROR: RUN IF CONDITION IS NULL.";
            }

        }else {
            return this.message = "ERROR: TASK DEFINITION IS NULL.";
        }

        return this.message;
    }

    //AGENT
    public String validate(Agent agent){
        if (agent != null){
            String agentName = agent.getName();
            String hostname = agent.getHostName();
            String ipAdress = agent.getIpAddress();
            String rootPath = agent.getRootPath();
            String os = agent.getOperatingSystem();
            Object environment = agent.getEnvironment();
            if (agentName == null){
                return this.message = "ERROR: AGENT NAME IS NULL.";
            }

            if (this.isValidRegEx(agentName,NAME_PATTERN) == false){
                return this.message = "ERROR: AGENT NAME IS INVALID.";
            }

            if (hostname == null){
                return this.message = "ERROR: AGENT HOSTNAME IS NULL.";
            }

            if (ipAdress == null){
                return this.message = "ERROR: AGENT IP ADRESS IS NULL.";
            }

            if (rootPath == null){
                return this.message = "ERROR: AGENT ROOT PATH IS NULL.";
            }

            if (os == null){
                return this.message = "ERROR: AGENT OPERATIONAL SYSTEM IS NULL.";
            }

            if (environment == null){
                return this.message = "ERROR: AGENT ENVIRONMENT IS NULL.";
            }
        }
        return this.message;
    }


    //MATERIAL DEFINITION
    public String validate(MaterialDefinition materialDefinition){
        if (materialDefinition != null){
            String materialName = materialDefinition.getName();
            String materialURL = materialDefinition.getUrl();
            String materialPipeline = materialDefinition.getPipelineDefinitionId();
            if (materialName == null){
                return this.message = "ERROR: MATERIAL DEFINITION NAME IS NULL.";
            }
            if (this.isValidRegEx(materialName, NAME_PATTERN) == false){
                return this.message = "ERROR: MATERIAL DEFINITION NAME IS INVALID.";
            }
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

            if (materialPipeline == null){
                return this.message = "ERROR: MATERIAL PIPELINE ID CAN NOT BE NULL.";
            }

        } else {
            return this.message = "ERROR: Material Definition is NULL";
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
