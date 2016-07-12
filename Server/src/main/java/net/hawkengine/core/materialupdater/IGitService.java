package net.hawkengine.core.materialupdater;

import net.hawkengine.model.GitMaterial;

public interface IGitService {
    boolean repositoryExists(GitMaterial gitMaterial);

    String cloneRepository(GitMaterial gitMaterial);

    GitMaterial fetchLatestCommit(GitMaterial gitMaterial);
}
