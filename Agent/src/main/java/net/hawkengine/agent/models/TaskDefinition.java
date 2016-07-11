package net.hawkengine.agent.models;

import net.hawkengine.agent.enums.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public abstract class TaskDefinition {
    private String id;
    private String name;
    private String jobDefinitionId;
    private String stageDefinitionId;
    private String pipelineDefinitionId;
    private TaskType type;
    private RunIf runIfCondition;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobDefinitionId() {
        return this.jobDefinitionId;
    }

    public void setJobDefinitionId(String jobDefinitionId) {
        this.jobDefinitionId = jobDefinitionId;
    }

    public String getStageDefinitionId() {
        return this.stageDefinitionId;
    }

    public void setStageDefinitionId(String stageDefinitionId) {
        this.stageDefinitionId = stageDefinitionId;
    }

    public String getPipelineDefinitionId() {
        return this.pipelineDefinitionId;
    }

    public void setPipelineDefinitionId(String pipelineDefinitionId) {
        this.pipelineDefinitionId = pipelineDefinitionId;
    }

    public TaskType getType() {
        return this.type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public RunIf getRunIfCondition() {
        return this.runIfCondition;
    }

    public void setRunIfCondition(RunIf runIfCondition) {
        this.runIfCondition = runIfCondition;
    }
}
