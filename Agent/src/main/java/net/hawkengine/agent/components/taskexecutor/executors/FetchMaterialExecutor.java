/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import net.hawkengine.agent.utilities.ReportAppender;

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

        FetchMaterialTask fetchMaterialTask = (FetchMaterialTask) task.getTaskDefinition();

        this.updateTask(task, TaskStatus.PASSED, LocalDateTime.now(), null);

        String fetchingMessage = String.format("Fetching Material %s", fetchMaterialTask.getMaterialName());
        LOGGER.debug(fetchingMessage);
        ReportAppender.appendInfoMessage(fetchingMessage, report);

        String materialPath = Paths.get(
                AgentConfiguration.getInstallInfo().getAgentPipelinesDir(),
                fetchMaterialTask.getPipelineName(),
                fetchMaterialTask.getDestination())
                .toString();
        String errorMessage = this.fileManagementService.deleteDirectoryRecursively(materialPath);

        if (errorMessage != null) {
            return this.nullProcessing(report, task, String.format("Unable to clean directory %s", materialPath));
        }

        errorMessage = this.materialService.fetchMaterial(fetchMaterialTask);

        if (errorMessage == null) {
            this.updateTask(task, TaskStatus.PASSED, null, LocalDateTime.now());

            String fetchedMessage = String.format("Material fetched at %s", materialPath);
            LOGGER.debug(fetchedMessage);
            ReportAppender.appendInfoMessage(fetchedMessage, report);
        } else {
            this.nullProcessing(report, task, errorMessage);
        }

        return task;
    }
}
