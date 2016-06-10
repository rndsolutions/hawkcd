package net.hawkengine.model;

public class UploadArtifactTask extends Task {
    private String source;
    private String destination;

    public UploadArtifactTask() throws Exception {
        this.setType(TaskType.UPLOAD_ARTIFACT);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String value) {
        source = value;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String value) {
        destination = value;
    }

}
