package net.hawkengine.core.materialupdater;

import net.hawkengine.model.GitMaterial;
import net.hawkengine.model.MaterialDefinition;

public class GitMaterialUpdater extends MaterialUpdater<GitMaterial> {
    private IGitService gitService;

    public GitMaterialUpdater() {
        this.gitService = new GitService();
    }

    public GitMaterialUpdater(IGitService gitService) {
        this.gitService = gitService;
    }

    @Override
    public MaterialDefinition getLatestMaterialVersion(GitMaterial gitMaterial) {
        boolean repositoryExists = this.gitService.repositoryExists(gitMaterial);
        if (!repositoryExists) {
            String cloneResult = this.gitService.cloneRepository(gitMaterial);
            if (cloneResult != null) {
                // TODO: Clean directory
                // TODO: Send error to UI
                return null;
            }

        }

        gitMaterial = this.gitService.fetchLatestCommit(gitMaterial);
        if (gitMaterial == null) {
            // TODO: Clean directory
            // TODO: Send error to UI
            return null;
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
