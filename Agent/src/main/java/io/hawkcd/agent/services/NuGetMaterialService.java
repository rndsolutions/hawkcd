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

package io.hawkcd.agent.services;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import io.hawkcd.agent.AgentConfiguration;
import io.hawkcd.agent.models.FetchMaterialTask;
import io.hawkcd.agent.models.NugetMaterial;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

public class NuGetMaterialService extends MaterialService {

    @Override
    public String fetchMaterial(FetchMaterialTask task) {
        String errorMessage = "";

        NugetMaterial nugetMaterial = (NugetMaterial) task.getMaterialDefinition();

//        HTTPBasicAuthFilter credentials = this.handleCredentials(task.getMaterialSpecificDetails());
//        if (credentials != null) {
//            super.restClient.addFilter(credentials);
//        }

//        String resource = this.constructResource(task.getSource(), task.getMaterialSpecificDetails());
//        WebResource webResource = super.restClient.resource(resource);
//        ClientResponse response = webResource.get(ClientResponse.class);

//        int responseCode = response.getStatus();
//        if (responseCode == 200) {
//            String materialDir = this.createMaterialDir(task);
//            String fileName = task.getMaterialSpecificDetails().get("packageId") + "." + task.getMaterialSpecificDetails().get("revision");
//            String filePath = Paths.get(materialDir, fileName + ".nupkg").toString();
//            errorMessage = super.fileManagementService.streamToFile(response.getEntityInputStream(), filePath);
//            if (errorMessage != null) {
//                return errorMessage;
//            }
//        } else {
//            errorMessage = this.generateErrorMessage(responseCode);
//        }

        HTTPBasicAuthFilter credentials = this.handleCredentials(nugetMaterial.getUsername(), nugetMaterial.getPassword());

        if(credentials != null){
            super.restClient.addFilter(credentials);
        }

        String resource = this.constructResource(nugetMaterial);
        WebResource webResource = super.restClient.resource(resource);
        ClientResponse response = webResource.get(ClientResponse.class);

        int responseCode = response.getStatus();
        if(responseCode == 200){
            String materialDir = this.createMaterialDir(task);
            String fileName = nugetMaterial.getPackageId() + "." + nugetMaterial.getPackageVersion();
            String filePath = Paths.get(materialDir, fileName + ".nupkg").toString();
            File file = new File(filePath).getAbsoluteFile();
            errorMessage = this.fileManagementService.initiateFile(file, response.getEntityInputStream(), filePath);
            if (errorMessage != null) {
                return errorMessage;
            }
        } else {
            errorMessage = this.generateErrorMessage(responseCode);
        }

        return errorMessage;
    }

    private HTTPBasicAuthFilter handleCredentials(String username, String password) {

        HTTPBasicAuthFilter credentials = null;

        if(username != null && password != null){
            super.securityService.decrypt(password);
        }

        credentials = new HTTPBasicAuthFilter(username, password);

        return credentials;
//        HTTPBasicAuthFilter credentials = null;
//
//        if (materialSpecificDetails.containsKey("username") && materialSpecificDetails.containsKey("password")) {
//            String username = materialSpecificDetails.get("username").toString();
//            String password = super.securityService.decrypt(materialSpecificDetails.get("password").toString());
//
//            credentials = new HTTPBasicAuthFilter(username, password);
//        }
//
//        return credentials;
    }

    private String constructResource(NugetMaterial nugetMaterial) {
        String resource = null;

        if (nugetMaterial.getPackageId() != null && nugetMaterial.getPackageVersion() != null) {
            resource = String.format("%s/package/%s/%s", nugetMaterial.getRepositoryUrl(), nugetMaterial.getPackageId(), nugetMaterial.getPackageVersion());
        }

        return resource;
    }

    private String createMaterialDir(FetchMaterialTask task) {
        String materialDirPath = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDir(), task.getPipelineName(), task.getMaterialName()).toString();
        File materialDirFile = new File(materialDirPath);
        materialDirFile.mkdir();

        return materialDirPath;
    }

    private String generateErrorMessage(int responseCode) {
        switch (responseCode) {
            case 203:
            case 401:
                return "Invalid credentials.";
            case 404:
                return "Item not found.";
            default:
                return "Unknown error has occurred.";
        }
    }
}
