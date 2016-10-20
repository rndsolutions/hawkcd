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

package net.hawkengine.agent.models.payload;

import net.hawkengine.agent.enums.ExecutionState;
import net.hawkengine.agent.enums.ExecutionStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JobExecutionInfo {
    private UUID pipelineId;
    private int pipelineExecutionId;
    private String pipelineName;
    private String pipelineLabel;
    private String executedBy;
    private UUID stageId;
    private int stageExecutionId;
    private String stageName;
    private UUID jobId;
    private String jobName;
    private ExecutionState state;
    private ExecutionStatus status;
    private LocalDateTime start;
    private LocalDateTime end;
    private String result;
    private List<TaskExecutionInfo> taskExecutionInfo;

    public JobExecutionInfo() {
        this.result = "";
        this.taskExecutionInfo = new ArrayList<TaskExecutionInfo>();
    }

    public UUID getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(UUID pipelineId) {
        this.pipelineId = pipelineId;
    }

    public int getPipelineExecutionId() {
        return pipelineExecutionId;
    }

    public void setPipelineExecutionId(int pipelineExecutionId) {
        this.pipelineExecutionId = pipelineExecutionId;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public String getPipelineLabel() {
        return pipelineLabel;
    }

    public void setPipelineLabel(String pipelineLabel) {
        this.pipelineLabel = pipelineLabel;
    }

    public String getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(String executedBy) {
        this.executedBy = executedBy;
    }

    public UUID getStageId() {
        return stageId;
    }

    public void setStageId(UUID stageId) {
        this.stageId = stageId;
    }

    public int getStageExecutionId() {
        return stageExecutionId;
    }

    public void setStageExecutionId(int stageExecutionId) {
        this.stageExecutionId = stageExecutionId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public ExecutionState getState() {
        return state;
    }

    public void setState(ExecutionState state) {
        this.state = state;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {

//        if(!result.isEmpty()){
//            result = String.format("%s %s %s", LocalDateTime.now(), result, System.lineSeparator());
//            this.result = result;
//        }

        this.result = result;
    }

    public void appendToJobResult(String input, boolean includeDate) {

        if (!input.isEmpty()) {

            if (includeDate) {
                input = String.format("%s %s %s", LocalDateTime.now(), input, System.lineSeparator());
            } else {
                input = String.format("%s %s", input, System.lineSeparator());
            }

            this.setResult(this.getResult() + input);
        }
    }

    public List<TaskExecutionInfo> getTaskExecutionInfo() {

        return taskExecutionInfo;
    }

    public void setTaskExecutionInfo(List<TaskExecutionInfo> taskExecutionInfo) {
        this.taskExecutionInfo = taskExecutionInfo;
    }
}
