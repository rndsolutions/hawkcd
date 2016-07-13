package net.hawkengine.core.materialhandler.materialservices;

import net.hawkengine.model.GitMaterial;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;

import java.io.File;
import java.io.IOException;

public class GitService implements IGitService {
    @Override
    public boolean repositoryExists(GitMaterial gitMaterial) {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        File gitDirectory = repositoryBuilder.findGitDir(new File(gitMaterial.getName())).getGitDir();

        return gitDirectory != null ? true : false;
    }

    @Override
    public String cloneRepository(GitMaterial gitMaterial) {
        try {
            Git.cloneRepository()
                    .setURI(gitMaterial.getRepositoryUrl())
                    .setDirectory(new File(gitMaterial.getName()))
                    .setCloneSubmodules(true)
                    .call();

            return null;
        } catch (GitAPIException e) {
            return e.getMessage();
        }
    }

    @Override
    public GitMaterial fetchLatestCommit(GitMaterial gitMaterial) {
        try {
            Git git = Git.open(new File(gitMaterial.getName() + File.separator + ".git"));
            git.fetch()
                    .setCheckFetchedObjects(true)
                    .setRefSpecs(new RefSpec("refs/heads/" + gitMaterial.getBranch() + ":refs/heads/" + gitMaterial.getBranch()))
                    .call();
            ObjectId objectId = git.getRepository().getRef(gitMaterial.getBranch()).getObjectId();
            RevWalk revWalk = new RevWalk(git.getRepository());
            RevCommit commit = revWalk.parseCommit(objectId);

            gitMaterial.setCommitId(commit.getId().getName());
            gitMaterial.setAuthorName(commit.getAuthorIdent().getName());
            gitMaterial.setAuthorEmail(commit.getAuthorIdent().getEmailAddress());
            gitMaterial.setComments(commit.getFullMessage());

            return gitMaterial;
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            return null;
        }
    }
}
