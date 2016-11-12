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

package io.hawkcd.model.payload;

import io.hawkcd.model.enums.PipelineStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class JobExecutionInfo {
    private String pipelineDefinitionId;
    private int pipelineExecutionId;
    private String pipelineLabel;
    private String pipelineName;
    private String executedBy;
    private UUID stageId;
    private int stageExecutionId;
    private String stageName;
    private String jobId;
    private String jobName;
    private PipelineStatus status = PipelineStatus.PASSED;
    private String jobStateString;
    private LocalDateTime start;
    private LocalDateTime end;
    private Duration duration;
    private String result;

    public String getPipelineId() {
        return this.pipelineDefinitionId;
    }

    public void setPipelineId(String value) {
        this.pipelineDefinitionId = value;
    }

    public int getPipelineExecutionId() {
        return this.pipelineExecutionId;
    }

    public void setPipelineExecutionId(int value) {
        this.pipelineExecutionId = value;
    }

    public String getPipelineLabel() {
        return this.pipelineLabel;
    }

    public void setPipelineLabel(String value) {
        this.pipelineLabel = value;
    }

    public String getPipelineName() {
        return this.pipelineName;
    }

    public void setPipelineName(String value) {
        this.pipelineName = value;
    }

    public String getExecutedBy() {
        return this.executedBy;
    }

    public void setExecutedBy(String value) {
        this.executedBy = value;
    }

    public UUID getStageId() {
        return this.stageId;
    }

    public void setStageId(UUID value) {
        this.stageId = value;
    }

    public int getStageExecutionId() {
        return this.stageExecutionId;
    }

    public void setStageExecutionId(int value) {
        this.stageExecutionId = value;
    }

    public String getStageName() {
        return this.stageName;
    }

    public void setStageName(String value) {
        this.stageName = value;
    }

    public String getJobId() {
        return this.jobId;
    }

    public void setJobId(String value) {
        this.jobId = value;
    }

    public String getJobName() {
        return this.jobName;
    }

    public void setJobName(String value) {
        this.jobName = value;
    }

    public PipelineStatus getStatus() {
        return this.status;
    }

    public void setStatus(PipelineStatus value) {
        this.status = value;
    }

    public String getJobStateString() {
        return this.jobStateString;
    }

    public void setJobStateString(String value) {
        this.jobStateString = value;
    }

    public LocalDateTime getStart() {
        return this.start;
    }

    public void setStart(LocalDateTime value) {
        this.start = value;
    }

    public LocalDateTime getEnd() {
        return this.end;
    }

    public void setEnd(LocalDateTime value) {
        this.end = value;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setDuration(Duration value) {
        this.duration = value;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String value) {
        this.result = value;
    }
}
