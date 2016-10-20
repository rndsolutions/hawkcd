/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public String replaceVariablesInArguments(List<EnvironmentVariable> variables, String arguments) {
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
