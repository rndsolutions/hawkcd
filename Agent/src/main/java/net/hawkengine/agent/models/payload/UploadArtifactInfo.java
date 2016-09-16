package net.hawkengine.agent.models.payload;

import java.io.File;

public class UploadArtifactInfo {
    private File zipFile;
    private String destination;

    public UploadArtifactInfo(File zipFile, String destination) {
        this.zipFile = zipFile;
        this.destination = destination;
    }

    public File getZipFile() {
        return this.zipFile;
    }

    public void setZipFile(File zipFile) {
        this.zipFile = zipFile;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
