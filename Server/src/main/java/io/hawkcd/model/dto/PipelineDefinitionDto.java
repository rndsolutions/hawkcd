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
import io.hawkcd.model.PermissionObject;
import io.hawkcd.model.Pipeline;
import io.hawkcd.model.PipelineDefinition;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Authorization(scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER)
public class PipelineDefinitionDto extends PermissionObject {
    private String id;
    private String name;
    private PipelineDto lastRun;
    private List<Integer> pipelineExecutionIds;

    public PipelineDefinitionDto() {
        this.lastRun = new PipelineDto();
        this.pipelineExecutionIds = new ArrayList<>();
    }

    public void constructDto(PipelineDefinition pipelineDefinition, List<Pipeline> pipelines) {
        this.id = pipelineDefinition.getId();
        this.name = pipelineDefinition.getName();
        pipelines = pipelines.stream().sorted((p1, p2) -> Integer.compare(p2.getExecutionId(), p1.getExecutionId())).collect(Collectors.toList());
        if (!pipelines.isEmpty()) {
            int numberOfPipelines = pipelines.size();
            for (int i = 0; i < numberOfPipelines; i++) {
                this.pipelineExecutionIds.add(pipelines.get(i).getExecutionId());
                if (i == 0) {
                    this.lastRun.constructBasePipelineDto(pipelines.get(i));
                }
            }
        } else {
            this.lastRun.constructEmptyPipelineDto(pipelineDefinition);
        }
    }
}
