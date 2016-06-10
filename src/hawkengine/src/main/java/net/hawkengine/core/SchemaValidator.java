package net.hawkengine.core;

import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.PipelineGroup;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by boris on 09.06.16.
 */
public class SchemaValidator {
    private String message = "OK";
    private static final String NAME_PATTERN = "[A-Za-z]{3,20}"; //TODO enter name pattern
    private static final String URL_PATTERN = ""; //TODO enter url pattern


    //PIPELINEGROUP
    public String validate(PipelineGroup pipelineGroup) {
        if (pipelineGroup != null) {
            String name = pipelineGroup.getName();
                if (name == null) {
                   return this.message = "ERROR: PIPELINE GROUP NAME IS NULL";
                }

                if(isValidRegEx(name,NAME_PATTERN) == false){
                    return this.message = "ERROR: Pipeline Group name is invalid.";
                }
        }
        else {
            return this.message = "ERROR: PIPELINEGROUP IS NULL";
        }
        return message;
    }

    //MATERIAL DEFINITION
    public String validate(MaterialDefinition materialDefinition){
        if (materialDefinition != null){
            String materialName = materialDefinition.getName();
            String materialURL = materialDefinition.getUrl();
            String materialPipeline = materialDefinition.getPipelineName();
            if (materialName == null){
                return this.message = "ERROR: Material Definition name can not be null.";
            }
            if (isValidRegEx(materialName, NAME_PATTERN) == false){
                return this.message = "ERROR: Material name is ivalid.";
            }
            if (materialURL == null){
                return this.message = "ERROR: Material URL is NULL.";
            }
            if (isValidRegEx(materialDefinition.getUrl(),URL_PATTERN) == false){
                return this.message = "ERROR: INVALID MATERIAL URL";
            }
            if (materialPipeline == null){
                return this.message = "ERROR: Material ID name can not be null.";
            }
            if (isValidRegEx(materialPipeline, NAME_PATTERN) == false){
                return this.message = "ERROR: Material ID is ivalid.";
            }

        } else {
            message = "ERROR: Material Definition is NULL";
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
