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

package io.hawkcd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.hawkcd.core.security.Authorization;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.enums.TaskType;

@Authorization(scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER)
public class ExecTask extends TaskDefinition {
    private String command;
    private String arguments;
    private String lookUpCommands;
    private String workingDirectory;
    private boolean isIgnoringErrors;

    public ExecTask() {
        this.setType(TaskType.EXEC);
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(String value) {
        this.command = value;
    }

    public String getArguments() {
        return this.arguments;
    }

    public void setArguments(String value) {
        this.arguments = value;
    }

    public String getLookUpCommands() {
        return this.lookUpCommands;
    }

    public void setLookUpCommands(String value) {
        this.lookUpCommands = value;
    }

    public String getWorkingDirectory() {
        return this.workingDirectory;
    }

    public void setWorkingDirectory(String value) {
        this.workingDirectory = value;
    }

    public boolean isIgnoringErrors() {
        return this.isIgnoringErrors;
    }

    @JsonProperty("isIgnoringErrors")
    public void setIgnoringErrors(boolean ignoringErrors) {
        this.isIgnoringErrors = ignoringErrors;
    }
}
