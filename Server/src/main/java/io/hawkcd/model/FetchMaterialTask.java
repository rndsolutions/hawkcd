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

import io.hawkcd.core.security.Authorization;
import io.hawkcd.model.enums.MaterialType;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.enums.TaskType;

@Authorization(scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER)
public class FetchMaterialTask extends TaskDefinition {
    private String materialDefinitionId;
    private String materialName;
    private String pipelineName;
    private MaterialType materialType;
    private String destination;
    private MaterialDefinition materialDefinition;

    public FetchMaterialTask() {
        this.setType(TaskType.FETCH_MATERIAL);
    }

    public String getMaterialDefinitionId() {
        return materialDefinitionId;
    }

    public void setMaterialDefinitionId(String materialDefinitionId) {
        this.materialDefinitionId = materialDefinitionId;
    }

    public String getMaterialName() {
        return this.materialName;
    }

    public void setMaterialName(String value) {
        this.materialName = value;
    }

    public String getPipelineName() {
        return this.pipelineName;
    }

    public void setPipelineName(String value) {
        this.pipelineName = value;
    }

    public MaterialType getMaterialType() {
        return this.materialType;
    }

    public void setMaterialType(MaterialType value) {
        this.materialType = value;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String value) {
        this.destination = value;
    }

    public MaterialDefinition getMaterialDefinition() {
        return this.materialDefinition;
    }

    public void setMaterialDefinition(MaterialDefinition materialDefinition) {
        this.materialDefinition = materialDefinition;
    }
}
