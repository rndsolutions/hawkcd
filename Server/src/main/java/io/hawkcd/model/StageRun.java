package io.hawkcd.model;


import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all Stages of the initial execution of a Pipeline.
 * Alternatively it can contain all Stages from a Stage rerun of the same Pipeline
 */
public class StageRun extends Entity{
    private int executionId;
    private List<Stage> stages;

    public StageRun() {
        this.stages = new ArrayList<>();
    }

    public int getExecutionId() {
        return this.executionId;
    }

    public void setExecutionId(int executionId) {
        this.executionId = executionId;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void addStage(Stage stage) {
        this.stages.add(stage);
    }
}
