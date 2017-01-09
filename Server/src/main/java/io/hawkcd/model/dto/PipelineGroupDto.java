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

package io.hawkcd.model.dto;

import io.hawkcd.core.security.Authorization;
import io.hawkcd.model.Entity;
import io.hawkcd.model.PipelineFamily;
import io.hawkcd.model.PipelineGroup;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;

import java.util.ArrayList;
import java.util.List;

@Authorization(scope = PermissionScope.PIPELINE_GROUP, type = PermissionType.VIEWER)
public class PipelineGroupDto extends Entity {
    private List<PipelineDefinitionDto> pipelines;

    public PipelineGroupDto() {
        this.pipelines = new ArrayList<>();
    }

    public List<PipelineDefinitionDto> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<PipelineDefinitionDto> pipelines) {
        this.pipelines = pipelines;
    }

    public void constructDto(PipelineGroup pipelineGroup) {
        this.setPermissionType(pipelineGroup.getPermissionType());
        super.setId(pipelineGroup.getId());
        super.setName(pipelineGroup.getName());
    }
}
