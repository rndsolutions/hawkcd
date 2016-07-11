package net.hawkengine.agent.models.payload;

import net.hawkengine.agent.enums.ExecutionState;
import net.hawkengine.agent.enums.ExecutionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class TaskExecutionInfo {
    private String taskId;
    private ExecutionState state;
    private ExecutionStatus status;
    private LocalDateTime start;
    private LocalDateTime end;
    private String result;
    private boolean ignoreErrors;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public ExecutionState getState() {
        return state;
    }

    public void setState(ExecutionState state) {
        this.state = state;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isIgnoreErrors() {
        return ignoreErrors;
    }

    public void setIgnoreErrors(boolean ignoreErrors) {
        this.ignoreErrors = ignoreErrors;
    }
}
