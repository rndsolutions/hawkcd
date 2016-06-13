package net.hawkengine.model;

import java.util.Date;
import java.util.UUID;

public abstract class Task {
    private UUID id;
    private RunIf runIfCondition = RunIf.PASSED;
    private TaskType type = TaskType.EXEC;
    private Status status = Status.PASSED;
    private String result;
    private Date start;
    private Date end;

    public Task() throws Exception {
        this.setId(UUID.randomUUID());
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID value) {
        this.id = value;
    }

    public RunIf getRunIfCondition() {
        return this.runIfCondition;
    }

    public void setRunIfCondition(RunIf value) {
        this.runIfCondition = value;
    }

    public TaskType getType() {
        return this.type;
    }

    public void setType(TaskType value) {
        this.type = value;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status value) {
        this.status = value;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String value) {
        this.result = value;
    }

    public Date getStart() {
        return this.start;
    }

    public void setStart(Date value) {
        this.start = value;
    }

    public Date getEnd() {
        return this.end;
    }

    public void setEnd(Date value) {
        this.end = value;
    }

}
