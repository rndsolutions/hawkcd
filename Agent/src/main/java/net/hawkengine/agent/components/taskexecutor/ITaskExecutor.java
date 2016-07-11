package net.hawkengine.agent.components.taskexecutor;

import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.TaskDefinition;
import net.hawkengine.agent.models.payload.JobExecutionInfo;
import net.hawkengine.agent.models.payload.TaskExecutionInfo;
import net.hawkengine.agent.models.payload.WorkInfo;

public interface ITaskExecutor {
    Task executeTask(Task task, StringBuilder report, WorkInfo workInfo);
}
