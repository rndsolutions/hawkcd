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

package io.hawkcd.agent.models;

public class InstallInfo {
    private String serverName;
    private int serverPort;
    private String agentSandbox;
    private String agentTempDirectoryPath;
    private String agentArtifactsDirectoryPath;
    private String agentPipelinesDir;
    private String serverAddress;
    private String reportJobApiAddress;
    private String reportAgentApiAddress;
    private String checkForWorkApiAddress;
    private String createArtifactApiAddress;
    private String fetchArtifactApiAddress;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getAgentTempDirectoryPath() {
        return agentTempDirectoryPath;
    }

    public void setAgentTempDirectoryPath(String agentTempDirectoryPath) {
        this.agentTempDirectoryPath = agentTempDirectoryPath;
    }

    public String getAgentPipelinesDir() {
        return agentPipelinesDir;
    }

    public void setAgentPipelinesDir(String agentPipelinesDir) {
        this.agentPipelinesDir = agentPipelinesDir;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getReportJobApiAddress() {
        return reportJobApiAddress;
    }

    public void setReportJobApiAddress(String reportJobApiAddress) {
        this.reportJobApiAddress = reportJobApiAddress;
    }

    public String getReportAgentApiAddress() {
        return this.reportAgentApiAddress;
    }

    public void setReportAgentApiAddress(String reportAgentApiAddress) {
        this.reportAgentApiAddress = reportAgentApiAddress;
    }

    public String getCheckForWorkApiAddress() {
        return checkForWorkApiAddress;
    }

    public void setCheckForWorkApiAddress(String checkForWorkApiAddress) {
        this.checkForWorkApiAddress = checkForWorkApiAddress;
    }

    public String getAgentArtifactsDirectoryPath() {
        return agentArtifactsDirectoryPath;
    }

    public void setAgentArtifactsDirectoryPath(String agentArtifactsDirectoryPath) {
        this.agentArtifactsDirectoryPath = agentArtifactsDirectoryPath;
    }

    public String getCreateArtifactApiAddress() {
        return createArtifactApiAddress;
    }

    public void setCreateArtifactApiAddress(String createArtifactApiAddress) {
        this.createArtifactApiAddress = createArtifactApiAddress;
    }

    public String getFetchArtifactApiAddress() {
        return fetchArtifactApiAddress;
    }

    public void setFetchArtifactApiAddress(String fetchArtifactApiAddress) {
        this.fetchArtifactApiAddress = fetchArtifactApiAddress;
    }

    public String getAgentSandbox() {
        return agentSandbox;
    }

    public void setAgentSandbox(String agentSandbox) {
        this.agentSandbox = agentSandbox;
    }
}
