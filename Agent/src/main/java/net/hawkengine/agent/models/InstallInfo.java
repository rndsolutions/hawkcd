package net.hawkengine.agent.models;

public class InstallInfo {
    private String serverName;
    private int serverPort;
    private String agentSandbox;
    private String agentTempDirectoryPath;
    private String agentPipelinesDirectoryPath;
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

    public String getAgentPipelinesDirectoryPath() {
        return agentPipelinesDirectoryPath;
    }

    public void setAgentPipelinesDirectoryPath(String agentPipelinesDirectoryPath) {
        this.agentPipelinesDirectoryPath = agentPipelinesDirectoryPath;
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
