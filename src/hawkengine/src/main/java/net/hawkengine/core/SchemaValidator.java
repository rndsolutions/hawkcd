package net.hawkengine.core;

import net.hawkengine.model.Agent;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.MaterialType;
import net.hawkengine.model.PipelineGroup;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by boris on 09.06.16.
 */
public class SchemaValidator {
    private String message = "OK";
    private static final String NAME_PATTERN = "[A-Za-z]{3,20}"; //TODO enter name pattern
    private static final String GIT_PATTERN = "[a-zA-Z]{5,50}"; //TODO define git url pattern
    private static final String NUGET_PATTERN = "[a-zA-Z]{5,50}"; //TODO define git url pattern



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

    //AGENT
    public String validate(Agent agent){
        if (agent != null){
            String agentName = agent.getName();
            String hostname = agent.getHostName();
            String ipAdress = agent.getIPAddress();



            if (agentName == null){
                return this.message = "ERROR: AGENT NAME IS NULL.";
            }

            if (this.isValidRegEx(agentName,NAME_PATTERN) == false){
                return this.message = "ERROR: AGENT NAME IS INVALID.";
            }

            if (hostname == null){
                return this.message = "ERROR: AGENT HOSTNAME IS NULL.";
            }

            if (this.isValidRegEx(hostname,NAME_PATTERN) == false){
                return this.message = "ERROR: AGENT HOSTNAME IS INVALID.";
            }


        }
        return this.message;
    }

    //MATERIAL DEFINITION
    public String validate(MaterialDefinition materialDefinition){
        if (materialDefinition != null){
            String materialName = materialDefinition.getName();
            String materialURL = materialDefinition.getUrl();
            String materialPipeline = materialDefinition.getPipelineName();
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
        return message;
    }


    private boolean isValidRegEx(String input, String string_pattern){
        Pattern pattern = Pattern.compile(string_pattern);
        Matcher matcher = pattern.matcher(input);
        boolean isMatch = matcher.matches();
        return isMatch;
    }
}
