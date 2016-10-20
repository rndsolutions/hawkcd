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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.hawkengine.agent.enums.MaterialType;
import net.hawkengine.agent.models.GitMaterial;
import net.hawkengine.agent.models.NugetMaterial;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GitMaterial.class, name = "GIT"),
        @JsonSubTypes.Type(value = NugetMaterial.class, name = "NUGET")})
public abstract class MaterialDefinition {
    private String pipelineDefinitionId;
    private String pipelineDefinitionName;
    private String name;
    private MaterialType type;
    private boolean isPollingForChanges;

    public String getPipelineDefinitionId() {
        return this.pipelineDefinitionId;
    }

    public void setPipelineDefinitionId(String value) {
        this.pipelineDefinitionId = value;
    }

    public String getPipelineDefinitionName() {
        return this.pipelineDefinitionName;
    }

    public void setPipelineDefinitionName(String pipelineDefinitionName) {
        this.pipelineDefinitionName = pipelineDefinitionName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public MaterialType getType() {
        return this.type;
    }

    public void setType(MaterialType value) {
        this.type = value;
    }

    @JsonProperty("isPollingForChanges")
    public boolean isPollingForChanges() {
        return this.isPollingForChanges;
    }

    public void setPollingForChanges(boolean pollingForChanges) {
        this.isPollingForChanges = pollingForChanges;
    }
}
