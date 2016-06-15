package net.hawkengine.model;

import net.hawkengine.model.enums.TaskType;

//[SD] I think we need a bool property to indicate if the request is for a file or folder. Although the agent will download it in any case because it will use the fetch\zip method. We need it to know SrcDir or SrcFile is the source.
public class FetchArtifactTask extends Task {
    /// <summary>
    /// This value can either be: 1. the name of upstream pipeline on which the
    /// pipeline of the job depends on.
    /// The pipeline should be added as a dependency under <materials>, or
    /// 2. the hierarchy of an ancestor pipeline of the current pipeline.
    /// Example,
    /// The value "BuildPipeline/AcceptancePipeline" denotes that the fetch task
    /// attempts to fetch artifacts
    /// from its ancestor 'BuildPipeline'. The given hierarchy denotes that the
    /// current pipeline depends
    /// on 'AcceptancePipeline' which in turn depends on 'BuildPipeline' using
    /// the dependency
    /// material definition given under materials. Defaults to current pipeline
    /// if not specified.
    /// </summary>
    private String pipeline;
    /**
     * The name of the stage to fetch artifacts from
     */
    private String stage;
    /**
     * The name of the job to fetch artifacts from
     */
    private String job;
    /**
     * The path of the artifact directory of a specific job, relative to the sandbox directory. If
     * the directory does not exist, the job is failed
     */
    private String source;
    private String destination;

    public FetchArtifactTask() {
        this.setType(TaskType.FETCH_ARTIFACT);
    }

    public String getPipeline() {
        return this.pipeline;
    }

    public void setPipeline(String value) {
        this.pipeline = value;
    }

    public String getStage() {
        return this.stage;
    }

    public void setStage(String value) {
        this.stage = value;
    }

    public String getJob() {
        return this.job;
    }

    public void setJob(String value) {
        this.job = value;
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
