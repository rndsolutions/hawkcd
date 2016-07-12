package net.hawkengine.agent.components.taskexecutor;

import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.payload.WorkInfo;

public interface ITaskExecutor {
    Task executeTask(Task task, StringBuilder report, WorkInfo workInfo);
}
