package net.hawkengine.agent.components.taskexecutor.factories;

import net.hawkengine.agent.components.taskexecutor.executors.FetchMaterialExecutor;
import net.hawkengine.agent.models.FetchMaterialTask;
import net.hawkengine.agent.models.TaskDefinition;
import net.hawkengine.agent.services.GitMaterialService;
import net.hawkengine.agent.services.NuGetMaterialService;
import net.hawkengine.agent.services.TFSMaterialService;

public class FetchMaterialExecutorFactory {
    public static FetchMaterialExecutor create(TaskDefinition task) {
        FetchMaterialTask taskDefinition = (FetchMaterialTask) task;
        switch (taskDefinition.getMaterialType()) {
            case GIT:
                return new FetchMaterialExecutor(new GitMaterialService());
            case TFS:
                return new FetchMaterialExecutor(new TFSMaterialService());
            case NUGET:
                return new FetchMaterialExecutor(new NuGetMaterialService());
            default:
                return null;
        }
    }
}
