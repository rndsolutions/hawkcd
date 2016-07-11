package net.hawkengine.model;

public class Server extends DbEntry {
    private String artifactsDir;
    private Double purgeStart;
    private Double purgeUpTo;

    public String getArtifactsDir() {
        return this.artifactsDir;
    }

    public void setArtifactsDir(String value) {
        this.artifactsDir = value;
    }

    public Double getPurgeStart() {
        return this.purgeStart;
    }

    public void setPurgeStart(Double value) {
        this.purgeStart = value;
    }

    public Double getPurgeUpTo() {
        return this.purgeUpTo;
    }

    public void setPurgeUpTo(Double value) {
        this.purgeUpTo = value;
    }

}
