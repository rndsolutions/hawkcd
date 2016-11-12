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

import io.hawkcd.model.enums.AgentExecutionState;

import java.util.UUID;

public class AgentInfo {
    private UUID agentId;
    private String name;
    private String ipAddress;
    private String rootPath;
    private AgentExecutionState state;

    public UUID getAgentId() {
        return this.agentId;
    }

    public void setAgentId(UUID value) {
        this.agentId = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getIPAddress() {
        return this.ipAddress;
    }

    public void setIPAddress(String value) {
        this.ipAddress = value;
    }

    public String getSandbox() {
        return this.rootPath;
    }

    public void setSandbox(String value) {
        this.rootPath = value;
    }

    public AgentExecutionState getState() {
        return this.state;
    }

    public void setState(AgentExecutionState value) {
        this.state = value;
    }

}
