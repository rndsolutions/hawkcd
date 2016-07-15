package net.hawkengine.agent.components.taskexecutor.executors;

import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.components.taskexecutor.TaskExecutor;
import net.hawkengine.agent.enums.TaskStatus;
import net.hawkengine.agent.models.FetchMaterialTask;
import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.payload.WorkInfo;
import net.hawkengine.agent.services.FileManagementService;
import net.hawkengine.agent.services.interfaces.IFileManagementService;
import net.hawkengine.agent.services.interfaces.IMaterialService;

import java.nio.file.Paths;
import java.time.LocalDateTime;

public class FetchMaterialExecutor extends TaskExecutor {
    private IMaterialService materialService;
    private IFileManagementService fileManagementService;

    public FetchMaterialExecutor(IMaterialService materialService) {
        this.materialService = materialService;
        this.fileManagementService = new FileManagementService();
    }

    public FetchMaterialExecutor(IMaterialService materialService, IFileManagementService fileManagementService) {
        this.materialService = materialService;
        this.fileManagementService = fileManagementService;
    }

    @Override
    public Task executeTask(Task task, StringBuilder report, WorkInfo workInfo) {

        FetchMaterialTask taskDefinition = (FetchMaterialTask) task.getTaskDefinition();

        this.updateTask(task, TaskStatus.PASSED, LocalDateTime.now(), null);

        report.append("Fetching material...");

        String materialPath = Paths.get(
                AgentConfiguration.getInstallInfo().getAgentPipelinesDir(),
                taskDefinition.getPipelineName(),
                taskDefinition.getDestination())
                .toString();
        String errorMessage = this.fileManagementService.deleteDirectoryRecursively(materialPath);

        if (errorMessage != null) {
           return this.NullProcessing(report,task,String.format("Unable to clean directory %s", materialPath));
        }

        errorMessage = this.materialService.fetchMaterial(taskDefinition);

        if (errorMessage == null) {
            this.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());

            report.append(String.format("Material fetched at %s", materialPath));
        } else {
           this.NullProcessing(report,task,errorMessage);
        }

        workInfo.getJob().setReport(report);

        return task;
    }
}
