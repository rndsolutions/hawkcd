package net.hawkengine.core.pipelinescheduler;

import net.hawkengine.model.EnvironmentVariable;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentVariableService {
    public List<EnvironmentVariable> getOverriddenVariables(
            List<EnvironmentVariable> jobVariables, List<EnvironmentVariable> stageVariables, List<EnvironmentVariable> pipelineVariables) {
        List<EnvironmentVariable> overriddenVariables = new ArrayList<>();
        for (EnvironmentVariable variable : jobVariables) {
            boolean variableExists = overriddenVariables.stream().anyMatch(v -> v.getKey().equals(variable.getKey()));
            if (!variableExists) {
                overriddenVariables.add(variable);
            }
        }

        for (EnvironmentVariable variable : stageVariables) {
            boolean variableExists = overriddenVariables.stream().anyMatch(v -> v.getKey().equals(variable.getKey()));
            if (!variableExists) {
                overriddenVariables.add(variable);
            }
        }

        for (EnvironmentVariable variable : pipelineVariables) {
            boolean variableExists = overriddenVariables.stream().anyMatch(v -> v.getKey().equals(variable.getKey()));
            if (!variableExists) {
                overriddenVariables.add(variable);
            }
        }

        return overriddenVariables;
    }

    public String replaceVariablesInArguments(String arguments, List<EnvironmentVariable> variables) {
        String result = arguments;

        int startIndex = arguments.indexOf('%');
        int endIndex = arguments.indexOf('%', startIndex + 1);
        while (startIndex >= 0 && endIndex > startIndex) {
            String variableToReplace = arguments.substring(startIndex, endIndex + 1);
            String variableKey = arguments.substring(startIndex + 1, endIndex);
            EnvironmentVariable currentVariable = variables.stream().filter(v -> v.getKey().equals(variableKey)).findFirst().orElse(null);
            if (currentVariable != null) {
                result = result.replace(variableToReplace, currentVariable.getValue());
            }

            startIndex = arguments.indexOf('%', endIndex + 1);
            endIndex = arguments.indexOf('%', startIndex + 1);
        }

        return result;
    }
}
