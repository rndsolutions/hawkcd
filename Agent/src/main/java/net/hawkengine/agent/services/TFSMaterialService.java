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

package net.hawkengine.agent.services;

import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import net.hawkengine.agent.models.FetchMaterialTask;
import net.hawkengine.model.MaterialDefinition;

import java.util.Map;

public class TFSMaterialService extends MaterialService {

    @Override
    public String fetchMaterial(FetchMaterialTask task) {
        String errorMessage = "";

//        HTTPBasicAuthFilter credentials = this.handleCredentials(task.getMaterialSpecificDetails());
//        if (credentials != null) {
//            super.restClient.addFilter(credentials);
//        }
//
//        String resource = this.constructResource(task.getMaterialSpecificDetails());
//        WebResource webResource = super.restClient.resource(resource);
//        ClientResponse response = webResource.accept("application/zip").get(ClientResponse.class);
//
//        int responseCode = response.getStatus();
//        if (responseCode == 200) {
//
//            String filePath = Paths.get(AgentConfiguration.getInstallInfo().getAgentTempDirectoryPath(), UUID.randomUUID() + ".zip").toString();
//            errorMessage = super.fileManagementService.streamToFile(response.getEntityInputStream(), filePath);
//            if (errorMessage != null) {
//                return errorMessage;
//            }
//
//            String destination = Paths.get(AgentConfiguration.getInstallInfo().getAgentPipelinesDir(), task.getPipelineName(), task.getDestination()).toString();
//            errorMessage = super.fileManagementService.unzipFile(filePath, destination);
//            super.fileManagementService.deleteFile(filePath);
//            if (errorMessage != null) {
//                return errorMessage;
//            }
//        } else {
//            errorMessage = this.generateErrorMessage(responseCode);
//        }

        return errorMessage;
    }

    private HTTPBasicAuthFilter handleCredentials(MaterialDefinition definition) {

        HTTPBasicAuthFilter credentials = null;
//
//        if (definition.getUsername() != null && definition.getPassword() != null) {
//            String username = materialSpecificDetails.get("username").toString();
//            String password = super.securityService.decrypt(materialSpecificDetails.get("password").toString());
//
//            credentials = new HTTPBasicAuthFilter(username, password);
//        }

        return credentials;
    }

    private String constructResource(Map<String, Object> materialSpecificDetails) {

        String resource = null;

        if (materialSpecificDetails.containsKey("domain") && materialSpecificDetails.containsKey("projectPath")) {
            String instance = materialSpecificDetails.get("domain").toString();
            String path = materialSpecificDetails.get("projectPath").toString();

            resource = String.format(
                    "https://%s.visualstudio.com/defaultcollection/_apis/tfvc/items/%s&api-version=1.0",
                    instance,
                    path);
        }

        return resource;
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