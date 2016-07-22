package net.hawkengine.model.configuration;

import net.hawkengine.model.enums.DatabaseType;
import net.hawkengine.model.enums.ProviderType;

import java.util.Map;

public final class Configuration {
    private String serverHost;
    private int serverPort;
    private DatabaseType databaseType;
    private Map<DatabaseType, DatabaseConfig> databaseConfigs;
    private Map<ProviderType, ProviderConfig> providerConfigs;
    private String materialsDestination;
    private String artifactsDestination;
    private int pipelineSchedulerPollInterval;
    private int materialTrackerPollInterval;

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public Map<DatabaseType, DatabaseConfig> getDatabaseConfigs() {
        return databaseConfigs;
    }

    public void setDatabaseConfigs(Map<DatabaseType, DatabaseConfig> databaseConfigs) {
        this.databaseConfigs = databaseConfigs;
    }

    public Map<ProviderType, ProviderConfig> getProviderConfigs() {
        return providerConfigs;
    }

    public void setProviderConfigs(Map<ProviderType, ProviderConfig> providerConfigs) {
        this.providerConfigs = providerConfigs;
    }

    public String getMaterialsDestination() {
        return materialsDestination;
    }

    public void setMaterialsDestination(String materialsDestination) {
        this.materialsDestination = materialsDestination;
    }

    public String getArtifactsDestination() {
        return artifactsDestination;
    }

    public void setArtifactsDestination(String artifactsDestination) {
        this.artifactsDestination = artifactsDestination;
    }

    public int getPipelineSchedulerPollInterval() {
        return pipelineSchedulerPollInterval;
    }

    public void setPipelineSchedulerPollInterval(int pipelineSchedulerPollInterval) {
        this.pipelineSchedulerPollInterval = pipelineSchedulerPollInterval;
    }

    public int getMaterialTrackerPollInterval() {
        return materialTrackerPollInterval;
    }

    public void setMaterialTrackerPollInterval(int materialTrackerPollInterval) {
        this.materialTrackerPollInterval = materialTrackerPollInterval;
    }
}
