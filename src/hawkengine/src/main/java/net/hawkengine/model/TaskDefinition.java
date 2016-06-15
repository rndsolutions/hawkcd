package net.hawkengine.model;

import net.hawkengine.model.enums.RunIf;
import net.hawkengine.model.enums.TaskType;

public abstract class TaskDefinition extends DbEntry {
    private String name;
    private String jobDefinitionId;
    private TaskType type;
    private RunIf runIfCondition;

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
