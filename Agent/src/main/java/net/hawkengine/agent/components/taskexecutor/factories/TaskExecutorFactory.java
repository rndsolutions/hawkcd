package net.hawkengine.agent.components.taskexecutor.factories;

import net.hawkengine.agent.components.taskexecutor.TaskExecutor;
import net.hawkengine.agent.components.taskexecutor.executors.ExecTaskExecutor;
import net.hawkengine.agent.components.taskexecutor.executors.FetchArtifactExecutor;
import net.hawkengine.agent.components.taskexecutor.executors.UploadArtifactExecutor;
import net.hawkengine.agent.models.TaskDefinition;

public class TaskExecutorFactory {

    public static TaskExecutor create(TaskDefinition task)
    {
        switch (task.getType())
        {
            case EXEC:
                return new ExecTaskExecutor();
            case FETCH_MATERIAL:
                return FetchMaterialExecutorFactory.create(task);
            case FETCH_ARTIFACT:
                return new FetchArtifactExecutor();
            case UPLOAD_ARTIFACT:
                return new UploadArtifactExecutor();
            default:
                return null;
        }
    }
}
