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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Authorization(scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER)
public class PipelineDefinition extends PipelineFamiliy {

    private String groupName;
    private String labelTemplate;
    private int revisionCount;
    private Set<String> materialDefinitionIds;
    private List<StageDefinition> stageDefinitions;
    private boolean isAutoSchedulingEnabled;

    public PipelineDefinition() {
        this.setLabelTemplate("%COUNT%");
        this.setEnvironmentVariables(new ArrayList<>());
        this.setMaterialDefinitionIds(new HashSet<>());
        this.setStageDefinitions(new ArrayList<>());
        this.setRevisionCount(1);
    }

    public String getLabelTemplate() {
        return this.labelTemplate;
    }

    public void setLabelTemplate(String labelTemplate) {
        this.labelTemplate = labelTemplate;
    }

    public int getRevisionCount() {
        return this.revisionCount;
    }

    public void setRevisionCount(int revisionCount) {
        this.revisionCount = revisionCount;
    }

    public Set<String> getMaterialDefinitionIds() {
        return materialDefinitionIds;
    }

    public void setMaterialDefinitionIds(Set<String> materialDefinitionIds) {
        this.materialDefinitionIds = materialDefinitionIds;
    }

    public List<StageDefinition> getStageDefinitions() {
        return this.stageDefinitions;
    }

    public void setStageDefinitions(List<StageDefinition> stageDefinitions) {
        this.stageDefinitions = stageDefinitions;
    }

    public boolean isAutoSchedulingEnabled() {
        return this.isAutoSchedulingEnabled;
    }

    @JsonProperty("isAutoSchedulingEnabled")
    public void setAutoSchedulingEnabled(boolean autoSchedulingEnabled) {
        this.isAutoSchedulingEnabled = autoSchedulingEnabled;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}

