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

package net.hawkengine.core.materialhandler.materialupdaters;

import net.hawkengine.core.materialhandler.materialservices.GitService;
import net.hawkengine.core.materialhandler.materialservices.IGitService;
import net.hawkengine.model.GitMaterial;
import net.hawkengine.services.FileManagementService;
import net.hawkengine.services.interfaces.IFileManagementService;

public class GitMaterialUpdater extends MaterialUpdater<GitMaterial> {
    private IGitService gitService;
    private IFileManagementService fileManagementService;

    public GitMaterialUpdater() {
        this.gitService = new GitService();
        this.fileManagementService = new FileManagementService();
    }

    public GitMaterialUpdater(IGitService gitService, IFileManagementService fileManagementService) {
        this.gitService = gitService;
        this.fileManagementService = fileManagementService;
    }

    @Override
    public GitMaterial getLatestMaterialVersion(GitMaterial gitMaterial) {
        boolean repositoryExists = this.gitService.repositoryExists(gitMaterial);
        if (!repositoryExists) {
//            String directoryToDelete = ServerConfiguration.getConfiguration().getMaterialsDestination() + File.separator + gitMaterial.getName();
            this.fileManagementService.deleteDirectoryRecursively(gitMaterial.getDestination());
            this.gitService.cloneRepository(gitMaterial);

            if (!gitMaterial.getErrorMessage().isEmpty()) {
                this.fileManagementService.deleteDirectoryRecursively(gitMaterial.getDestination());
                return gitMaterial;
            }
        }

        this.gitService.fetchLatestCommit(gitMaterial);

        return gitMaterial;
    }

    @Override
    public boolean areMaterialsSameVersion(GitMaterial latestMaterial, GitMaterial dbMaterial) {
        boolean areSameVersion = false;
        if (dbMaterial != null) {
            areSameVersion = latestMaterial.getCommitId().equals(dbMaterial.getCommitId());
        }

        return areSameVersion;
    }
}
