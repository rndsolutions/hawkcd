package net.hawkengine.core.materialupdater;

import net.hawkengine.model.GitMaterial;

public interface IGitService {
    boolean shouldCloneRepository(GitMaterial materialDefinition);

    boolean cloneRepository(GitMaterial materialDefinition);
}
