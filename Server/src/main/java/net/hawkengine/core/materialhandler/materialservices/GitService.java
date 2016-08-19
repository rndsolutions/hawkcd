package net.hawkengine.core.materialhandler.materialservices;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.model.GitMaterial;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

public class GitService implements IGitService {
    private static final String MATERIALS_FOLDER = ServerConfiguration.getConfiguration().getMaterialsDestination();

    @Override
    public boolean repositoryExists(GitMaterial gitMaterial) {
        try {
            Repository repository = Git.open(new File(gitMaterial.getDestination())).getRepository();
            Config config = repository.getConfig();
            String repositoryUrl = config.getString("remote", "origin", "url");
            if (!repositoryUrl.equals(gitMaterial.getRepositoryUrl())) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public GitMaterial cloneRepository(GitMaterial gitMaterial) {
        try {
            CredentialsProvider credentials = this.handleCredentials(gitMaterial);
            Git.cloneRepository()
                    .setURI(gitMaterial.getRepositoryUrl())
                    .setCredentialsProvider(credentials)
                    .setDirectory(new File(gitMaterial.getDestination()))
                    .setCloneSubmodules(true)
                    .call();

            gitMaterial.setErrorMessage("");

            return null;
        } catch (GitAPIException | JGitInternalException e) {
            gitMaterial.setErrorMessage(e.getMessage());
            return gitMaterial;
        }
    }

    @Override
    public GitMaterial fetchLatestCommit(GitMaterial gitMaterial) {
        try {
            Git git = Git.open(new File(gitMaterial.getDestination() + File.separator + ".git"));
            CredentialsProvider credentials = this.handleCredentials(gitMaterial);
            git.fetch()
                    .setCredentialsProvider(credentials)
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
            gitMaterial.setErrorMessage("");
            git.close();

            return gitMaterial;
        } catch (IOException | GitAPIException e) {
            gitMaterial.setErrorMessage(e.getMessage());
            return gitMaterial;
        }
    }

    private CredentialsProvider handleCredentials(GitMaterial gitMaterial) {
        UsernamePasswordCredentialsProvider credentials = null;
        String username = gitMaterial.getUsername();
        String password = gitMaterial.getPassword();
        if (username != null && password != null) {
            credentials = new UsernamePasswordCredentialsProvider(username, password);
        }

        return credentials;
    }
}
