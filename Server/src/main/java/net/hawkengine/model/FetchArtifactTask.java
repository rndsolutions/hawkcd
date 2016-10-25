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

package net.hawkengine.model;

import net.hawkengine.model.enums.TaskType;

public class FetchArtifactTask extends TaskDefinition {
    private String designatedPipelineDefinitionId;
    private String designatedPipelineDefinitionName;
    private String designatedPipelineExecutionId;
    private String source;
    private String destination;
    private boolean shouldUseLatestRun;

    public FetchArtifactTask() {
        this.setType(TaskType.FETCH_ARTIFACT);
        this.source = "";
        this.destination = "";
    }

    public String getDesignatedPipelineDefinitionId() {
        return designatedPipelineDefinitionId;
    }

    public void setDesignatedPipelineDefinitionId(String designatedPipelineDefinitionId) {
        this.designatedPipelineDefinitionId = designatedPipelineDefinitionId;
    }

    public String getDesignatedPipelineDefinitionName() {
        return this.designatedPipelineDefinitionName;
    }

    public void setDesignatedPipelineDefinitionName(String value) {
        this.designatedPipelineDefinitionName = value;
    }

    public String getDesignatedPipelineExecutionId() {
        return this.designatedPipelineExecutionId;
    }

    public void setDesignatedPipelineExecutionId(String value) {
        this.designatedPipelineExecutionId = value;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String value) {
        this.source = value;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String value) {
        this.destination = value;
    }

    public boolean shouldUseLatestRun() {
        return shouldUseLatestRun;
    }

    public void setShouldUseLatestRun(boolean shouldUseLatestRun) {
        this.shouldUseLatestRun = shouldUseLatestRun;
    }
}
