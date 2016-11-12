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

import java.util.ArrayList;
import java.util.List;

public class StageDefinition extends DbEntry {
    private String name;
    private String pipelineDefinitionId;
    private List<EnvironmentVariable> environmentVariables;
    private List<JobDefinition> jobDefinitions;
    private boolean isTriggeredManually;
    private String status;

    public StageDefinition() {
        this.setEnvironmentVariables(new ArrayList<>());
        this.setJobDefinitions(new ArrayList<>());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPipelineDefinitionId() {
        return this.pipelineDefinitionId;
    }

    public void setPipelineDefinitionId(String pipelineDefinitionId) {
        this.pipelineDefinitionId = pipelineDefinitionId;
    }

    public List<EnvironmentVariable> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public void setEnvironmentVariables(List<EnvironmentVariable> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public List<JobDefinition> getJobDefinitions() {
        return this.jobDefinitions;
    }

    public void setJobDefinitions(List<JobDefinition> jobDefinitions) {
        this.jobDefinitions = jobDefinitions;
    }

    public boolean isTriggeredManually() {
        return this.isTriggeredManually;
    }

    @JsonProperty("isTriggeredManually")
    public void setTriggeredManually(boolean triggeredManually) {
        this.isTriggeredManually = triggeredManually;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
