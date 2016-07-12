package net.hawkengine.agent.components.taskexecutor.executors;

import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.base.TestBase;
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

import java.lang.reflect.Constructor;

public class FetchMaterialExecutorTest extends TestBase {

    private Task correctFetchMaterialTask;
    private Task incorrectFetchMaterialTask;
    private Task secondIncorrectFetchMaterialTask;
    private WorkInfo workInfo;
    private TaskExecutor fetchMaterialExecutor;
    private IMaterialService mockedGitService;
    private IFileManagementService mockedFileManagementService;
    private String succcessReportMessage = "Start to update materials.Material fetched at Pipelines\\%s.";
    private String errorReportMessage = "Start to update materials.Unable to clean directory Pipelines\\%s\\%s";
    private String secondErrorReportMessage = "Start to update materials.%s";

    @Before
    public void setUp() throws Exception {
        AgentConfiguration.configure();

        this.mockedGitService = Mockito.mock(GitMaterialService.class);
        this.mockedFileManagementService = Mockito.mock(FileManagementService.class);
        this.fetchMaterialExecutor = new FetchMaterialExecutor(this.mockedGitService, this.mockedFileManagementService);
        setupData();
        Mockito.when(this.mockedGitService.fetchMaterial((FetchMaterialTask) this.correctFetchMaterialTask.getTaskDefinition())).thenReturn(null);
        Mockito.when(this.mockedGitService.fetchMaterial((FetchMaterialTask) this.secondIncorrectFetchMaterialTask.getTaskDefinition())).thenReturn("Error in Git Service");
        Mockito.when(this.mockedGitService.fetchMaterial((FetchMaterialTask) this.incorrectFetchMaterialTask.getTaskDefinition())).thenReturn("Error in Git service");

        Mockito.when(this.mockedFileManagementService.deleteDirectoryRecursively("Pipelines\\Wrong\\Wrong")).thenReturn("Error in FileSystem service");
        Mockito.when(this.mockedFileManagementService.deleteDirectoryRecursively("Pipelines\\MyPipe")).thenReturn(null);
        Mockito.when(this.mockedFileManagementService.deleteDirectoryRecursively("\\")).thenReturn(null);

    }

    private void setupData() {
        this.correctFetchMaterialTask = new Task();
        this.correctFetchMaterialTask.setType(TaskType.FETCH_MATERIAL);
        GitMaterial correctMaterialDefinition = new GitMaterial();
        FetchMaterialTask fetchMaterialTaskDefinition = new FetchMaterialTask();
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

    @Test
    public void FetchMaterialExecutor_validTask_success() throws Exception {
        StringBuilder report = new StringBuilder();
        Task myTask = this.fetchMaterialExecutor.executeTask(this.correctFetchMaterialTask, report, this.workInfo);
        String resultMessage = String.format(this.succcessReportMessage, "MyPipe");

        Assert.assertEquals(myTask.getStatus(), TaskStatus.PASSED);
        Assert.assertEquals(resultMessage, report.toString());
        Mockito.verify(this.mockedGitService, Mockito.times(1)).fetchMaterial((FetchMaterialTask) this.correctFetchMaterialTask.getTaskDefinition());
        Mockito.verify(this.mockedFileManagementService, Mockito.times(1)).deleteDirectoryRecursively("Pipelines\\MyPipe");
    }

    @Test
    public void FetchMaterialExecutor_InvalidTask_failure() throws Exception {
        StringBuilder report = new StringBuilder();
        Task myTask = this.fetchMaterialExecutor.executeTask(this.incorrectFetchMaterialTask, report, this.workInfo);
        String resultMessage = String.format(this.errorReportMessage, "Wrong","Wrong");

        Assert.assertEquals(myTask.getStatus(), TaskStatus.FAILED);
        Assert.assertEquals(resultMessage, report.toString());
        Mockito.verify(this.mockedGitService, Mockito.times(0)).fetchMaterial((FetchMaterialTask) this.incorrectFetchMaterialTask.getTaskDefinition());
        Mockito.verify(this.mockedFileManagementService, Mockito.times(1)).deleteDirectoryRecursively("Pipelines\\Wrong\\Wrong");
    }

    @Test
    public void FetchMaterialExecutor_InvalidTask_(){
        StringBuilder report = new StringBuilder();
        Task myTask = this.fetchMaterialExecutor.executeTask(this.secondIncorrectFetchMaterialTask, report, this.workInfo);
        String resultMessage = workInfo.getJob().getReport().toString();

        Assert.assertEquals(myTask.getStatus(), TaskStatus.FAILED);
        Assert.assertEquals(resultMessage, report.toString());
        Mockito.verify(this.mockedGitService, Mockito.times(1)).fetchMaterial((FetchMaterialTask) this.secondIncorrectFetchMaterialTask.getTaskDefinition());
        Mockito.verify(this.mockedFileManagementService, Mockito.times(1)).deleteDirectoryRecursively("Pipelines");
    }

    @Test
    public void testInitialConstructor(){
        FetchMaterialExecutor exec = new FetchMaterialExecutor(this.mockedGitService);
        Assert.assertNotNull(exec);
    }
}