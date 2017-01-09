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
import io.hawkcd.model.payload.JsTreeFile;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.enums.PipelineStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Authorization(scope = PermissionScope.PIPELINE, type = PermissionType.VIEWER)
public class Pipeline extends PipelineFamily {
    private String pipelineDefinitionName;
    private int executionId;
    private List<Material> materials;
    private List<Stage> stages;
    private PipelineStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;
    private String triggerReason;
    private boolean areMaterialsUpdated;
    private boolean isPrepared;
    private boolean shouldBeCanceled;
    private List<JsTreeFile> artifactsFileStructure;

    public Pipeline() {
        this.setStartTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        this.setEnvironmentVariables(new ArrayList<>());
        this.setMaterials(new ArrayList<>());
        this.setStages(new ArrayList<>());
        this.setArtifactsFileStructure(new ArrayList<>());
        this.status = PipelineStatus.IN_PROGRESS;
    }

    public String getPipelineDefinitionName() {
        return pipelineDefinitionName;
    }

    public void setPipelineDefinitionName(String pipelineDefinitionName) {
        this.pipelineDefinitionName = pipelineDefinitionName;
    }

    public int getExecutionId() {
        return this.executionId;
    }

    public void setExecutionId(int executionId) {
        this.executionId = executionId;
    }

    public List<Material> getMaterials() {
        return this.materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public List<Stage> getStages() {
        return this.stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public PipelineStatus getStatus() {
        return this.status;
    }

    public void setStatus(PipelineStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getTriggerReason() {
        return this.triggerReason;
    }

    public void setTriggerReason(String triggerReason) {
        this.triggerReason = triggerReason;
    }

    public boolean areMaterialsUpdated() {
        return this.areMaterialsUpdated;
    }

    public void setMaterialsUpdated(boolean areMaterialsUpdated) {
        this.areMaterialsUpdated = areMaterialsUpdated;
    }

    public boolean isPrepared() {
        return this.isPrepared;
    }

    public void setPrepared(boolean prepared) {
        this.isPrepared = prepared;
    }

    public boolean shouldBeCanceled() {
        return this.shouldBeCanceled;
    }

    public void setShouldBeCanceled(boolean shouldBeCanceled) {
        this.shouldBeCanceled = shouldBeCanceled;
    }

    public List<JsTreeFile> getArtifactsFileStructure() {
        return this.artifactsFileStructure;
    }

    public void setArtifactsFileStructure(List<JsTreeFile> artifactsFileStructure) {
        this.artifactsFileStructure = artifactsFileStructure;
    }
}