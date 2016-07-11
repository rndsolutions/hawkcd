package net.hawkengine.core.materialupdater;

import net.hawkengine.model.GitMaterial;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class GitService implements IGitService {
    @Override
    public boolean shouldCloneRepository(GitMaterial materialDefinition) {
        try {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            File gitDirectory = new File(materialDefinition.getName() + File.separator + ".git");
            repositoryBuilder.setMustExist(true).setGitDir(gitDirectory);
            Repository repository = repositoryBuilder.build();
            Ref head = repository.getRef("HEAD");
            if (head != null) {
                return false;
            }
        } catch (IOException e) {
            return true;
        }

        return true;
    }

    @Override
    public boolean cloneRepository(GitMaterial materialDefinition) {
        try {
            Git.cloneRepository()
                    .setURI(materialDefinition.getRepositoryUrl())
                    .setDirectory(new File(materialDefinition.getName()))
                    .call();

            return true;
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return false;
    }
}
