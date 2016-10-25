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

package net.hawkengine.model.payload;

import net.hawkengine.model.Job;

public class WorkInfo {
    private String pipelineDefinitionName;
    private int pipelineExecutionID;
    private String stageDefinitionName;
    private int stageExecutionID;
    private String jobDefinitionName;
    private Job job;

    public String getPipelineDefinitionName() {
        return this.pipelineDefinitionName;
    }

    public void setPipelineDefinitionName(String pipelineDefinitionName) {
        this.pipelineDefinitionName = pipelineDefinitionName;
    }

    public int getPipelineExecutionID() {
        return this.pipelineExecutionID;
    }

    public void setPipelineExecutionID(int pipelineExecutionID) {
        this.pipelineExecutionID = pipelineExecutionID;
    }

    public String getStageDefinitionName() {
        return this.stageDefinitionName;
    }

    public void setStageDefinitionName(String stageDefinitionName) {
        this.stageDefinitionName = stageDefinitionName;
    }

    public int getStageExecutionID() {
        return this.stageExecutionID;
    }

    public void setStageExecutionID(int stageExecutionID) {
        this.stageExecutionID = stageExecutionID;
    }

    public String getJobDefinitionName() {
        return this.jobDefinitionName;
    }

    public void setJobDefinitionName(String jobDefinitionName) {
        this.jobDefinitionName = jobDefinitionName;
    }

    public Job getJob() {
        return this.job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
