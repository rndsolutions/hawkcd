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

package net.hawkengine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Agent extends DbEntry {
    private String name;
    private String hostName;
    private String ipAddress;
    private String rootPath;
    private String operatingSystem;
    private Set<String> resources;
    private Environment environment;
    private boolean isRunning;
    private boolean isEnabled;
    private boolean isConnected;
    private boolean isAssigned;
    private LocalDateTime lastReportedTime;

    public Agent() {
        this.setResources(new HashSet<>());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getRootPath() {
        return this.rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getOperatingSystem() {
        return this.operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public Set<String> getResources() {
        return this.resources;
    }

    public void setResources(Set<String> resources) {
        this.resources = resources;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @JsonProperty("isRunning")
    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    @JsonProperty("isEnabled")
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    @JsonProperty("isConnected")
    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    @JsonProperty("isAssigned")
    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }

    public LocalDateTime getLastReportedTime() {
        return this.lastReportedTime;
    }

    public void setLastReportedTime(LocalDateTime lastReportedTime) {
        this.lastReportedTime = lastReportedTime;
    }
}