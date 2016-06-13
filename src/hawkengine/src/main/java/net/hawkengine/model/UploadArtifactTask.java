package net.hawkengine.model;

public class UploadArtifactTask extends Task {
    private String source;
    private String destination;

    public UploadArtifactTask() throws Exception {
        this.setType(TaskType.UPLOAD_ARTIFACT);
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String value) {
        this.source = value;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String value) {
        this.destination = value;
    }

}
