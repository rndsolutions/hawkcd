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

public class AgentReport {
    private AgentInfo agentInfo;
    private EnvironmentInfo environmentInfo;
    private JobExecutionInfo jobExecutionInfo;

    public AgentInfo getAgentInfo() {
        return this.agentInfo;
    }

    public void setAgentInfo(AgentInfo value) {
        this.agentInfo = value;
    }

    public EnvironmentInfo getEnvironmentInfo() {
        return this.environmentInfo;
    }

    public void setEnvironmentInfo(EnvironmentInfo value) {
        this.environmentInfo = value;
    }

    public JobExecutionInfo getJobExecutionInfo() {
        return this.jobExecutionInfo;
    }

    public void setJobExecutionInfo(JobExecutionInfo value) {
        this.jobExecutionInfo = value;
    }
}
