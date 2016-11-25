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
import io.hawkcd.model.*;
import io.hawkcd.model.configuration.filetree.JsTreeFile;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.enums.PipelineStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Authorization(scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER)
public class PipelineDto extends PipelineFamily {
    private String pipelineDefinitionId;
    private String pipelineDefinitionName;
    private int executionId;
    private List<Material> materials;
    private PipelineStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;
    private String triggerReason;
    private List<JsTreeFile> artifactsFileStructure;
    private List<StageDto> stages;

    public PipelineDto() {
        this.stages = new ArrayList<>();
    }

    public void constructEmptyPipelineDto(PipelineDefinition pipelineDefinition) {
        int numberOfStageDefinitions = pipelineDefinition.getStageDefinitions().size();
        for (int i = 0; i < numberOfStageDefinitions; i++) {
            this.stages.add(new StageDto());
        }
    }

    public void constructBasePipelineDto(Pipeline pipeline) {
        this.setPermissionType(pipeline.getPermissionType());
        super.setId(pipeline.getId());
        super.setPipelineDefinitionId(pipeline.getPipelineDefinitionId());
        super.setPipelineGroupId(pipeline.getPipelineGroupId());
        this.pipelineDefinitionId = pipeline.getPipelineDefinitionId();
        this.pipelineDefinitionName = pipeline.getPipelineDefinitionName();
        this.executionId = pipeline.getExecutionId();
        this.status = pipeline.getStatus();
        this.startTime = pipeline.getStartTime();
        this.endTime = pipeline.getEndTime();
        this.duration = pipeline.getDuration();
        this.triggerReason = pipeline.getTriggerReason();

        List<Stage> stages = pipeline.getStages();
        for (Stage pipelineStage : stages) {
            StageDto stageDto = new StageDto();
            stageDto.constructDto(pipelineStage);
            this.stages.add(stageDto);
        }
    }

    public void constructHistoryPipelineDto(Pipeline pipeline) {
        this.constructBasePipelineDto(pipeline);
        this.materials = pipeline.getMaterials();
    }

    public void constructArtifactPipelineDto(Pipeline pipeline) {
        this.constructHistoryPipelineDto(pipeline);
        this.artifactsFileStructure = pipeline.getArtifactsFileStructure();
    }
}
