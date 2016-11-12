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

package io.hawkcd.agent.models;

import io.hawkcd.agent.enums.RunIf;
import io.hawkcd.agent.enums.TaskType;

public abstract class TaskDefinition {
    private String id;
    private String name;
    private String jobDefinitionId;
    private String stageDefinitionId;
    private String pipelineDefinitionId;
    private TaskType type;
    private RunIf runIfCondition;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobDefinitionId() {
        return this.jobDefinitionId;
    }

    public void setJobDefinitionId(String jobDefinitionId) {
        this.jobDefinitionId = jobDefinitionId;
    }

    public String getStageDefinitionId() {
        return this.stageDefinitionId;
    }

    public void setStageDefinitionId(String stageDefinitionId) {
        this.stageDefinitionId = stageDefinitionId;
    }

    public String getPipelineDefinitionId() {
        return this.pipelineDefinitionId;
    }

    public void setPipelineDefinitionId(String pipelineDefinitionId) {
        this.pipelineDefinitionId = pipelineDefinitionId;
    }

    public TaskType getType() {
        return this.type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public RunIf getRunIfCondition() {
        return this.runIfCondition;
    }

    public void setRunIfCondition(RunIf runIfCondition) {
        this.runIfCondition = runIfCondition;
    }
}
