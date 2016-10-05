package net.hawkengine.model;

import net.hawkengine.model.enums.TaskType;

public class UploadArtifactTask extends TaskDefinition {
    private String source;
    private String destination;

    public UploadArtifactTask() {
        this.setType(TaskType.UPLOAD_ARTIFACT);
        this.source = "";
        this.destination = "";
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
