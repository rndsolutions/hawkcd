package net.hawkengine.core.materialhandler.materialupdaters;

import net.hawkengine.core.materialhandler.materialservices.GitService;
import net.hawkengine.core.materialhandler.materialservices.IGitService;
import net.hawkengine.model.GitMaterial;
import net.hawkengine.services.FileManagementService;
import net.hawkengine.services.interfaces.IFileManagementService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class GitMaterialUpdaterTest {
    private IMaterialUpdater<GitMaterial> gitMaterialUpdater;
    private IGitService mockedGitService;
    private IFileManagementService mockedFileManagementService;

    @Before
    public void setUp() throws Exception {
        this.mockedGitService = Mockito.mock(GitService.class);
        this.mockedFileManagementService = Mockito.mock(FileManagementService.class);
        this.gitMaterialUpdater = new GitMaterialUpdater(this.mockedGitService, this.mockedFileManagementService);
    }

    @Test
    public void gitMaterialUpdater_instantiated_notNull() {
        // Act
        this.gitMaterialUpdater = new GitMaterialUpdater();

        // Assert
        Assert.assertNotNull(this.gitMaterialUpdater);
    }

    @Test
    public void getLatestMaterialVersion_successfullyFetchedLatest_allFieldsUpdated() {
        // Arrange
        GitMaterial expectedResult = new GitMaterial();
        expectedResult.setCommitId("commitId");
        expectedResult.setAuthorName("authorName");
        expectedResult.setAuthorEmail("authorEmail");
        expectedResult.setComments("comment");
        expectedResult.setErrorMessage("");
        Mockito.when(this.mockedGitService.repositoryExists(expectedResult)).thenReturn(true);
        Mockito.when(this.mockedGitService.fetchLatestCommit(expectedResult)).thenReturn(expectedResult);

        // Act
        GitMaterial actualResult = this.gitMaterialUpdater.getLatestMaterialVersion(expectedResult);

        // Assert
        Assert.assertEquals(actualResult.getId(), expectedResult.getId());
        Assert.assertNotNull(actualResult.getCommitId());
        Assert.assertNotNull(actualResult.getAuthorName());
        Assert.assertNotNull(actualResult.getAuthorEmail());
        Assert.assertNotNull(actualResult.getComments());
    }

    @Test
    public void getLatestMaterialVersion_couldNotCloneRepository_errorMessage() {
        // Arrange
        GitMaterial gitMaterial = new GitMaterial();
        gitMaterial.setErrorMessage("errorMessage");
        Mockito.when(this.mockedGitService.repositoryExists(gitMaterial)).thenReturn(false);
        Mockito.when(this.mockedFileManagementService.deleteDirectoryRecursively(Mockito.anyString())).thenReturn(null);
        Mockito.when(this.mockedGitService.cloneRepository(gitMaterial)).thenReturn(gitMaterial);
        String expectedResult = "errorMessage";

        // Act
        String actualResult = this.gitMaterialUpdater.getLatestMaterialVersion(gitMaterial).getErrorMessage();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getLatestMaterialVersion_couldNotFetchLatest_errorMessage() {
        // Arrange
        GitMaterial gitMaterial = new GitMaterial();
        gitMaterial.setErrorMessage("errorMessage");
        Mockito.when(this.mockedGitService.repositoryExists(gitMaterial)).thenReturn(true);
        Mockito.when(this.mockedGitService.fetchLatestCommit(gitMaterial)).thenReturn(gitMaterial);
        String expectedResult = "errorMessage";

        // Act
        String actualResult = this.gitMaterialUpdater.getLatestMaterialVersion(gitMaterial).getErrorMessage();

        // Assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void areMaterialsSameVersion_matchingCommitIds_true() {
        // Arrange
        GitMaterial latestMaterial = new GitMaterial();
        GitMaterial dbMaterial = new GitMaterial();
        latestMaterial.setCommitId("commitId");
        dbMaterial.setCommitId("commitId");

        // Act
        boolean actualResult = this.gitMaterialUpdater.areMaterialsSameVersion(latestMaterial, dbMaterial);

        // Assert
        Assert.assertTrue(actualResult);
    }

    @Test
    public void areMaterialsSameVersion_notMatchingCommitIds_false() {
        // Arrange
        GitMaterial latestMaterial = new GitMaterial();
        GitMaterial dbMaterial = new GitMaterial();
        latestMaterial.setCommitId("commitId");
        dbMaterial.setCommitId("otherCommitId");

        // Act
        boolean actualResult = this.gitMaterialUpdater.areMaterialsSameVersion(latestMaterial, dbMaterial);

        // Assert
        Assert.assertFalse(actualResult);
    }

    @Test
    public void areMaterialsSameVersion_dbMaterialIsNull_false() {
        // Arrange
        GitMaterial latestMaterial = new GitMaterial();
        GitMaterial dbMaterial = null;
        latestMaterial.setCommitId("commitId");

        // Act
        boolean actualResult = this.gitMaterialUpdater.areMaterialsSameVersion(latestMaterial, dbMaterial);

        // Assert
        Assert.assertFalse(actualResult);
    }
}