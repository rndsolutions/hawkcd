package net.hawkengine.agent.services;

import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.models.FetchMaterialTask;
import net.hawkengine.agent.models.GitMaterial;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.nio.file.Paths;

public class GitMaterialService extends MaterialService {

    @Override
    public String fetchMaterial(FetchMaterialTask task) {
        String errorMessage = null;
        String materialPath = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDir(), task.getPipelineName(), task.getDestination()).toString();
        GitMaterial definition = (GitMaterial) task.getMaterialDefinition();
        CloneCommand clone = Git.cloneRepository();
        clone.setURI(definition.getRepositoryUrl());
        clone.setBranch(definition.getBranch());
        clone.setDirectory(new File(materialPath));
        clone.setCloneSubmodules(true);
        UsernamePasswordCredentialsProvider credentials = this.handleCredentials(definition);
        clone.setCredentialsProvider(credentials);
        try {
            Git git = clone.call();
            git.close();
        } catch (GitAPIException e) {
            errorMessage = e.getMessage();
        }

        return errorMessage;
    }

    private UsernamePasswordCredentialsProvider handleCredentials(GitMaterial definition) {
        UsernamePasswordCredentialsProvider credentials = null;
        if (definition.getUsername() != null && definition.getPassword() != null) {
            String username = definition.getUsername();
            //String password = super.securityService.decrypt(materialSpecificDetails.get("password").toString());
            String password = definition.getPassword();
            credentials = new UsernamePasswordCredentialsProvider(username, password);
        }

        return credentials;
    }
}
