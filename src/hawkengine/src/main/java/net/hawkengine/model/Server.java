package net.hawkengine.model;

public class Server extends DbEntry {
    private String artifactsDir;
    private Double purgeStart;
    private Double purgeUpTo;

    public String getArtifactsDir() {
        return artifactsDir;
    }

    public void setArtifactsDir(String value) {
        artifactsDir = value;
    }

    public Double getPurgeStart() {
        return purgeStart;
    }

    public void setPurgeStart(Double value) {
        purgeStart = value;
    }

    public Double getPurgeUpTo() {
        return purgeUpTo;
    }

    public void setPurgeUpTo(Double value) {
        purgeUpTo = value;
    }

}
