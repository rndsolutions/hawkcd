package net.hawkengine.agent.models;

import net.hawkengine.agent.enums.TaskType;

public class FetchArtifactTask extends TaskDefinition {
    private String pipeline;
    private String stage;
    private String job;
    private String source;
    private String destination;

    public FetchArtifactTask() {
        this.setType(TaskType.FETCH_ARTIFACT);
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
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
