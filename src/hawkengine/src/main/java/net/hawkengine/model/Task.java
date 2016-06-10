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
        return id;
    }

    public void setId(UUID value) {
        id = value;
    }

    public RunIf getRunIfCondition() {
        return runIfCondition;
    }

    public void setRunIfCondition(RunIf value) {
        runIfCondition = value;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType value) {
        type = value;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status value) {
        status = value;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String value) {
        result = value;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date value) {
        start = value;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date value) {
        end = value;
    }

}
