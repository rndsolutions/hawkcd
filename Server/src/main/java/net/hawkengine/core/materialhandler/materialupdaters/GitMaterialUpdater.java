package net.hawkengine.core.materialhandler.materialupdaters;

import net.hawkengine.core.materialhandler.materialservices.GitService;
import net.hawkengine.core.materialhandler.materialservices.IGitService;
import net.hawkengine.model.GitMaterial;

public class GitMaterialUpdater extends MaterialUpdater<GitMaterial> {
    private IGitService gitService;

    public GitMaterialUpdater() {
        this.gitService = new GitService();
    }

    public GitMaterialUpdater(IGitService gitService) {
        this.gitService = gitService;
    }

    @Override
    public GitMaterial getLatestMaterialVersion(GitMaterial gitMaterial) {
        boolean repositoryExists = this.gitService.repositoryExists(gitMaterial);
        if (!repositoryExists) {
            // TODO: Clean directory
            this.gitService.cloneRepository(gitMaterial);
            if (!gitMaterial.getErrorMessage().isEmpty()) {
                // TODO: Clean directory
                // TODO: Send error to UI
                return gitMaterial;
            }
        }

        this.gitService.fetchLatestCommit(gitMaterial);
        if (!gitMaterial.getErrorMessage().isEmpty()) {
            // TODO: Send error to UI
        }

        return gitMaterial;
    }

    @Override
    public boolean areMaterialsSameVersion(GitMaterial latestMaterial, GitMaterial dbMaterial) {
        boolean areSameVersion;
        if (dbMaterial != null) {
            areSameVersion = latestMaterial.getCommitId().equals(dbMaterial.getCommitId());
        } else {
            areSameVersion = false;
        }

        return areSameVersion;
    }
}
