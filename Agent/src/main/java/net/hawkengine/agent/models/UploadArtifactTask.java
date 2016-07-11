package net.hawkengine.agent.models;

import net.hawkengine.agent.enums.TaskType;

public class UploadArtifactTask extends TaskDefinition {

    private String source;
    private String destination;

    public UploadArtifactTask() {
        this.setType(TaskType.UPLOAD_ARTIFACT);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

}
