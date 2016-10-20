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

package net.hawkengine.model.dto;

import net.hawkengine.model.PermissionObject;
import net.hawkengine.model.PipelineGroup;

import java.util.List;

public class PipelineGroupDto extends PermissionObject {
    private String id;
    private String name;
    private List<PipelineDefinitionDto> pipelines;

    public List<PipelineDefinitionDto> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<PipelineDefinitionDto> pipelines) {
        this.pipelines = pipelines;
    }

    public void constructDto(PipelineGroup pipelineGroup) {
        this.setPermissionType(pipelineGroup.getPermissionType());
        this.id = pipelineGroup.getId();
        this.name = pipelineGroup.getName();
    }
}
