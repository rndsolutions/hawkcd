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

package io.hawkcd.materials.materialservices;

import io.hawkcd.core.config.Config;
import io.hawkcd.model.GitMaterial;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
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
    private static final String MATERIALS_FOLDER = Config.getConfiguration().getMaterialsDestination();

    @Override
    public boolean repositoryExists(GitMaterial gitMaterial) {
        try {
            Repository repository = Git.open(new File(gitMaterial.getDestination())).getRepository();
            org.eclipse.jgit.lib.Config config = repository.getConfig();
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
