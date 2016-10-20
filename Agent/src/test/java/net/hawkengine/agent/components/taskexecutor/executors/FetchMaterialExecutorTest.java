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
import net.hawkengine.agent.enums.TaskType;
import net.hawkengine.agent.models.FetchMaterialTask;
import net.hawkengine.agent.models.GitMaterial;
import net.hawkengine.agent.models.Job;
import net.hawkengine.agent.models.Task;
import net.hawkengine.agent.models.payload.WorkInfo;
import net.hawkengine.agent.services.FileManagementService;
import net.hawkengine.agent.services.GitMaterialService;
import net.hawkengine.agent.services.interfaces.IFileManagementService;
import net.hawkengine.agent.services.interfaces.IMaterialService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

public class FetchMaterialExecutorTest {
    private TaskExecutor fetchMaterialExecutor;
    private IMaterialService mockedGitService;
    private IFileManagementService mockedFileManagementService;
    private Task correctFetchMaterialTask;
    private Task incorrectFetchMaterialTask;
    private Task secondIncorrectFetchMaterialTask;
    private WorkInfo workInfo;
    private String correctDirectoryPath = "Pipelines" + File.separator + "MyPipe";
    private String wrongDirectoryPath = "Pipelines" + File.separator + "Wrong" + File.separator + "Wrong";

    @Before
    public void setUp() {
        AgentConfiguration.configure();

        this.mockedGitService = Mockito.mock(GitMaterialService.class);
        this.mockedFileManagementService = Mockito.mock(FileManagementService.class);
        this.fetchMaterialExecutor = new FetchMaterialExecutor(this.mockedGitService, this.mockedFileManagementService);
        this.setupData();
        Mockito.when(this.mockedGitService.fetchMaterial((FetchMaterialTask) this.correctFetchMaterialTask.getTaskDefinition())).thenReturn(null);
        Mockito.when(this.mockedGitService.fetchMaterial((FetchMaterialTask) this.secondIncorrectFetchMaterialTask.getTaskDefinition())).thenReturn("Error in Git Service");
        Mockito.when(this.mockedGitService.fetchMaterial((FetchMaterialTask) this.incorrectFetchMaterialTask.getTaskDefinition())).thenReturn("Error in Git service");

        Mockito.when(this.mockedFileManagementService.deleteDirectoryRecursively(this.wrongDirectoryPath)).thenReturn("Error in FileSystem service");
        Mockito.when(this.mockedFileManagementService.deleteDirectoryRecursively(this.correctDirectoryPath)).thenReturn(null);
        Mockito.when(this.mockedFileManagementService.deleteDirectoryRecursively(File.separator)).thenReturn(null);
    }

    @Test
    public void fetchMaterialExecutor_instantiated_notNull() {
        FetchMaterialExecutor fetchMaterialExecutor = new FetchMaterialExecutor(this.mockedGitService);
        Assert.assertNotNull(fetchMaterialExecutor);
    }

    @Test
    public void executeTask_validTask_taskPassed() {
        // Arrange
        StringBuilder report = new StringBuilder();
        TaskStatus expectedStatus = TaskStatus.PASSED;

        // Act
        Task actualResult = this.fetchMaterialExecutor.executeTask(this.correctFetchMaterialTask, report, this.workInfo);

        // Assert
        Assert.assertEquals(actualResult.getStatus(), expectedStatus);
        Mockito.verify(this.mockedGitService, Mockito.times(1)).fetchMaterial((FetchMaterialTask) this.correctFetchMaterialTask.getTaskDefinition());
        Mockito.verify(this.mockedFileManagementService, Mockito.times(1)).deleteDirectoryRecursively(this.correctDirectoryPath);
    }

    @Test
    public void executeTask_directoryCleanFailed_taskFailed() {
        // Arrange
        StringBuilder report = new StringBuilder();

        // Act
        Task actualResult = this.fetchMaterialExecutor.executeTask(this.incorrectFetchMaterialTask, report, this.workInfo);

        // Assert
        Assert.assertEquals(actualResult.getStatus(), TaskStatus.FAILED);
        Mockito.verify(this.mockedGitService, Mockito.times(0)).fetchMaterial((FetchMaterialTask) this.incorrectFetchMaterialTask.getTaskDefinition());
        Mockito.verify(this.mockedFileManagementService, Mockito.times(1)).deleteDirectoryRecursively(this.wrongDirectoryPath);
    }

    @Test
    public void executeTask_fetchMaterialFailed_taskFailed() {
        // Arrange
        StringBuilder report = new StringBuilder();

        // Act
        Task actualResult = this.fetchMaterialExecutor.executeTask(this.secondIncorrectFetchMaterialTask, report, this.workInfo);

        // Assert
        Assert.assertEquals(actualResult.getStatus(), TaskStatus.FAILED);
        Mockito.verify(this.mockedGitService, Mockito.times(1)).fetchMaterial((FetchMaterialTask) this.secondIncorrectFetchMaterialTask.getTaskDefinition());
        Mockito.verify(this.mockedFileManagementService, Mockito.times(1)).deleteDirectoryRecursively("Pipelines");
    }

    private void setupData() {
        this.correctFetchMaterialTask = new Task();
        this.correctFetchMaterialTask.setType(TaskType.FETCH_MATERIAL);
        GitMaterial correctMaterialDefinition = new GitMaterial();
        FetchMaterialTask fetchMaterialTaskDefinition = new FetchMaterialTask();
        fetchMaterialTaskDefinition.setMaterialName("TestMaterial");
        fetchMaterialTaskDefinition.setMaterialDefinition(correctMaterialDefinition);
        fetchMaterialTaskDefinition.setType(TaskType.FETCH_MATERIAL);
        fetchMaterialTaskDefinition.setPipelineName("MyPipe");
        fetchMaterialTaskDefinition.setDestination("");
        this.correctFetchMaterialTask.setTaskDefinition(fetchMaterialTaskDefinition);

        this.incorrectFetchMaterialTask = new Task();
        GitMaterial incorrectMaterialDefinition = new GitMaterial();
        this.incorrectFetchMaterialTask.setMaterialDefinition(incorrectMaterialDefinition);
        FetchMaterialTask incorrectFetchTaskDefinition = new FetchMaterialTask();
        incorrectFetchTaskDefinition.setPipelineName("Wrong");
        incorrectFetchTaskDefinition.setDestination("Wrong");
        this.incorrectFetchMaterialTask.setTaskDefinition(incorrectFetchTaskDefinition);

        this.secondIncorrectFetchMaterialTask = new Task();
        GitMaterial secondIncorrectFetchMaterialDefinition = new GitMaterial();
        this.incorrectFetchMaterialTask.setMaterialDefinition(secondIncorrectFetchMaterialDefinition);
        FetchMaterialTask secondIncorrectTaskDefinition = new FetchMaterialTask();
        secondIncorrectTaskDefinition.setPipelineName("");
        secondIncorrectTaskDefinition.setDestination("");
        this.secondIncorrectFetchMaterialTask.setTaskDefinition(secondIncorrectTaskDefinition);

        this.workInfo = new WorkInfo();
        this.workInfo.setJob(new Job());
    }
}