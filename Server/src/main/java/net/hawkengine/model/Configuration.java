package net.hawkengine.model;

public final class Configuration {
    private String serverName;
    private int serverPort;
    private int embeddedDbPort;

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

    public int getEmbeddedDbPort() {
        return embeddedDbPort;
    }

    public void setEmbeddedDbPort(int embeddedDbPort) {
        this.embeddedDbPort = embeddedDbPort;
    }
}
