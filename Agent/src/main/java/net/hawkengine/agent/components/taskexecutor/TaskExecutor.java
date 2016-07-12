package net.hawkengine.agent.components.taskexecutor;

import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.payload.WorkInfo;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;

public abstract class TaskExecutor implements ITaskExecutor {
    protected static final Logger LOGGER = LogManager.getLogger(TaskExecutor.class);

    @Override
    public Task executeTask(Task task, StringBuilder report, WorkInfo workInfo) {
        return task;
    }

    protected void updateTask(Task task, TaskStatus status, LocalDateTime startDateTime, LocalDateTime endDateTime) {
//        if (startDateTime != null) {
//            task.setStartTime(startDateTime);
//        }
//
//        if (endDateTime != null) {
//            task.setEndTime(endDateTime);
//        }

        task.setStartTime(null);


        task.setEndTime(null);

        task.setStatus(status);
    }
}
