package net.hawkengine.agent.services;

import net.hawkengine.agent.AgentConfiguration;
import net.hawkengine.agent.models.FetchMaterialTask;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

public class GitMaterialService extends MaterialService {

    @Override
    public String fetchMaterial(FetchMaterialTask task) {
        String errorMessage = null;

        String materialPath = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDir(), task.getPipelineName(), task.getDestination()).toString();

        CloneCommand clone = Git.cloneRepository();
        clone.setURI(task.getSource());
        clone.setBranch(task.getMaterialSpecificDetails().get("branch").toString());
        clone.setDirectory(new File(materialPath));
        clone.setCloneSubmodules(true);
        UsernamePasswordCredentialsProvider credentials = this.handleCredentials(task.getMaterialSpecificDetails());
        clone.setCredentialsProvider(credentials);
        try {
            Git git = clone.call();
            git.close();
        } catch (GitAPIException e) {
            errorMessage = e.getMessage();
        }

        return errorMessage;
    }

    private UsernamePasswordCredentialsProvider handleCredentials(Map<String, Object> materialSpecificDetails) {
        UsernamePasswordCredentialsProvider credentials = null;
        if (materialSpecificDetails.containsKey("username") && materialSpecificDetails.containsKey("password")) {
            String username = materialSpecificDetails.get("username").toString();
            String password = super.securityService.decrypt(materialSpecificDetails.get("password").toString());

            credentials = new UsernamePasswordCredentialsProvider(username, password);
        }

        return credentials;
    }
}
